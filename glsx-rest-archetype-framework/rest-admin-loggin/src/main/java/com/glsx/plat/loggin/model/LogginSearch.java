package com.glsx.plat.loggin.model;

import cn.hutool.db.Page;
import lombok.Data;

@Data
public class LogginSearch extends Page {
    
    private String sDate;
    private String eDate;
    private String module;
    private String action;
    private String remark;
    private String operator;

}
