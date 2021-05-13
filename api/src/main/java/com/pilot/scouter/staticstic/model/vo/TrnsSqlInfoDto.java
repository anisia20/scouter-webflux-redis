package com.pilot.scouter.staticstic.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TrnsSqlInfoDto {

    /* sql 수집일자 */
    @NotNull
    private String sqlTgDate;

    /* sql 명 */
    @NotNull
    private String sqlNm;

    /* sql Contents */
    private String sqlContents;

    /* Sql 총 건수 */
    private Long sqlCnt;

    /* sql 에러 건수 */
    private Long sqlErrCnt;

    /*  sql 총 지연 */
    private Long sqlTotalElap;


    /* sql 평균 지연 */
    private Long sqlAvgElap;

    /* 등록 일시 */
    private String svcRgDt;
}

