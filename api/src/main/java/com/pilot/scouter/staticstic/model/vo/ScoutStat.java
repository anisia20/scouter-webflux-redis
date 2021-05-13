package com.pilot.scouter.staticstic.model.vo;

import lombok.Data;

@Data
public class ScoutStat {

    /**
     * redis 에 있는 날짜별 데이터를 읽고, 응답지연 율, 에러율, 평균처리량, 가용율,
     * 총 트렌젝션 수, 에러수 총 지연시간, 응답지연 건수를 반환
     */
    public ScoutStat() {
        this.tgDate = "";
        this.resDlyRate = 0.0;
        this.errRate = 0.0;
        this.tps = 0.0;
        this.avlAbility = 0.0;
        this.totalTrnCnt = Long.valueOf(0);
        this.errCnt = Long.valueOf(0);
        this.totalDlyTime = Long.valueOf(0);
        this.resDlyCnt = Long.valueOf(0);
        this.svcUnAvail = Long.valueOf(0);
    }

    private String tgDate;

    /**
     * 응답지연 율
     */
    private double resDlyRate;

    /**
     * 에러율
     */
    private double errRate;


    /**
     * tps : 평균 처리량
     */
    private double tps;


    /**
     * 가용률
     */
    private double avlAbility;

    /**
     * 총 트렌젝션 수
     */
    private Long totalTrnCnt;


    /**
     * 에러 수
     */
    private Long errCnt;


    /**
     * 총 지연시간
     */
    private Long totalDlyTime;

    /**
     * 응답 지연 건수
     */
    private Long resDlyCnt;


    /**
     * 서비스 불가 분
     */
    private Long svcUnAvail;

}
