package com.lucky.ut.effective.h2.model;


import com.lucky.ut.effective.h2.enums.ColType;

import java.util.List;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/10/10 16:52
 * @Description yml 测试数据实体
 */
public class YmlTestDate {


    private List<TestDataBean> testData;

    public List<TestDataBean> getTestData() {
        return testData;
    }

    public void setTestData(List<TestDataBean> testData) {
        this.testData = testData;
    }

    /**
     * 测试数据 Bean
     */
    public static class TestDataBean {
        /**
         * table : {"name":"t_goods","rows":[{"row":{"columns":[{"col":{"name":"id","value":"1","isId":true}},{"col":{"name":"name","value":"apple"}},{"col":{"name":"sku","value":"apple-10001"}},{"col":{"name":"inventory","value":"100"}}]}}],"types":[{"typeHint":{"name":"id","type":"ColType.BIGINT"}},{"typeHint":{"name":"inventory","type":"ColType.INTEGER"}}]}
         */
        private TableBean table;

        public TableBean getTable() {
            return table;
        }

        public void setTable(TableBean table) {
            this.table = table;
        }

        public static class TableBean {
            /**
             * name : t_goods
             * rows : [{"row":{"columns":[{"col":{"name":"id","value":"1","isId":true}},{"col":{"name":"name","value":"apple"}},{"col":{"name":"sku","value":"apple-10001"}},{"col":{"name":"inventory","value":"100"}}]}}]
             * types : [{"typeHint":{"name":"id","type":"ColType.BIGINT"}},{"typeHint":{"name":"inventory","type":"ColType.INTEGER"}}]
             */
            /**
             * table name
             */
            private String name;
            /**
             * table rows
             */
            private List<RowsBean> rows;
            /**
             * table column type
             */
            private List<TypesBean> types;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<RowsBean> getRows() {
                return rows;
            }

            public void setRows(List<RowsBean> rows) {
                this.rows = rows;
            }

            public List<TypesBean> getTypes() {
                return types;
            }

            public void setTypes(List<TypesBean> types) {
                this.types = types;
            }

            public static class RowsBean {
                /**
                 * row : {"columns":[{"col":{"name":"id","value":"1","isId":true}},{"col":{"name":"name","value":"apple"}},{"col":{"name":"sku","value":"apple-10001"}},{"col":{"name":"inventory","value":"100"}}]}
                 */

                private RowBean row;

                public RowBean getRow() {
                    return row;
                }

                public void setRow(RowBean row) {
                    this.row = row;
                }

                public static class RowBean {

                    private List<ColumnsBean> columns;

                    public List<ColumnsBean> getColumns() {
                        return columns;
                    }

                    public void setColumns(List<ColumnsBean> columns) {
                        this.columns = columns;
                    }

                    public static class ColumnsBean {
                        /**
                         * col : {"name":"id","value":"1","isId":true}
                         */

                        private ColBean col;

                        public ColBean getCol() {
                            return col;
                        }

                        public void setCol(ColBean col) {
                            this.col = col;
                        }

                        public static class ColBean {
                            /**
                             * name : id
                             * value : 1
                             * isId : true
                             */

                            private String name;
                            private String value;
                            private Boolean isId = false;

                            public String getName() {
                                return name;
                            }

                            public void setName(String name) {
                                this.name = name;
                            }

                            public String getValue() {
                                return value;
                            }

                            public void setValue(String value) {
                                this.value = value;
                            }

                            public boolean isIsId() {
                                return isId;
                            }

                            public void setIsId(boolean isId) {
                                this.isId = isId;
                            }
                        }
                    }
                }
            }

            public static class TypesBean {
                /**
                 * typeHint : {"name":"id","type":"BIGINT"}
                 */
                private TypeHintBean typeHint;

                public TypeHintBean getTypeHint() {
                    return typeHint;
                }

                public void setTypeHint(TypeHintBean typeHint) {
                    this.typeHint = typeHint;
                }

                public static class TypeHintBean {
                    /**
                     * name : id
                     * type : BIGINT
                     */
                    private String name;
                    private ColType type;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public ColType getType() {
                        return type;
                    }

                    public void setType(ColType type) {
                        this.type = type;
                    }
                }
            }
        }
    }
}
