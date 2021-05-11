package com.glsx.plat.common.model;

import com.google.common.net.InternetDomainName;
import lombok.Data;

import java.util.List;

/**
 * @author zhouhaibao
 * @date 2021/5/10 10:58
 */
@Data
public class PageDto<T> {

    private int totalElements;
    private int totalPages;
    private List<T> list;
}
