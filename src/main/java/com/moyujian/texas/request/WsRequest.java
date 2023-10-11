package com.moyujian.texas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WsRequest<T> {

    private int type;

    private T Data;
}
