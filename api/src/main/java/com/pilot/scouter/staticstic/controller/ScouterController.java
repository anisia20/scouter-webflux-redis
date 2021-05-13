package com.pilot.scouter.staticstic.controller;

import com.pilot.scouter.staticstic.service.ApiService;
import com.pilot.scouter.staticstic.service.ScoutJsnService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/v1/scouter")
public class ScouterController {

    /**
     * api 처리 서비스
     */
    @Autowired
    ApiService mApiService;


    @Autowired
    private ScoutJsnService scoutJsnService;


    /**
     * 파일 선택 서비스
     *
     * @throws IOException
     * @throws Exception
     */

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Object> FileUploadController(
            @RequestPart("files") Flux<FilePart> filePartFux)  {

        int n_res = mApiService.TrnsInfo2Redis(filePartFux);


        return Mono.just("");
    }

    /**
     * Json 가져오기
     */
    @GetMapping("/scoutUpdate")
    public Mono<Object>  ScoutJsnController( ) {
        scoutJsnService.insertScoutDt();
        return Mono.just("");
    }


    /**
     * Json 가져오기
     */
    @GetMapping("/scoutUpdateSql")
    public Mono<Object> ScoutJsnSqlController() {
        scoutJsnService.insertScoutDtSql();
        return Mono.just("");
    }
}
