package cn.com.glsx.vasp.modules.model;

import cn.hutool.db.Page;
import lombok.Data;

/**
 * @author payu
 */
@Data
public class UserSearch extends Page {

    private String term;

}
