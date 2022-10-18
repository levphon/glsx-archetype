package com.glsx.plat.common.model;

import java.util.List;

public interface TreeModel<T> {

    /**
     * 获取编号
     *
     * @return
     */
    Object getId();

    /**
     * 父级编号
     *
     * @return
     */
    Object getParentId();

    /**
     * 获取文本
     *
     * @return
     */
    String getLabel();

    /**
     * 根标识
     *
     * @return
     */
    Integer isRoot();

    /**
     * 层级深度
     *
     * @return
     */
    Integer getDepth();

    /**
     * 排序
     *
     * @return
     */
    Integer getOrder();

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
