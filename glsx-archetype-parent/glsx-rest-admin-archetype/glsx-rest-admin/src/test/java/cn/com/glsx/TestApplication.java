//package cn.com.glsx;
//
//import com.glsx.microservice.api.EchoCenterFeignService;
//import com.glsx.plat.core.web.R;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import javax.annotation.Resource;
//
///**
// * 测试spring cloud 服务调用方式
// */
//@EnableDiscoveryClient
//@SpringBootApplication
//public class TestApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(TestApplication.class, args);
//    }
//
//    @Slf4j
//    @RestController
//    static class TestController {
//
//        @Autowired
//        RestTemplate restTemplate;
//
//        @Resource
//        private EchoCenterFeignService echoCenterFeignService;
//
////        @Autowired
////        private WebClient.Builder webClientBuilder;
//
//        @GetMapping("/test1")
//        public String test1() {
//            String result = restTemplate.getForObject("http://192.168.0.63:8829/glsx-rest-echo-center/echo/didi", String.class);
//            return "Return : " + result;
//        }
//
////        @GetMapping("/test")
////        public Mono<String> test() {
////            Mono<String> result = webClientBuilder.build()
////                    .get()
////                    .uri("http://alibaba-nacos-discovery-server/hello?name=didi")
////                    .retrieve()
////                    .bodyToMono(String.class);
////            return result;
////        }
//
//        @GetMapping("/test3")
//        public R test3() {
//            String result = echoCenterFeignService.echo("this is a test!");
//            return R.ok().data(result);
//        }
//    }
//
//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//
////    @Bean
////    @LoadBalanced
////    public WebClient.Builder loadBalancedWebClientBuilder() {
////        return WebClient.builder();
////    }
//
//}