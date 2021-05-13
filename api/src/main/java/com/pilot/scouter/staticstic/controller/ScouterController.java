package com.pilot.scouter.staticstic.controller;

import com.pilot.scouter.staticstic.service.ApiService;
import com.pilot.scouter.staticstic.service.ScoutJsnService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/front/scouter")
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

    //TODO: 여기 수정필요
//    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity FileUploadController(ServerHttpRequest req, ServerHttpResponse res,
//                                               @RequestBody Flux<Part> parts) throws IOException {
//
//        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) req;
//
//        Map<String, MultipartFile> files = multiRequest.getFileMap();
//
//        int n_res = mApiService.TrnsInfo2Redis(files);
//
//        ResponseEntity result = null;
//        if (n_res < 0) result = new ResponseEntity(HttpStatus.BAD_REQUEST);
//        if (n_res == -10) result = new ResponseEntity(HttpStatus.BAD_REQUEST);
//
//
//        return result;
//    }

    /**
     * Json 가져오기
     */
    @GetMapping("/scoutUpdate")
    public ResponseEntity ScoutJsnController( ) {


        int res_n = 1;
        scoutJsnService.insertScoutDt();

        ResponseEntity result = null;
        if (res_n < 0) result = new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (res_n == -10) result = new ResponseEntity(HttpStatus.BAD_REQUEST);
        return result;
    }


    /**
     * Json 가져오기
     */
    @GetMapping("/scoutUpdateSql")
    public ResponseEntity ScoutJsnSqlController() {


        int res_n = 1;
        scoutJsnService.insertScoutDtSql();

        ResponseEntity result = null;
        if (res_n < 0) result = new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (res_n == -10) result = new ResponseEntity(HttpStatus.BAD_REQUEST);
        return result;
    }
}
