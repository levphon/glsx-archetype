package cn.com.glsx.storage.modules.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "t_storage")
public class Storage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(name = "commodity_code")
    private String commodityCode;

    private String name;

    private Integer count;

}