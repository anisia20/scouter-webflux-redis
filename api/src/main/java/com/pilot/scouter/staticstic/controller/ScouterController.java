package com.pilot.scouter.staticstic.controller;

import com.pilot.scouter.staticstic.service.ApiService;
import com.pilot.scouter.staticstic.service.ScoutJsnService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
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
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<Object> FileUploadController  (
            @RequestPart("files") FilePart filePart)  throws IOException{
            log.debug(filePart.filename());
            Path tempFile = Files.createTempFile("test", filePart.filename());

            AsynchronousFileChannel channel =
                    AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);
            DataBufferUtils.write(filePart.content(), channel, 0)
                    .doOnComplete(() -> {
                        log.debug("finish");
                    })
                    .subscribe();

            filePart.transferTo(tempFile.toFile());
            log.debug(tempFile.toString());
            return mApiService.trnsInfo2Redis(tempFile);
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
