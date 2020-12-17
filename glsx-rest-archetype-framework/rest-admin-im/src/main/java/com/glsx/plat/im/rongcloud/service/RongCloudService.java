package com.glsx.plat.im.rongcloud.service;

import com.glsx.plat.im.rongcloud.util.RongCloudUtil;
import io.rong.methods.user.User;
import io.rong.methods.user.onlinestatus.OnlineStatus;
import io.rong.models.response.CheckOnlineResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RongCloudService {

    @Autowired
    private RongCloudUtil utils;

    public TokenResult register(UserModel userModel) throws Exception {
        User user = utils.getRongCloud().user;
        return user.register(userModel);
    }

    public CheckOnlineResult checkOnline(String userId) {
        OnlineStatus onlineStatus = utils.getRongCloud().user.onlineStatus;
        try {
            return onlineStatus.check(new UserModel().setId(userId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isOnline(String userId) {
        CheckOnlineResult result = this.checkOnline(userId);
        if (utils.isSuccess(result)) {
            assert result != null;
            return "1".equals(result.getStatus());
        }
        return false;
    }

}
