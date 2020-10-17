package cn.com.glsx.admin.api;

import cn.com.glsx.admin.services.userservice.model.UserBO;
import cn.com.glsx.admin.services.userservice.model.UserDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author payu
 */
@FeignClient(name = "glsx-rest-admin", contextId = "usercenter", path = "/api/user/", decode404 = true)
public interface UserCenterFeignClient {

    /**
     *  一、FeignClients使用注意事项：
     *  1. @EnableFeignClients 默认扫描 xxxxApplication启动入口 所在包路径下的 @FeignClient bean，若无法扫描到， 可以在使用Feign调用外部模块的api时候，
     *  需要在引用服务中 xxxxApplication 中的 `@EnableFeignClients(basePackages = "cn.tendyron.customer")` 添加外部包需要扫描FeignClient的路径,否则无法注入bean
     *  2. @FeignClient 声明的类，使用 spring.application.name 作为 name配置 @FeignClient(name="xxxx"),如果想保留 context-path , 则需要配置 path 属性 ，如：@FeignClient(name="xxxx" , path="xxxx（context-path）")
     *  3. @FeignClient 接口对应的实现类，需要使用 @RestController注解 声明
     *  4. mapper注解不支持 ： @GetMapping,@PostMapping  ， 参数要加 @RequestParam（“xxx”）
     *  5. FeignClient 调用，实质是httpClient调用 ,若我们暴露的接口api，声明了对应的 http mapper 信息，在调用方调用时候，通过代理 发起了 http请求，到服务提供方对应的http服务上去，所以在服务提供方的接口，可以使用 @RestController
     *     来声明接口的 实现，否则调用方无法找到 http 对应的路径，会报404 ；
     *     或者 根据 api 声明的http 信息，构建一个 Controller ，再来调用接口的实现类，但是这样不太方便；
     *  6.FeignClient是一个“客户端”，它是用来发起请求的，不是提供服务的。
     *
     *
     *  二、fallback 几种模式：
     *  1.EchoCenterFeignServiceFallback.class 直接fallback，获取不到HTTP请求错误状态码和信息
     *  2.EchoCenterFeignFactory.class 特定类型工厂模式
     *  3.FunFallbackFactory 使用统一的工厂模式处理
     *
     *
     *  三、性能问题
     *  1.Feign是一个http请求调用的轻量级框架,基于http协议，性能不如rpc
     *  2.可以为Feign配置连接池，配合使用okhttp等
     */

    /**
     * 测试Feign Get ，普通处理
     * 注意： @RequestParam("xx") 注解一定要写，而且属性名xx不能省略
     */
    @GetMapping("/echo")
    String echo(@RequestParam("message") String message);

    @GetMapping("/search")
    PageInfo<UserDTO> search(@RequestParam Map<String, Object> params);

    @PostMapping(value = "/add")
    UserDTO add(@RequestBody @Valid UserBO userBO);

    @PostMapping(value = "/edit")
    int edit(@RequestBody @Valid UserBO userBO);

    @GetMapping(value = "/info/{id}")
    UserDTO info(@PathVariable("id") Long id);

}
