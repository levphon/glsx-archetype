package cn.com.glsx.smartdevice.Common;

/**
 * @author fengzhi
 * @date 2021/3/9 17:05
 * @description 平台信息枚举
 */
public enum EMonth {
    MONTH1 {
        @Override
        public Integer getCode() {
            return 1;
        }

        @Override
        public String getValue() {
            return "01";
        }

        @Override
        public String getBeanName(){
            return "一月";
        }
    },
    MONTH2 {
        @Override
        public Integer getCode() {
            return 2;
        }

        @Override
        public String getValue() {
            return "02";
        }

        @Override
        public String getBeanName(){
            return "二月";
        }
    },
    MONTH3 {
        @Override
        public Integer getCode() {
            return 3;
        }

        @Override
        public String getValue() {
            return "03";
        }

        @Override
        public String getBeanName(){
            return "三月";
        }
    },
    MONTH4 {
        @Override
        public Integer getCode() {
            return 4;
        }

        @Override
        public String getValue() {
            return "04";
        }

        @Override
        public String getBeanName(){
            return "四月";
        }
    },
    MONTH5 {
        @Override
        public Integer getCode() {
            return 5;
        }

        @Override
        public String getValue() {
            return "05";
        }

        @Override
        public String getBeanName(){
            return "五月";
        }
    },
    MONTH6 {
        @Override
        public Integer getCode() {
            return 6;
        }

        @Override
        public String getValue() {
            return "06";
        }

        @Override
        public String getBeanName(){
            return "六月";
        }
    },
    MONTH7 {
        @Override
        public Integer getCode() {
            return 7;
        }

        @Override
        public String getValue() {
            return "07";
        }

        @Override
        public String getBeanName(){
            return "七月";
        }
    },
    MONTH8 {
        @Override
        public Integer getCode() {
            return 8;
        }

        @Override
        public String getValue() {
            return "08";
        }

        @Override
        public String getBeanName(){
            return "八月";
        }
    },
    MONTH9 {
        @Override
        public Integer getCode() {
            return 9;
        }

        @Override
        public String getValue() {
            return "09";
        }

        @Override
        public String getBeanName(){
            return "九月";
        }
    },
    MONTH10 {
        @Override
        public Integer getCode() {
            return 10;
        }

        @Override
        public String getValue() {
            return "10";
        }

        @Override
        public String getBeanName(){
            return "十月";
        }
    },
    MONTH11 {
        @Override
        public Integer getCode() {
            return 11;
        }

        @Override
        public String getValue() {
            return "11";
        }

        @Override
        public String getBeanName(){
            return "十一月";
        }
    },
    MONTH12 {
        @Override
        public Integer getCode() {
            return 12;
        }

        @Override
        public String getValue() {
            return "12";
        }

        @Override
        public String getBeanName(){
            return "十二月";
        }
    },;

    public static String getValueByCode(Integer code) {
        for (EMonth month : EMonth.values()) {
            if (month.getCode().equals(code)) {
                return month.getValue();
            }
        }
        return null;
    }

    public static String getBeanNameByCode(Integer code) {
        for (EMonth month : EMonth.values()) {
            if (month.getCode().equals(code)) {
                return month.getBeanName();
            }
        }
        return null;
    }

    public abstract Integer getCode();

    public abstract String getValue();

    public abstract String getBeanName();
}
