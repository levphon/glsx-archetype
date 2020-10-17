package com.glsx.plat.common.utils;

import com.google.gson.*;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author payu
 */
public class JsonSortUtil {

    /**
     * 定义比较规则
     *
     * @return
     */
    private static Comparator<String> getComparator() {
        return String::compareTo;
    }

    /**
     * 排序
     *
     * @param e
     */
    public static void sort(JsonElement e) {
        if (e.isJsonNull() || e.isJsonPrimitive()) {
            return;
        }

        if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            Iterator<JsonElement> it = a.iterator();
            it.forEachRemaining(JsonSortUtil::sort);
            return;
        }

        if (e.isJsonObject()) {
            Map<String, JsonElement> tm = new TreeMap<>(getComparator());
            for (Map.Entry<String, JsonElement> en : e.getAsJsonObject().entrySet()) {
                tm.put(en.getKey(), en.getValue());
            }

            String key;
            JsonElement val;
            for (Map.Entry<String, JsonElement> en : tm.entrySet()) {
                key = en.getKey();
                val = en.getValue();
                e.getAsJsonObject().remove(key);
                e.getAsJsonObject().add(key, val);
                sort(val);
            }
        }
    }

    /**
     * 根据json key排序
     *
     * @param json
     * @return
     */
    public static String sortJson(String json) {
        Gson g = new GsonBuilder().create();
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(json);
        sort(e);
        return g.toJson(e);
    }

    public static void main(String[] args) {
        String json = "{\"orderNumber\":\"B20200908165943000043\",\"personalData\":{\"name\":\"莫娟容\",\"gender\":2,\"certificateNumber\":\"450881199204266545\",\"phoneNumber\":\"13650015330\",\"email\":\"35677656@qq.com\",\"educationInfo\":2,\"marriageInfo\":4},\"addressData\":{\"togetherDwell\":1,\"housingType\":3,\"liveDate\":1425263714,\"monthRent\":2000,\"presentProvince\":\"广东省\",\"presentCity\":\"深圳市\",\"presentRegion\":\"罗湖区\",\"presentAddress\":\"平湖街道熙璟城豪苑1000栋10000号楼AAA座EE\"},\"jobData\":{\"businessType\":1,\"companyName\":\"深圳沐希健身管理有限公司\",\"operateTime\":1425263714,\"industryCode\":\"3\",\"contactNumber\":\"076622948288\",\"monthTurnover\":200000,\"operateAddress\":\"深圳市宝安区桂园街道红桂路10086号首层110层\",\"hiredTime\":1425263714,\"station\":\"技术顾问\",\"wage\":123,\"unitType\":1},\"contactData\":[{\"contactName\":\"巩俐(老婆)\",\"relation\":8,\"idNumber\":\"512900095212030000\",\"contactNumber\":\"13378789090\",\"workUnit\":\"华夏中影北京文化传媒-11\",\"unitAddress\":\"深圳市南山区白石路碧海云天1111栋000B-22\",\"contactAddress\":\"深圳市南山区白石路碧海云天1111栋000B--22\",\"isKnow\":1},{\"contactName\":\"李嘉诚(父母)\",\"relation\":1,\"contactNumber\":\"18900997878\",\"isKnow\":1},{\"contactName\":\"刘德华(朋友)\",\"relation\":6,\"idNumber\":\"512900095212030011\",\"contactNumber\":\"15623235656\",\"workUnit\":\"华夏中影北京文化传媒-33\",\"contactAddress\":\"contactAddress-深圳市南山区白石路碧海云天1111栋000B-33\",\"isKnow\":1}],\"incomeData\":{\"privateIncome\":{\"nearlyOneMonth\":531216.0,\"nearlyTwoMonth\":764312.0,\"nearlyThirdMonth\":131696.0,\"nearlyFourMonth\":147184.0,\"nearlyFiveMonth\":100730.0,\"nearlySixMonth\":33446.0,\"interestAmount\":0.0},\"publicIncome\":{\"nearlyOneMonth\":31216,\"nearlyTwoMonth\":64312,\"nearlyThirdMonth\":31696,\"nearlyFourMonth\":47184,\"nearlyFiveMonth\":90730,\"nearlySixMonth\":3446,\"interestAmount\":0}},\"documentData\":{\"idCardFront\":\"https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image\\u0026quality\\u003d100\\u0026size\\u003db4000_4000\\u0026sec\\u003d1597049224\\u0026di\\u003d2540ac61b76f71c5380ed81d304cfc2f\\u0026src\\u003dhttp://77fkxu.com1.z0.glb.clouddn.com/20160822/1471847646_56934.jpg\",\"idCardBack\":\"https://timgsa.baidu.com/timg?image\\u0026quality\\u003d80\\u0026size\\u003db9999_10000\\u0026sec\\u003d1597059321261\\u0026di\\u003d1655b0c45ed8851610da190340aec51f\\u0026imgtype\\u003d0\\u0026src\\u003dhttp%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20160616%2Fc5e9e66472f6445b87c5bc72b4ba63df_th.jpg\"},\"pedestrianData\":{\"account\":\"liudehua\",\"password\":\"999666\",\"authorizationCode\":\"test6666\",\"expiredTime\":1583209533},\"loanData\":{\"productId\":13,\"annuity\":120000,\"loanPurpose\":2,\"loanPeriod\":12},\"mer_id\":\"2214534678987105\",\"sign\":\"F2E9FF6CF0340D618928960E66AB1F32\"}";
        System.out.println(sortJson(json));
    }

}
