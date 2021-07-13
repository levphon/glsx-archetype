package cn.com.glsx.canal.common.bussinessFactory;

/**
 * @author zhouhaibao
 * @version 1.0
 * @description: tableName原来的库的表名,handerService处理实现类的实例
 * @date 2021/6/23 14:36
 */
public enum BussinessType {

    test("aaaaa", "testImpl");
    private String busType;
    private String handerImp;

    BussinessType(String busType, String handerImp) {
        this.busType = busType;
        this.handerImp = handerImp;
    }

    public static String getHanderType(String busType) {
        for (BussinessType m : BussinessType.values()) {
            if (m.busType.equals(busType)) {
                return m.handerImp;
            }
        }
        return null;
    }

}
