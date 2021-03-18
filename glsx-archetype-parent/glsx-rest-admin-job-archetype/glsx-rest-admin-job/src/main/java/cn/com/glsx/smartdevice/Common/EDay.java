package cn.com.glsx.smartdevice.Common;

public enum EDay {

    WORKDAY {
        @Override
        public Integer getCode() {
            return 0;
        }
        
        @Override
        public Integer getValue() {
            return 2;
        }

        @Override
        public String getName(){
            return "工作日";
        }
    },
    HOLIDAY {
        @Override
        public Integer getCode() {
            return 1;
        }

        @Override
        public Integer getValue() {
            return 3;
        }

        @Override
        public String getName(){
            return "节假日";
        }
    },
    WEEKDAY {
        @Override
        public Integer getCode() {
            return 3;
        }

        @Override
        public Integer getValue() {
            return 3;
        }

        @Override
        public String getName(){
            return "双休日";
        }
    },
    LEAVE_OFF_DAY {
        @Override
        public Integer getCode() {
            return 4;
        }

        @Override
        public Integer getValue() {
            return 2;
        }

        @Override
        public String getName(){
            return "调休日";
        }
    },;

    public static Integer getValueByCode(Integer code) {
        for (EDay day : EDay.values()) {
            if (day.getCode().equals(code)) {
                return day.getValue();
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        for (EDay day : EDay.values()) {
            if (day.getCode().equals(code)) {
                return day.getName();
            }
        }
        return null;
    }

    /* 0表示工作日、为1、节假日，为2双休日、3为调休日（上班）*/
    public abstract Integer getCode();

    // 1每天 2工作日 3法定节假日
    public abstract Integer getValue();

    public abstract String getName();
}
