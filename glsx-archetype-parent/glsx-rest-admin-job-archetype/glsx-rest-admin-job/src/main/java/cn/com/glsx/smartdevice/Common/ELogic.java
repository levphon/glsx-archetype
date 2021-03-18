package cn.com.glsx.smartdevice.Common;

public enum ELogic {

    LESS_THAN {
        @Override
        public Integer getCode() {
            return 1;
        }

        @Override
        public boolean getValue(String deviceOrderValue,String deviceOrderValueReal) {
            return Integer.parseInt(deviceOrderValueReal) < Integer.parseInt(deviceOrderValue);
        }

        @Override
        public String getName(){
            return "小于";
        }
    },
    GREATER_THAN {
        @Override
        public Integer getCode() {
            return 2;
        }

        @Override
        public boolean getValue(String deviceOrderValue,String deviceOrderValueReal) {
            return Integer.parseInt(deviceOrderValueReal) > Integer.parseInt(deviceOrderValue);
        }


        @Override
        public String getName(){
            return "大于";
        }
    },
    EQUALS {
        @Override
        public Integer getCode() {
            return 3;
        }

        @Override
        public boolean getValue(String deviceOrderValue,String deviceOrderValueReal) {
            return deviceOrderValueReal.equals(deviceOrderValue);
        }


        @Override
        public String getName(){
            return "等于";
        }
    },;

    public static boolean getValueByCode(Integer code,String deviceOrderValue,String deviceOrderValueReal) {
        for (ELogic day : ELogic.values()) {
            if (day.getCode().equals(code)) {
                return day.getValue(deviceOrderValue,deviceOrderValueReal);
            }
        }
        return false;
    }

    public static String getNameByCode(Integer code) {
        for (ELogic day : ELogic.values()) {
            if (day.getCode().equals(code)) {
                return day.getName();
            }
        }
        return null;
    }

    /* 逻辑（1：<,2：>,3：=）*/
    public abstract Integer getCode();
    //deviceOrderValue:触发条件中的设备状态值,deviceOrderValueReal:数据库中的设备状态值
    public abstract boolean getValue(String deviceOrderValue,String deviceOrderValueReal);

    public abstract String getName();
}
