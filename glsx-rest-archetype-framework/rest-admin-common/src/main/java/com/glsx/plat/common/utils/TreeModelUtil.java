package com.glsx.plat.common.utils;

import com.glsx.plat.common.model.TreeModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 可以将有层级结构的list的数据转树结构
 */
@Slf4j
public class TreeModelUtil {

    private static final Log logger = LogFactory.getLog(TreeModelUtil.class);

    /**
     * 线性结构转树结构
     *
     * @param treeModels
     * @param rootNo
     * @return
     */
    public static List<? extends TreeModel> convert(List<? extends TreeModel> treeModels, String rootNo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //查找树的根
        List<TreeModel> roots = findRoots(treeModels, rootNo);
        if (CollectionUtils.isNotEmpty(roots)) {
            roots.stream().forEach(root -> findChildrens(root, treeModels));
        }
        stopWatch.stop();
        log.debug("线程结构转换树结构[递归]耗时:" + stopWatch.getTime() + " ms");
        return roots;
    }

    /**
     * 查找所有子节点
     *
     * @param root
     * @param treeModels
     * @return
     */
    private static List<TreeModel> findChildrens(TreeModel root, List<? extends TreeModel> treeModels) {
        if (CollectionUtils.isNotEmpty(treeModels)) {
            List<TreeModel> childrens = treeModels.stream().filter(treeModel -> root.getId().equals(treeModel.getParentId())).collect(Collectors.toList());
            root.setChildren(childrens);
            childrens.stream().forEach(children -> findChildrens(children, treeModels));
            return childrens;
        }
        return null;
    }

    /**
     * 查找所有的根节点
     *
     * @param treeModels
     * @param rootNo
     * @return
     */
    public static List<TreeModel> findRoots(List<? extends TreeModel> treeModels, String rootNo) {
        List<TreeModel> treeRoots = treeModels.stream().filter(treeModel -> treeModel.getId().equals(rootNo)).collect(Collectors.toList());
        return treeRoots;
    }

    /**
     * 快速转换树结构; 还可以把 root 提取出来,变复杂度为 2O(n); 目前复杂度 3O(n)
     *
     * @param treeModels
     * @return
     */
    public static List<? extends TreeModel> fastConvert(List<? extends TreeModel> treeModels, String rootNo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<Integer, ? extends TreeModel> treeIdMap = treeModels.stream().collect(Collectors.toMap(TreeModel::getId, treeModel -> treeModel));
        treeModels.stream().forEach(treeModel -> {
            TreeModel parentTreeModel = treeIdMap.get(((TreeModel) treeModel).getParentId());
            if (parentTreeModel != null) {
                parentTreeModel.getChildren().add(treeModel);
            }
        });

        //然后把 root 列表找出来
        List<? extends TreeModel> collect = treeModels.stream().filter(treeModel ->
                ((TreeModel) treeModel).getId().equals(rootNo)
        ).collect(Collectors.toList());
        stopWatch.stop();
        log.debug("线性结构转换树结构[fast]耗时:" + stopWatch.getTime() + " ms");
        return collect;
    }

    /**
     * 快速转换树结构; 还可以把 root 提取出来,变复杂度为 2O(n); 目前复杂度 3O(n)
     *
     * @param treeModels
     * @return
     */
    public static List<? extends TreeModel> fastConvert(List<? extends TreeModel> treeModels, Integer rootNo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<Integer, ? extends TreeModel> treeIdMap = treeModels.stream().collect(Collectors.toMap(TreeModel::getId, treeModel -> treeModel));
        treeModels.stream().forEach(treeModel -> {
            TreeModel parentTreeModel = treeIdMap.get(((TreeModel) treeModel).getParentId());
            if (parentTreeModel != null) {
                parentTreeModel.getChildren().add(treeModel);
            }
        });

        //然后把 root 列表找出来
        List<? extends TreeModel> collect = treeModels.stream().filter(treeModel -> (treeModel).getId().equals(rootNo)
        ).collect(Collectors.toList());
        stopWatch.stop();
        log.debug("线性结构转换树结构[fast]耗时:" + stopWatch.getTime() + " ms");
        return collect;
    }

}
