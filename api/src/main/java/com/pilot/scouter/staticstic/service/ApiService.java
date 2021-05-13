package com.pilot.scouter.staticstic.service;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.pilot.scouter.common.code.ResultCode;
import com.pilot.scouter.common.constants.RedisConstants;
import com.pilot.scouter.common.model.Result;
import com.pilot.scouter.components.CommonResourceManager;
import com.pilot.scouter.staticstic.model.vo.ScouterOupt;
import com.pilot.scouter.staticstic.model.vo.TrnsSvcInfoDto;
import com.pilot.scouter.staticstic.model.vo.TrnsSvcInfoVo;
import com.pilot.scouter.utils.AESCrytoUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ApiService {

    @Autowired
    AESCrytoUtil aes;

    @Autowired
    CommonResourceManager resourcesManager;

    /** 멀티파트 형식의 파일을 java file  로 컨버팅
     * @throws IOException */

    public File convert(MultipartFile file) throws IOException
    {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }


    /**
     * 상품 등록 REDIS 큐 저장
     *
     * @param
     * @return
     */

    @SuppressWarnings("unchecked")
    public int TrnsInfo2Redis(Flux<FilePart> files)  {


        CsvSchema bootstrap = CsvSchema.builder()
                .addColumn("svcNm", CsvSchema.ColumnType.STRING)
                .addColumn("svcCnt", CsvSchema.ColumnType.STRING)
                .addColumn("svcErrCnt", CsvSchema.ColumnType.STRING)
                .addColumn("svcTotalElap", CsvSchema.ColumnType.STRING)
                .addColumn("svcAvgElap", CsvSchema.ColumnType.STRING)
                .addColumn("totalCpu", CsvSchema.ColumnType.STRING)
                .addColumn("avgCpu", CsvSchema.ColumnType.STRING)
                .addColumn("totalMem", CsvSchema.ColumnType.STRING)
                .addColumn("avgMem", CsvSchema.ColumnType.STRING)
                .build();


        //emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<?, ?>> mappingIterator = null;

        //Iterator<MultipartFile> itr = files.values().iterator();
        //MultipartFile file = itr.next();
        List ld = null;

        return 0;
    }

}

