package com.glsx.plat.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompoundDropOptions {

    private String id;
    private String pId;
    private String name;

    private List<CompoundDropOptions> children;

    public CompoundDropOptions(String id, String pId, String name) {
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

}
