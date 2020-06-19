package com.glsx.plat.core.web;

import lombok.Data;

import java.util.List;

@Data
public class Pagination<T> {

    private long total;
    private List<T> list;

    public Pagination(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

}
