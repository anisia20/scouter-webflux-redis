package com.pilot.scouter.staticstic.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class TrnsSvcAvailVo {

    /**
     * 서비스 수집일자
     */
    @NotNull
    private String svcTgDate;


    /**
     * 서비스 불가 시간 (분 단위)
     */
    private Long svcUnAvail;
}
