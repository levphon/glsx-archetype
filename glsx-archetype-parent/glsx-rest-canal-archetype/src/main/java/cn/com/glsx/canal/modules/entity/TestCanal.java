package cn.com.glsx.canal.modules.entity;

import lombok.Data;

import java.io.Serializable;
import javax.persistence.*;

/**
 * @author zhouhaibao
 * @version 1.0
 * @description: 新库的实体类
 * @date 2021/6/23 14:36
 */
@Table(name = "test_canal")
@Data
public class TestCanal implements Serializable {

    @Id
    private Integer id;

    private String sn;

    private String name;

}