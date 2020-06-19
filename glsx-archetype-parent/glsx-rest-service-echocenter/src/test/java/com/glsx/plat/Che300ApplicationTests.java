//package com.glsx.gecc;
//
//import com.alibaba.fastjson.JSON;
//import com.glsx.miniservice.services.che300.Che300Service;
//import com.glsx.miniservice.services.che300.req.*;
//import com.glsx.miniservice.services.che300.resp.*;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class Che300ApplicationTests {
//
//    @Resource
//    private Che300Service che300Service;
//
//    @Test
//    public void getAllCity() {
//        CityListReq req = new CityListReq();
//        CityListResp resp = che300Service.getAllCity(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void getCarBrandList() {
//        CarBrandListReq req = new CarBrandListReq();
//        CarBrandListResp resp = che300Service.getCarBrandList(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void getCarSeriesList() {
//        CarSeriesListReq req = new CarSeriesListReq();
//        req.setBrandId(35);
//        CarSeriesListResp resp = che300Service.getCarSeriesList(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void getCarModelList() {
//        CarModelListReq req = new CarModelListReq();
//        req.setSeriesId(408);
//        CarModelListResp resp = che300Service.getCarModelList(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void getSeriesInfo() {
//        SeriesInfoReq req = new SeriesInfoReq();
//        req.setSeriesId(5);
//        SeriesInfoResp resp = che300Service.getSeriesInfo(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void getCarModelInfo() {
//        CarModelInfoReq req = new CarModelInfoReq();
//        req.setModelId(1351700);
//        CarModelInfoResp resp = che300Service.getCarModelInfo(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void getModelParameters() {
//        ModelParametersReq req = new ModelParametersReq();
//        req.setModelId(1234745);
//        req.setVersion(2);
////        req.setKeyType("en");
//        ModelParametersResp resp = che300Service.getModelParameters(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void getUpdatedModelParamsInTimeRange() {
//        UpdatedModelParamsInTimeRangeReq req = new UpdatedModelParamsInTimeRangeReq();
//        req.setFromTime("2019-12-02");
//        UpdatedModelParamsInTimeRangeResp resp = che300Service.getUpdatedModelParamsInTimeRange(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//    @Test
//    public void getUsedCarPrice() {
//        UsedCarPriceReq req = new UsedCarPriceReq();
//        req.setModelId(5997);
//        req.setZone("50");
//        req.setMile(3.2);
//        req.setRegDate("2015-11");
//        UsedCarPriceResp resp = che300Service.getUsedCarPrice(req);
//        System.out.println(JSON.toJSONString(resp));
//    }
//
//}