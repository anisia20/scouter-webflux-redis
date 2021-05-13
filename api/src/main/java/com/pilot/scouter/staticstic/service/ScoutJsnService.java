package com.pilot.scouter.staticstic.service;

import com.pilot.scouter.components.CommonResourceManager;
import com.pilot.scouter.staticstic.model.ResponseScouter;
import com.pilot.scouter.staticstic.model.ResponseSqlScouter;
import com.pilot.scouter.staticstic.model.vo.ScoutSvcReq;
import com.pilot.scouter.staticstic.model.vo.TrnsSqlInfoDto;
import com.pilot.scouter.staticstic.model.vo.TrnsSvcInfoDto;
import com.pilot.scouter.utils.AESCrytoUtil;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Log4j2
@Service
public class ScoutJsnService {

    private final WebClient webClient;

    @Value("${scouter.url:'127.0.0.1:6800'}")
    private String scouterUrl;


    @Autowired
    CommonResourceManager resourcesManager;


    @Autowired
    AESCrytoUtil aes;



    public ScoutJsnService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(scouterUrl)
                .build();

    }

    /**
     * 스카우터에서 서비스 데이터를 조회하는 서비스
     *
     * @param endYmdHm
     * @param startYmdHm
     */
    public ResponseScouter selectScoutDt(String endYmdHm, String startYmdHm) {
        ResponseScouter response =
                this.webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1/summary/service/ofType/tomcat")
                                //.queryParam("objType","tomcat")
                                .queryParam("endYmdHm", endYmdHm)
                                .queryParam("startYmdHm", startYmdHm)
                                .build()
                        )
                        .retrieve()
                        .bodyToMono(ResponseScouter.class)
                        .block();

        log.info(response);
        return response;
    }


    /**
     * 스카우터에서 SQL 데이터를 조회하는 서비스
     *
     * @param endYmdHm
     * @param startYmdHm
     */
    public ResponseSqlScouter selectScoutSqlDt(String endYmdHm, String startYmdHm) {
        ResponseSqlScouter response =
                this.webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1/summary/sql/ofType/tomcat")
                                .queryParam("objType","tomcat")
                                .queryParam("endYmdHm", endYmdHm)
                                .queryParam("startYmdHm", startYmdHm)
                                .build()
                        )
                        .retrieve()
                        .bodyToMono(ResponseSqlScouter.class)
                        .block();

        log.info(response);
        return response;
    }

    /**
     * 3일 동안의 날짜 조회 함수
     *
     * @return
     */
    public List<ScoutSvcReq> getDt() {
        //TODO
        // 날짜 조회 3
        List<ScoutSvcReq> dtRq = new ArrayList<>();

        // (1) 오늘날짜 가져오기
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMdd");

        Calendar cal = Calendar.getInstance();

        String today = null;
        ScoutSvcReq tmp;
        // 3일 동안의 날짜 집어 넣기

        for (int i = 0; i < 3; i++) {
            tmp = new ScoutSvcReq();
            cal.add(Calendar.DATE, -1);
            today = sdformat.format(cal.getTime());
            tmp.setStartYmdHm(today + "0000");
            tmp.setEndYmdHm(today + "2400");
            dtRq.add(tmp);

        }

        return dtRq;
    }


    public void insertScoutDt() {
        //TODO
        // 지정한 날짜의 데이터를 있으면 넣지 않고, 없으면 집어 넣음
        List<ScoutSvcReq> dtRq = getDt();
        List tmpkey = null;
        TrnsSvcInfoDto redisData;
        // 레디스에 있는지 확인
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String today = sdformat.format(cal.getTime());
        //1. 스카우터 서버 데이터 추출
        List<TrnsSvcInfoDto> redisDataList = new ArrayList<>();
        for (ScoutSvcReq dt : dtRq) {
            //tmpkey = resourcesManager.getRedisCmd().getKeyList("TRNS_SCV_INFO" + dt.getEndYmdHm().substring(8));
            //if (tmpkey != null || tmpkey.isEmpty()) {
            selectScoutDt(dt.getEndYmdHm(), dt.getStartYmdHm())
                    .getResult()
                    .get("itemMap")
                    .values()
                    .stream()
                    .forEach(rs -> {
                        try {
                            TrnsSvcInfoDto tmp = new TrnsSvcInfoDto();
                            tmp.setSvcTgDate(dt.getEndYmdHm().substring(0, 8));
                            tmp.setSvcCnt(Long.parseLong(rs.getCount()));
                            tmp.setSvcUrl(aes.encrypt(rs.getSummaryKeyName()));
                            tmp.setSvcErrCnt(Long.parseLong(rs.getErrorCount()));
                            tmp.setSvcTotalElap(Long.parseLong(rs.getElapsedSum()));
                            tmp.setSvcAvgElap(Long.parseLong(rs.getElapsedSum()) / Long.parseLong(rs.getCount()));
                            tmp.setSvcNm(rs.getSummaryKeyName() == null || rs.getSummaryKeyName().indexOf("/", 2) == -1
                                    ? aes.encrypt(rs.getSummaryKeyName())
                                    : aes.encrypt(rs.getSummaryKeyName().substring(1, rs.getSummaryKeyName().indexOf("/", 2))));
                            tmp.setSvcRgDt(today);
                            redisDataList.add(tmp);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    });
        }


        //2. 레디스 입력
        //
        for (TrnsSvcInfoDto indata : redisDataList) {
            if (resourcesManager.getRedisCmd().hput("TRNS_SCV_INFO" + "_" + indata.getSvcTgDate(),
                    indata.getSvcTgDate() + "_" + indata.getSvcUrl(), indata, 60 * 60 * 24 * 40 * 3) == false) {
                log.warn("fail data line = {}", indata);
            }

        }
    }

    public void insertScoutDtSql() {

        List<ScoutSvcReq> dtRq = getDt();


        SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String today = sdformat.format(cal.getTime());


        //1. 스카우터 서버 데이터 추출
        List<TrnsSqlInfoDto> redisDataList = new ArrayList<>();

        for (ScoutSvcReq dt : dtRq) {

            selectScoutSqlDt(dt.getEndYmdHm(), dt.getStartYmdHm())
                    .getResult()
                    .get("itemMap")
                    .values()
                    .stream()
                    .forEach(rs -> {

                        TrnsSqlInfoDto tmp = new TrnsSqlInfoDto();
                        tmp.setSqlTgDate(dt.getEndYmdHm().substring(0, 8));
                        tmp.setSqlCnt(Long.parseLong(rs.getCount()));
                        tmp.setSqlContents(rs.getSummaryKeyName());
                        tmp.setSqlErrCnt(Long.parseLong(rs.getErrorCount()));
                        tmp.setSqlTotalElap(Long.parseLong(rs.getElapsedSum()));
                        tmp.setSqlAvgElap(Long.parseLong(rs.getElapsedSum()) / Long.parseLong(rs.getCount()));
                        tmp.setSqlNm( rs.getSummaryKeyName().indexOf("/*") == -1 || rs.getSummaryKeyName().indexOf("*/") == -1
                                ? rs.getSummaryKeyName()
                                : rs.getSummaryKeyName().substring(rs.getSummaryKeyName().indexOf("/*")+3, rs.getSummaryKeyName().indexOf("*/")));
                        tmp.setSvcRgDt(today);
                        redisDataList.add(tmp);

                    });

        }


        //2. 레디스 입력
        //
        for (TrnsSqlInfoDto indata : redisDataList) {
            if (resourcesManager.getRedisCmd().hput("TRNS_SQL_INFO" + "_" + indata.getSqlTgDate(),
                    indata.getSqlAvgElap() +"_" + indata.getSqlTgDate() + "_" + indata.getSqlNm(), indata, 60 * 60 * 24 * 30 * 2) == false) {
                log.warn("fail data line = {}", indata);
            }

        }
    }
}
