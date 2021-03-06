package com.lucky.ut.effective.data.category;

import com.lucky.ut.effective.annotation.MockIgnore;
import com.lucky.ut.effective.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author zhourj
 * @date 2020/9/18 12:40
 */
public class ObjectProvider<T> implements Provider<T> {
	Class<T> clazz;

	private static String STRING_TYPE = "java.lang.String";
	private static String INTEGER_TYPE = "java.lang.Integer";
	private static String LONG_TYPE = "java.lang.Long";
	private static String FLOAT_TYPE = "java.lang.Float";
	private static String DOUBLE_TYPE = "java.lang.Double";
	private static String BYTE_TYPE = "java.lang.Byte";
	private static String BOOLEAN_TYPE = "java.lang.Boolean";
	private static String LOCAL_DATE_TYPE = "java.time.LocalDate";
	private static String LOCAL_DATE_TIME_TYPE = "java.time.LocalDateTime";
	private static String LIST_TYPE = "java.util.List";
	private static String MAP_TYPE = "java.util.Map";
	private static String SET_TYPE = "java.util.Set";
	private static String ARRAY_PREFIX = "[L";

	public ObjectProvider(Class<T> clazz){
		this.clazz = clazz;
	}
	@Override
	public T generate() {
		T result = null;
		try {
			// 是特定对象的就直接生成
			result = (T) specificValue();
			if(result != null){
				return result;
			}
			// 自定义对象的就遍历对象中的属性，将内部属性依次初始化
			result = clazz.newInstance();
			setFieldValueByFieldAccessible(result);
		} catch (Exception e) {}
		return result;
	}

	/**
	 * 反射设置属性值
	 * @throws IllegalAccessException
	 */
	private void setFieldValueByFieldAccessible(Object result) throws IllegalAccessException , InstantiationException{
		for (Class<?> currentClass = clazz; currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
			// 模拟有setter方法的字段
			for (Field field :currentClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(MockIgnore.class)) {
					continue;
				}

				if (field.getName() != null && field.getName().equalsIgnoreCase("serialVersionUID")) {
					continue;
				}

				ReflectionUtils.setFieldValue(result, field, magicValue(field));
			}
		}
	}

	private Object specificValue(){
		// 基础对象
		if(clazz.getName().equalsIgnoreCase(STRING_TYPE)){
			return new StringProvider().generate();
		}
		if(clazz.getName().equalsIgnoreCase(INTEGER_TYPE)){
			return new IntegerProvider().generate();
		}
		if(clazz.getName().equalsIgnoreCase(LONG_TYPE)){
			return new LongProvider().generate();
		}
		if(clazz.getName().equalsIgnoreCase(FLOAT_TYPE)){
			return new FloatProvider().generate();
		}
		if(clazz.getName().equalsIgnoreCase(DOUBLE_TYPE)){
			return new DoubleProvider().generate();
		}
		if(clazz.getName().equalsIgnoreCase(BYTE_TYPE)){
			return new ByteProvider().generate();
		}
		if(clazz.getName().equalsIgnoreCase(BOOLEAN_TYPE)){
			return  BooleanProvider.generate();
		}
		if(clazz.getName().equalsIgnoreCase(LOCAL_DATE_TYPE)){
			return new LocalDateProvider().generate();
		}
		if(clazz.getName().equalsIgnoreCase(LOCAL_DATE_TIME_TYPE)){
			return new LocalDateTimeProvider().generate();
		}
		return null;
	};

	private Object magicValue(Field field){
		Type type = field.getGenericType();
		Class<?> clazz = field.getType();
		String fieldName = field.getType().getName();

		// 基础对象
		Object result = specificValue();
		if(result !=null){
			return result;
		}
		// 数组
		if(fieldName.startsWith(ARRAY_PREFIX)){
			return new ArrayProvider(clazz).generate();
		}
		// List
		if(clazz.getName().equalsIgnoreCase(LIST_TYPE)){
			// List<> 泛型
			if (type instanceof ParameterizedType) {
				// 获取泛型里面的具体对象类型
				Type[] params = ((ParameterizedType) type).getActualTypeArguments();
				return new ListProvider<List>((Class)params[0]).generate();
			}
			return new ArrayProvider(clazz).generate();
		}
		// Map
		if(clazz.getName().equalsIgnoreCase(MAP_TYPE)){
			// Map<> 泛型
			if (type instanceof ParameterizedType) {
				// 获取泛型里面的具体对象类型
				Type[] params = ((ParameterizedType) type).getActualTypeArguments();

				return new MapProvider(params).generate();
			}
			return new MapProvider().generate();
		}
		// Set
		if(clazz.getName().equalsIgnoreCase(SET_TYPE)){
			// SET<> 泛型
			if (type instanceof ParameterizedType) {
				// 获取泛型里面的具体对象类型
				Type[] params = ((ParameterizedType) type).getActualTypeArguments();

				return SetProvider.generate((Class)params[0]);
			}
			return SetProvider.generate(String.class);
		}
		// 枚举
		if(clazz.isEnum()){
			return new EnumProvider(clazz).generate();
		}

		return new ObjectProvider<>(clazz).generate();
	}
}
