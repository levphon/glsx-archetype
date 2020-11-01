package com.glsx.plat.ai.baidu.baidu.model;

public class PersonVerifyResp {

    private boolean success;

    private String result;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PersonVerifyResp{" +
                "success=" + success +
                ", result='" + result + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}
