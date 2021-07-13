package cn.com.glsx.canal.modules.service;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhouhaibao
 * @date 2021/6/23
 */
@Service
public interface DataHandder {

    void proessData(List<CanalEntry.Column> afterColumnsList);
}
