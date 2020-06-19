package cn.com.glsx.admin.modules.user.service;

import cn.hutool.core.lang.UUID;
import com.github.pagehelper.PageInfo;
import com.glsx.plat.common.utils.ObjectUtils;
import com.glsx.plat.common.utils.StringUtils;
import com.glsx.plat.core.constant.BasicConstants;
import com.glsx.plat.jwt.util.JwtUtils;
import com.glsx.plat.web.utils.SessionUtils;
import cn.com.glsx.admin.modules.user.dto.UserSearch;
import cn.com.glsx.admin.modules.user.utils.JwtUser;
import com.glsx.vasp.modules.entity.User;
import com.glsx.vasp.modules.mapper.UserMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuyf
 * @desc 用户信息
 * @date 2019年10月24日 下午2:37:40
 */
@Service
public class UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private UserMapper userMapper;

    public User getById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public User findByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    public PageInfo<User> search(UserSearch search) {
        final PageInfo<User> pageInfo = new PageInfo<>(this.userMapper.selectAll());
        return pageInfo;
    }

    public User addUser(User user) {

        String password = user.getPassword();

//        // uuid获取随机字符串，作为盐值。
//        String salt = StringUtils.generateRandomCode(false, 4);
//
//        //add Salt to password
//        //user.getCredentialsSalt
//        SimpleHash hash = new SimpleHash(hcm.getHashAlgorithmName(), password, salt, hcm.getHashIterations());
//        //重新赋值
//        user.setSalt(salt);
//        user.setPassword(hash.toString());
//        user.setEnableStatus(SysConstants.EnableStatus.enable.getCode());
//        user.setCreateTime(new Date());

        //todo 入库
        return user;
    }

    public User editUser(User user) {
        String password = user.getPassword();

        //todo 入库
        return user;
    }

    public String createToken(User user) {
        // 前端有两种登录方式，如果用手机号+短信验证码方式，miniOpenid可能为空
        String jwtId = UUID.randomUUID().toString(); //JWT 随机ID,做为验证的key

        stringRedisTemplate.delete(jwtUtils.JWT_SESSION_PREFIX + jwtId);

        JwtUser jwtUser = new JwtUser();
        jwtUser.setJwtId(jwtId);
        jwtUser.setUserId(String.valueOf(user.getId()));
        jwtUser.setPhone(user.getPhone());
        jwtUser.setTerminal(jwtUtils.getApplication());

        Map<String, String> userMap = (Map<String, String>) ObjectUtils.objectToMap(jwtUser);

        String token = jwtUtils.create(new HashMap<>(), userMap);

        stringRedisTemplate.opsForValue().set(jwtUtils.JWT_SESSION_PREFIX + jwtId, token, jwtUtils.getTtl(), TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 根据Token获取Customer
     */
    public User getByToken() {

        String token = SessionUtils.request().getHeader(BasicConstants.REQUEST_HEADERS_TOKEN);

        if (StringUtils.isNullOrEmpty(token)) return null;

        if (token.startsWith("Bearer")) token = token.replace("Bearer ", "");

        //解析token，反转成JwtUser对象
        Map<String, Object> userMap = jwtUtils.parseClaim(JwtUser.class, token);
        JwtUser jwtUser = null;
        try {
            jwtUser = (JwtUser) ObjectUtils.mapToObject(userMap, JwtUser.class);
        } catch (Exception e) {
            return null;
        }

        User user = new User();
        user.setId(StringUtils.isNotEmpty(jwtUser.getUserId()) ? Long.parseLong(jwtUser.getUserId()) : null);
        user.setPhone(jwtUser.getPhone());
        return user;
    }

}
