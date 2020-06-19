package com.glsx.plat.common.model;

import java.util.List;

public interface TreeModel<T> {
    /**
     * 获取编号
     *
     * @return
     */
    Integer getId();

    /**
     * 父级编号
     *
     * @return
     */
    Integer getParentId();

    /**
     * 获取文本
     *
     * @return
     */
    String getLabel();

    /**
     * 选中状态：true选中，false未选中
     *
     * @return
     */
    boolean checked();

    /**
     * 获取当前原始对象
     *
     * @return
     */
    T getOrigin();

    List<? extends TreeModel> getChildren();

    void setChildren(List<? extends TreeModel> data);

}
