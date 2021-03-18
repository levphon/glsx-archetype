package cn.com.glsx.smartdevice.model;

import lombok.Data;

/**
 * @author zhouhaibao
 * @date 2021/3/4 10:30
 */
@Data
public class ResponseBody {
    /**
     * 返回的结果
     */
    private boolean success;
    /**
     * 返回的数据
     */
    private String result;

    /**
     * 返回code
     */
    private Integer code;

    /**
     * 返回message
     */
    private String message;
}
