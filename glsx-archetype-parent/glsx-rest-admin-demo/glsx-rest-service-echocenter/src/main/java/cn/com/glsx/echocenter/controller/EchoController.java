package cn.com.glsx.echocenter.controller;

import cn.com.glsx.echocenter.services.echoservice.EchoService;
import cn.com.glsx.echocenter.services.echoservice.req.EchoReq;
import cn.com.glsx.echocenter.services.echoservice.resp.EchoResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
public class EchoController {

    @Resource
    private EchoService echoService;

    @GetMapping("echo/{message}")
    public String echo(@PathVariable("message") String message) {
        log.info("echo");
        return echoService.echo(message);
    }

    @GetMapping("echo2")
    public String echo(@RequestParam("title") String title, @RequestParam("message") String message) {
        log.info("echo2");
        return echoService.echo(title, message);
    }

    @PostMapping("echo3")
    public EchoResp echo(@RequestBody EchoReq req) {
        log.info("echo3");
        return echoService.echo(req);
    }

}
