package com.moyujian.texas.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperateModel {

    private int type;

    private int chipNum;
}
