package cn.com.glsx.smartdevice.factory;

/**
 * @author fengzhi
 * @date 2021/3/9 17:05
 * @description 平台信息枚举
 */
public enum Condition implements ConditionConstants {
    ANY_MATCH {
        @Override
        public Integer getCode() {
            return 1;
        }

        @Override
        public String getValue() {
            return "任意条件满足";
        }

        @Override
        public String getBeanName(){
            return "anyMatchStrategy";
        }
    },
    ALL_MATCH {
        @Override
        public Integer getCode() {
            return 2;
        }

        @Override
        public String getValue() {
            return "全部条件满足";
        }

        @Override
        public String getBeanName(){
            return "allMatchStrategy";
        }
    },;

    public static String getValueByCode(Integer code) {
        for (Condition platForm : Condition.values()) {
            if (platForm.getCode().equals(code)) {
                return platForm.getValue();
            }
        }
        return null;
    }

    public static String getBeanNameByCode(Integer code) {
        for (Condition platForm : Condition.values()) {
            if (platForm.getCode().equals(code)) {
                return platForm.getBeanName();
            }
        }
        return null;
    }
}
