package cn.com.glsx.admin.modules.model;

import cn.hutool.db.Page;
import lombok.Data;

/**
 * @author payu
 */
@Data
public class UserSearch extends Page {

    private String term;

}
