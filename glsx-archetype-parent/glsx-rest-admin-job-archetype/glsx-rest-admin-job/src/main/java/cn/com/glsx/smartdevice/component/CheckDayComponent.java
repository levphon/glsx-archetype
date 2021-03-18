package cn.com.glsx.smartdevice.component;

import cn.com.glsx.smartdevice.Common.EMonth;
import cn.com.glsx.smartdevice.utils.HttpUtil;
import cn.com.glsx.vasp.modules.entity.DayInfo;
import cn.com.glsx.vasp.modules.mapper.DayInfoMapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CheckDayComponent {
    //天行数据平台key(节假日接口)
    @Value("${tianapi.url}")
    private String url;
    //天行数据平台key(节假日接口)
    @Value("${tianapi.key}")
    private String key;

    @Autowired
    private DayInfoMapper dayInfoMapper;

    //1.查询节假日接口(参数为当前时间转化的年月，如2021-03，type为月份)
    public JSONObject sendCheckDayRequestGet(String date) {
        JSONObject response = null;
        try {
            Map<String, String> reqParams = new HashMap<>();
            reqParams.put("key", key);
            reqParams.put("type", "2");
            reqParams.put("date", date);
            response = HttpUtil.sendGet(url, reqParams, new HashMap<>());
        } catch (IOException e) {
            e.printStackTrace();
            log.info("请求节假日接口异常,异常信息{}", e);
        }
        return response;
    }

    //每天100次免费调用，每次使用12次（查12个月）
    public void searchAndInsertMonth() {
        //全年数据
        List<DayInfo> daysYear = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        long start = System.currentTimeMillis();
        //全年12个月遍历
        Arrays.stream(EMonth.values()).forEach(m -> {
            //月份
            String monthStr = m.getValue();
            //查询年月，返回当月每天总数据，如2021-01，每次查一个月所有天数据（每天可判断节假日，工作日，双休日，调休日）
            JSONObject response = sendCheckDayRequestGet(year + "-" + monthStr);
            if (null != response && response.getInteger("code") == 200) {
                //请求成功
                JSONArray newslist = response.getJSONArray("newslist");
                //当月数据
                List<DayInfo> daysMonth = newslist.stream().map(j -> {
                    JSONObject jsonObject = (JSONObject) j;
                    DayInfo day = new DayInfo();
                    //当前阳历日期,如：2019-10-01
                    String date = jsonObject.getString("date");
                    day.setDate(date);
                    //日期类型，为0表示工作日、为1节假日、为2双休日、3为调休日（上班）
                    int daycode = jsonObject.getIntValue("daycode");
                    day.setDaycode(daycode);
                    //节假日名称，如：国庆节
                    String name = jsonObject.getString("name");
                    day.setName(name);
                    //放假提示，如：10月1日至7日放假调休，共7天......
                    String tip = jsonObject.getString("tip");
                    day.setTip(tip);
                    return day;
                }).collect(Collectors.toList());
                //添加数据
                daysYear.addAll(daysMonth);
            }
        });
        long end1 = System.currentTimeMillis();
        log.info("全年数据请求时长=====" + (end1 - start));

        /*daysYear.stream().forEach(day->{
            log.info("WorkOrHoliDayJob==date:"+day.getDate()+"==daycode:"+day.getDaycode()+"==name:"+day.getName()+"==tip:"+day.getTip());
        });*/
        //全年每天的数据入库
        if (null != daysYear && !daysYear.isEmpty()) {
            int res = dayInfoMapper.updateDayInsert(daysYear);
            long end2 = System.currentTimeMillis();
            log.info("全年数据入库时长=====code:" + res + "======time:" + (end2 - start));
        }
    }

    //测试
    public void searchAndInsertMonth2() {
        //全年数据
        List<DayInfo> daysYear = new ArrayList<>();

        long start = System.currentTimeMillis();
        //全年12个月遍历

            for(int i=0;i<31;i++){
                DayInfo day = new DayInfo();
                //当前阳历日期,如：2019-10-01
                String date = "datefz"+i;
                day.setDate(date);
                //日期类型，为0表示工作日、为1节假日、为2双休日、3为调休日（上班）
                int daycode = 0;
                day.setDaycode(daycode);
                //节假日名称，如：国庆节
                String name = "name";
                day.setName(name);
                //放假提示，如：10月1日至7日放假调休，共7天......
                String tip = "tip";
                day.setTip(tip);
                //添加数据
                daysYear.add(day);
            }

        long end1 = System.currentTimeMillis();
        log.info("全年数据请求时长=====" + (end1 - start));

        /*daysYear.stream().forEach(day->{
            log.info("WorkOrHoliDayJob==date:"+day.getDate()+"==daycode:"+day.getDaycode()+"==name:"+day.getName()+"==tip:"+day.getTip());
        });*/
        //全年每天的数据入库
        if (null != daysYear && !daysYear.isEmpty()) {
            int res = dayInfoMapper.updateDayInsert(daysYear);
            long end2 = System.currentTimeMillis();
            log.info("全年数据入库时长=====code:" + res + "======time:" + (end2 - start));
        }
    }
}
