package com.pilot.scouter.staticstic.model.vo;

import lombok.Data;

@Data
public class ScoutSvcReq {

    /** 시작 날짜 : 예) 202103240000 */
    String startYmdHm;

    /** 종료 날짜 : 예) 202103242400 */
    String endYmdHm;

}
