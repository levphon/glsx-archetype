package com.glsx.plat.document.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析监听器，
 * 每解析一行会回调invoke()方法。
 * 整个excel解析结束会执行doAfterAllAnalysed()方法
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelListener<T> extends AnalysisEventListener<T> {

    /**
     * 自定义用于暂时存储data。
     * 可以通过实例获取该值
     */
    private List<T> datas = new ArrayList<>();

    /**
     * 自定义存储标题结果
     */
    private T titleMap;

    /**
     * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
     *
     * @param result
     * @param context
     */
    @Override
    public void invoke(T result, AnalysisContext context) {
        //获取当前行号
        Integer rowIndex = context.readRowHolder().getRowIndex();
        if (rowIndex == 0) {
            this.titleMap = result;
        } else {
            //数据存储到list，供批量处理，或后续自己业务逻辑处理
            doSomething();
            datas.add(result);
        }
        log.info("解析数据第{}行，数据为：{}", rowIndex, result);
    }

    /**
     * 根据业务自行实现该方法
     */
    private void doSomething() {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // list.clear();//解析结束销毁不用的资源
        // 处理业务数据插入表 还是校验等等
        log.info("解析完成！");
    }

}
