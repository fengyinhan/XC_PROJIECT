package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

public class CustomException extends RuntimeException {

    //错误代码
    private ResultCode resultCode;

    public CustomException(ResultCode resultCode){
        //异常信息为错误代码+异常信息
        super("错误代码: "+resultCode.code()+" 错误信息:"+resultCode.message());
        this.resultCode = resultCode;
    }

    //获取错误信息

    public ResultCode getResultCode() {
        return this.resultCode;
    }
}
