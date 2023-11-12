package com.moyujian.texas.logic.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Operate {

    private OperateType operateType;

    @JsonIgnore
    private int operateChips;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private int[] accessibleChips;

    public Operate(OperateType type) {
        this.operateType = type;
    }

    public Operate(OperateType type, int chips) {
        this.operateType = type;
        this.operateChips = chips;
    }

    public Operate(OperateType type, int[] accessibleChips) {
        this.operateType = type;
        this.accessibleChips = accessibleChips;
    }
}
