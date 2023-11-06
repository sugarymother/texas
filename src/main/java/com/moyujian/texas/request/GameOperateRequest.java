package com.moyujian.texas.request;

import lombok.Data;

@Data
public class GameOperateRequest extends WsRequest {

    private OperateModel data;
}
