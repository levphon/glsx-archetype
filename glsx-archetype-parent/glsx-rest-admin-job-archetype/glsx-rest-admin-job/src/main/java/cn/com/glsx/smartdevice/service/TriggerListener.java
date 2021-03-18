package cn.com.glsx.smartdevice.service;

import cn.com.glsx.vasp.modules.entity.ScenseData;


public interface TriggerListener {
    boolean getFlag(ScenseData condition);
}
