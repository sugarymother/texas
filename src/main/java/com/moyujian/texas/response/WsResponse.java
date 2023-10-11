package com.moyujian.texas.response;

import com.moyujian.texas.constants.WsOperateType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class WsResponse<T> {

    private int type;

    private T data;

    public WsResponse(WsOperateType type) {
        this.type = type.getSerial();
    }

    public WsResponse(WsOperateType type, T data) {
        this.type = type.getSerial();
        this.data = data;
    }
}
