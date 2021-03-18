package cn.com.glsx.vasp.modules.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Accessors(chain = true)
@Data
@Entity
@Table(name = "smart_date")
public class DayInfo implements Serializable {
    @Id
    @Column(
            name = "id",
            unique = true,
            nullable = false
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "SELECT LAST_INSERT_ID()"
    )
    private Long id;

    /**
     * 当前阳历日期,如：2019-10-01
     */
    private String date;

    /**
     * 日期类型，为0表示工作日、为1节假日、为2双休日、3为调休日（上班）
     */
    private Integer daycode;

    /**
     * 节假日名称，如：国庆节
     */
    private String name;

    /**
     * 放假提示，如：10月1日至7日放假调休，共7天......	
     */
    private String tip;

    private static final long serialVersionUID = -8402343759691572906L;
}