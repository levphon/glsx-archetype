package cn.com.glsx.admin.modules.user.service;

import cn.com.glsx.admin.common.exception.AdminException;
import cn.com.glsx.admin.modules.user.utils.JwtUser;
import cn.com.glsx.admin.services.userservice.model.UserSearch;
import com.github.pagehelper.PageInfo;
import com.glsx.plat.common.utils.StringUtils;
import com.glsx.plat.core.constant.BasicConstants;
import com.glsx.plat.exception.SystemMessage;
import com.glsx.plat.jwt.util.JwtUtils;
import com.glsx.plat.jwt.util.ObjectUtils;
import com.glsx.plat.web.utils.SessionUtils;
import com.glsx.vasp.modules.entity.User;
import com.glsx.vasp.modules.mapper.UserMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

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
        String jwtId = UUID.randomUUID().toString(); //JWT 随机ID,做为验证的key

        JwtUser jwtUser = new JwtUser();
        jwtUser.setJwtId(jwtId);
        jwtUser.setUserId(String.valueOf(user.getId()));
        jwtUser.setPhone(user.getPhone());
        jwtUser.setApplication(jwtUtils.getApplication());

        Map<String, String> userMap = (Map<String, String>) ObjectUtils.objectToMap(jwtUser);

        return jwtUtils.createToken(jwtId, userMap);
    }

    /**
     * 根据Token获取Customer
     */
    public User getByToken() {

        String token = SessionUtils.request().getHeader(BasicConstants.REQUEST_HEADERS_TOKEN);

        if (StringUtils.isNullOrEmpty(token)) throw new AdminException(SystemMessage.ILLEGAL_ACCESS.getCode(), "登录已失效");

        if (token.startsWith("Bearer")) token = token.replace("Bearer ", "");

        //解析token，反转成JwtUser对象
        Map<String, Object> userMap = jwtUtils.parseClaim(JwtUser.class, token);
        JwtUser jwtUser = null;
        try {
            jwtUser = (JwtUser) ObjectUtils.mapToObject(userMap, JwtUser.class);
        } catch (Exception e) {
            throw new AdminException(SystemMessage.ILLEGAL_ACCESS.getCode(), "登录已失效");
        }
        User user = new User();
        user.setId(StringUtils.isNotEmpty(jwtUser.getUserId()) ? Long.parseLong(jwtUser.getUserId()) : null);
        user.setPhone(jwtUser.getPhone());
        return user;
    }

}
