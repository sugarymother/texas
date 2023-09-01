package com.moyujian.texas.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {

    private String username;

    private Integer rechargeChips;
}
