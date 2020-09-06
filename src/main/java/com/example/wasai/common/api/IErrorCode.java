package com.example.wasai.common.api;

/**
 * 封装API的错误码
 * lzh 2018-8-29
 */
public interface IErrorCode {
    long getCode();

    String getMessage();
}
