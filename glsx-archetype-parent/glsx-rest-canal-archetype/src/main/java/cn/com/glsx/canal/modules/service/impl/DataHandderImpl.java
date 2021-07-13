package cn.com.glsx.canal.modules.service.impl;

import cn.com.glsx.canal.modules.convert.CanalDataHandler;
import cn.com.glsx.canal.modules.entity.TestCanal;
import cn.com.glsx.canal.modules.entity.TestCanalSource;
import cn.com.glsx.canal.modules.mapper.TestCanalMapper;
import cn.com.glsx.canal.modules.service.DataHandder;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhouhaibao
 * @version 1.0
 * @description: 测试数据处理
 * @date 2021/6/23 14:54
 */
@Service("testImpl")
public class DataHandderImpl implements DataHandder {

    @Autowired
    private TestCanalMapper testCanalMapper;

    @Override
    public void proessData(List<CanalEntry.Column> afterColumnsList) {
        TestCanalSource testCanalSource = CanalDataHandler.ConvertToBean(afterColumnsList, TestCanalSource.class);
        // 业务处理
        TestCanal testCanal=testCanalMapper.findTestBySn(testCanalSource.getSn());
        if(testCanal==null) {
            testCanal = new TestCanal();
            testCanal.setName(testCanalSource.getName());
            testCanal.setSn(testCanalSource.getSn());
            testCanalMapper.insert(testCanal);
        }else{
            testCanal.setName(testCanalSource.getName());
            testCanalMapper.updateByPrimaryKeySelective(testCanal);
        }
    }
}
