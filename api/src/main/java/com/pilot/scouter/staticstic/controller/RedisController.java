package com.pilot.scouter.staticstic.controller;

import com.pilot.scouter.common.model.Result;
import com.pilot.scouter.staticstic.model.vo.TrnsSvcAvailVo;
import com.pilot.scouter.staticstic.service.RedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;

@Log4j2
@Controller
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    RedisService redisService;


    /**
     * 스카우터 전체 통계
     * 설명 : 기존 액셀  모양과 같은 화면에 뿌려주는 api
     *
     * @param dtime
     */

    @GetMapping("/scoutTotalStat")
    public Mono<Object> scoutTotalStat() {
        String redisKey = "TRNS_SCV_INFO";
        Result result = redisService.getScoutTotalStat(redisKey); //redisService.getRedisHashKeys(redisKey);
        return Mono.just(result);
    }


    /**
     * 스카우터 최근 7 통계 (평균)
     * 계산하여 표현하기
     * <p>
     * 내용 : redis 에 있는 날짜별 데이터를 읽고, 응답지연 율, 에러율, 평균처리량, 가용율, 총 트렌젝션 수, 에러수 총 지연시간, 응답지연 건수를 반환
     */
    @GetMapping("/scoutAvgSevStat")
    public Mono<Object> scoutAvgSevStat() throws ParseException {

        Result result = redisService.getScoutAvgSevStat();

        return Mono.just(result);
    }


    /**
     * 스카우터 날짜별 통계 (평균)
     * 계산하여 표현하기
     * <p>
     * 내용 : redis 에 있는 날짜별 데이터를 읽고, 응답지연 율, 에러율, 평균처리량, 가용율, 총 트렌젝션 수, 에러수 총 지연시간, 응답지연 건수를 반환
     */
    @GetMapping("/scoutAvgStat")
    public Mono<Object> scoutAvgStat() {

        Result result = redisService.getScoutAvgStat();
        return Mono.just(result);
    }

    /**
     * 스카우터 서비스 별 통계
     * 계산하여 표현하기
     * <p>
     * 내용 : redis 에 있는 서비스 별 데이터를 읽고, 응답지연 율, 에러율, 평균처리량, 가용율, 총 트렌젝션 수, 에러수 총 지연시간, 응답지연 건수를 반환
     */
    @GetMapping("/scoutSvcStat")
    public Mono<Object> scoutSvcStat() {

        Result result = redisService.getScoutSvcStat();

        return Mono.just(result);
    }

    /**
     * 스카우터 서비스 불가 시간 수정 (분 단위)
     */

    @PostMapping("/scUnAvail")
    public Mono<Object> setSvcAvail(
            @RequestBody TrnsSvcAvailVo data,
            @ApiIgnore ServerHttpRequest request,
            @ApiIgnore ServerHttpResponse response
            ) {
        Result result = redisService.setSvcAvail(data);

        return Mono.just(result);
    }
}
