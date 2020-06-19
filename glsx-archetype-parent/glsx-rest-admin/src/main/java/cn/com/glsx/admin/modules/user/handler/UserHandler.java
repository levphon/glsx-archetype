package cn.com.glsx.admin.modules.user.handler;

import com.glsx.vasp.modules.entity.User;
import com.glsx.vasp.modules.mapper.UserMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author payu
 */
@Component
public class UserHandler {

    @Resource
    private UserMapper userMapper;

    public Mono<Integer> save(User user) {
        return Mono.create(sink -> sink.success(userMapper.insert(user)));
    }

    public Mono<User> findById(Long id) {
        return Mono.justOrEmpty(userMapper.selectByPrimaryKey(id));
    }

    public Mono<User> findByPhone(String phone) {
        return Mono.justOrEmpty(userMapper.selectByPrimaryKey(phone));
    }

    public Flux<User> findAllUser() {
        return Flux.fromIterable(userMapper.selectAll());
    }

    public Mono<Integer> modify(User user) {
        return Mono.create(sink -> sink.success(userMapper.updateByPrimaryKey(user)));
    }

    public Mono<Integer> delete(Long id) {
        return Mono.create(sink -> sink.success(userMapper.deleteByPrimaryKey(id)));
    }

}
