package com.moyujian.texas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyujian.texas.constants.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonResponse<T> {

    private int status;

    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> CommonResponse<T> suc(T data) {
        return new CommonResponse<>(0, "success", data);
    }

    public static <T> CommonResponse<T> suc() {
        return new CommonResponse<>(0, "success", null);
    }

    public static <T> CommonResponse<T> err() {
        return new CommonResponse<>(1, "error", null);
    }

    public static <T> CommonResponse<T> get(ResponseStatus responseStatus) {
        return new CommonResponse<>(responseStatus.getStatus(), responseStatus.getMsg(), null);
    }

    public static <T> CommonResponse<T> get(ResponseStatus responseStatus, T data) {
        return new CommonResponse<>(responseStatus.getStatus(), responseStatus.getMsg(), data);
    }
}
