package cn.com.glsx.admin.services.userservice.model;

import cn.hutool.db.Page;
import lombok.Data;

/**
 * @author payu
 */
@Data
public class UserSearch extends Page {

    private String term;

}
