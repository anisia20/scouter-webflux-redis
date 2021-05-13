package com.pilot.scouter.staticstic.controller;

import com.pilot.scouter.common.model.Result;
import com.pilot.scouter.staticstic.service.RedisSqlService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Log4j2
@Controller
@RequestMapping("/redisSql")
public class RedisSqlController {

    @Autowired
    private RedisSqlService redisSqlService;

    @GetMapping("/scoutSqlStat")
    public Mono<Object> scoutSvcStat( ) {


        Result result = redisSqlService.getScoutSqlStat(30, "20210511");

        return Mono.just(result);
    }
}