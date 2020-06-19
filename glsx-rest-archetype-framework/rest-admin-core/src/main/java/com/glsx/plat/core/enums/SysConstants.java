package com.glsx.plat.core.enums;

/**
 * 常量列表
 */
public interface SysConstants {

    /**
     * 获取代码量
     *
     * @return
     */
    Integer getCode();

    /**
     * 获取值
     *
     * @return
     */
    String getValue();

    /**
     * 启用状态
     */
    public enum EnableStatus implements SysConstants {
        initialize {
            @Override
            public Integer getCode() {
                return 0;
            }

            @Override
            public String getValue() {
                return "审核中";
            }
        },
        enable {
            @Override
            public Integer getCode() {
                return 1;
            }

            @Override
            public String getValue() {
                return "启用";
            }
        }, disable {
            @Override
            public Integer getCode() {
                return 2;
            }

            @Override
            public String getValue() {
                return "停用";
            }
        };
    }

    /**
     * 删除状态
     */
    public enum DeleteStatus implements SysConstants {
        normal {
            @Override
            public Integer getCode() {
                return 0;
            }

            @Override
            public String getValue() {
                return "正常";
            }
        }, delete {
            @Override
            public Integer getCode() {
                return -1;
            }

            @Override
            public String getValue() {
                return "删除";
            }
        };
    }

}
