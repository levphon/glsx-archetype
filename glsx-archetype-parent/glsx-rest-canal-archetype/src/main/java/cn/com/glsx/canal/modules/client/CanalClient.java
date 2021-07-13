package cn.com.glsx.canal.modules.client;

import cn.com.glsx.canal.common.bussinessFactory.BussinessFactory;
import cn.com.glsx.canal.config.CanalConfig;
import cn.com.glsx.canal.modules.service.DataHandder;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouhaibao
 * @version 1.0
 * @description: 增量数据通道
 * @date 2021/6/23 14:36
 */
@Component()
public class CanalClient implements InitializingBean {

    private final static int BATCH_SIZE = 1000;

    @Autowired
    private CanalConfig canalConfig;

    @Autowired
    private DataHandder dataHandder;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("开始监控--------------");
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalConfig.getIp(), canalConfig.getPort()), canalConfig.getDestination(), "", "");
        try {
            //打开连接
            connector.connect();
            //回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
            connector.rollback();
            while (true) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(BATCH_SIZE, (long) 1, TimeUnit.SECONDS);
                //获取批量ID
                long batchId = message.getId();
                //获取批量的数量
                int size = message.getEntries().size();
                //如果没有数据
                if (batchId == -1 || size == 0) {
                    try {
                        //线程休眠2秒
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //如果有数据,处理数据
                    printEntry(message.getEntries());
                }
                //进行 batch id 的确认。确认之后，小于等于此 batchId 的 Message 都会被确认。
                connector.ack(batchId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connector.disconnect();
        }
    }

    /**
     * 打印canal server解析binlog获得的实体类信息
     */
    private static void printEntry(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                //开启/关闭事务的实体类型，跳过
                continue;
            }
            //RowChange对象，包含了一行数据变化的所有特征
            //比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
            CanalEntry.RowChange rowChage;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }
            //获取操作类型：insert/update/delete类型
            CanalEntry.EventType eventType = rowChage.getEventType();
            //打印Header信息
            System.out.println(String.format("================》; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));
            //判断是否是DDL语句
            if (rowChage.getIsDdl()) {
                System.out.println("================》;isDdl: true,sql:" + rowChage.getSql());
            }
            String tableName = entry.getHeader().getTableName();
            DataHandder instance = BussinessFactory.getInstance(tableName, DataHandder.class);
            if(null==instance){
                continue;
            }
            //获取RowChange对象里的每一行数据，打印出来
            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                //如果是删除语句
                if (eventType == CanalEntry.EventType.DELETE) {
                    // 暂时不处理
                } else if (eventType == CanalEntry.EventType.INSERT||eventType == CanalEntry.EventType.UPDATE) {
                    instance.proessData(rowData.getAfterColumnsList());
                }
            }
        }
    }
}

