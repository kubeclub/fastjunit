package com.lucky.ut.effective.h2.enums;

import com.lucky.ut.effective.utils.Base64Utils;
import org.apache.commons.codec.Charsets;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/27 10:52
 * @Description The enum that defines the types that are used to identify generic SQL types.
 * <p>
 * Each type corresponds to [java.sql.Types](https://docs.oracle.com/javase/8/docs/api/java/sql/Types.html)
 */
public enum ColType {
    /**
     *
     */
    BINARY(Types.BINARY),
    VARBINARY(Types.VARBINARY),
    LONGVARBINARY(Types.LONGVARBINARY),
    BLOB(Types.BLOB),

    BOOLEAN(Types.BOOLEAN),
    BIT(Types.BIT),

    DATE(Types.DATE),
    TIME(Types.TIME),
    TIMESTAMP(Types.TIMESTAMP),
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE),

    BIGINT(Types.BIGINT),
    DECIMAL(Types.DECIMAL),
    DOUBLE(Types.DOUBLE),
    FLOAT(Types.FLOAT),
    INTEGER(Types.INTEGER),
    REAL(Types.REAL),
    SMALLINT(Types.SMALLINT),
    TINYINT(Types.TINYINT),

    CHAR(Types.CHAR),
    CLOB(Types.CLOB),
    LONGVARCHAR(Types.LONGVARCHAR),
    VARCHAR(Types.VARCHAR),

    DEFAULT(Types.NULL);


    private int sqlType;

    ColType(int sqlType) {
        this.sqlType = sqlType;
    }

    public int getSqlType() {
        return sqlType;
    }

    public static ColType getColType(int sqlType) {
        Stream<ColType> values = Stream.of(values());
        Optional<ColType> optional = values.filter(colType -> colType.sqlType == sqlType).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        return DEFAULT;
    }


    public Object convert(String value) throws SQLException {
        switch (this) {
            case VARBINARY:
            case LONGVARBINARY:
                return value.getBytes(Charsets.UTF_8);
            case BINARY:
                return Base64Utils.decode(value);
            case BLOB:
                return new SerialBlob(Base64Utils.decode(value));
            case BIT:
            case BOOLEAN:
                return Boolean.valueOf(value);
            case DATE:
                LocalDate date = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return java.sql.Date.valueOf(date);
            case TIME:
                LocalTime time = LocalTime.parse(value, DateTimeFormatter.ofPattern("[HH:mm:ss.SSSSSSSSS][HH:mm:ss.SSSSSSSS][HH:mm:ss.SSSSSSSS][HH:mm:ss.SSSSSSS][HH:mm:ss.SSSSSS][HH:mm:ss.SSSSS][HH:mm:ss.SSSS][HH:mm:ss.SSS][HH:mm:ss.SS][HH:mm:ss.S][HH:mm:ss]"));
                return java.sql.Time.valueOf(time);
            case TIMESTAMP:
                LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss.SSSSSSSSS][yyyy-MM-dd HH:mm:ss.SSSSSSSS][yyyy-MM-dd HH:mm:ss.SSSSSSSS][yyyy-MM-dd HH:mm:ss.SSSSSSS][yyyy-MM-dd HH:mm:ss.SSSSSS][yyyy-MM-dd HH:mm:ss.SSSSS][yyyy-MM-dd HH:mm:ss.SSSS][yyyy-MM-dd HH:mm:ss.SSS][yyyy-MM-dd HH:mm:ss.SS][yyyy-MM-dd HH:mm:ss.S][yyyy-MM-dd HH:mm:ss]"));
                return java.sql.Timestamp.valueOf(localDateTime);
            case TIMESTAMP_WITH_TIMEZONE:
                return OffsetDateTime.parse(value, DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ssxxx][yyyy-MM-dd HH:mm:ssxx][yyyy-MM-dd HH:mm:ssx]"));
            case TINYINT:
                return Short.valueOf(value);
            case INTEGER:
            case SMALLINT:
                return Integer.valueOf(value);
            case BIGINT:
                return Long.valueOf(value);
            case REAL:
                return Float.valueOf(value);
            case FLOAT:
            case DOUBLE:
                return Double.valueOf(value);
            case DECIMAL:
                return new BigDecimal(value);
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
                return value;
            case CLOB:
                return new SerialClob(value.toCharArray());
            default:
                break;
        }
        return value;
    }

}
