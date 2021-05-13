package com.pilot.scouter.staticstic.service;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.pilot.scouter.common.constants.RedisConstants;
import com.pilot.scouter.common.model.Result;
import com.pilot.scouter.components.CommonResourceManager;
import com.pilot.scouter.staticstic.model.vo.ScouterOupt;
import com.pilot.scouter.staticstic.model.vo.TrnsSvcInfoDto;
import com.pilot.scouter.staticstic.model.vo.TrnsSvcInfoVo;
import com.pilot.scouter.utils.AESCrytoUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    public int TrnsInfo2Redis(Map<String, MultipartFile> files)  {


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

        Iterator<MultipartFile> itr = files.values().iterator();
        MultipartFile file = itr.next();
        List ld = null;

        /**
         * step 0 : 데이터 읽기
         * 읽어 오는 로직
         * */
        try {


            mappingIterator = csvMapper
                    .readerFor(ScouterOupt.CreateScouterOupt.class
                    ).with(bootstrap).readValues(file.getInputStream());

            mappingIterator.next();//reader(ScouterOupt.CreateScouterOupt.class).with(bootstrap).readValues(file.getInputStream());
            ld = mappingIterator.readAll();
            //System.out.println(ld.toString());

        } catch (IOException e) {
            System.out.println("catch :" + mappingIterator.toString());
            log.warn("fail file parse D:{}", file.getOriginalFilename());
            e.printStackTrace();
            return -1;
        }


//		/** 예외처리 고려 해야 됨
//		 *파일 입력 도중 끈킨다면 ? 1. 재입력 프로세스 정의 2. 실패 라인 로깅 3. 남은 데이터 저장
//		 */


        /**
         * step 1 : 데이터 저장
         * 읽어 온 파일 Stream 을 TrnsSvcInfoVo 리스트로 변환
         */

        List <TrnsSvcInfoVo> tmp =
                (List<TrnsSvcInfoVo>) ld.stream()
                        //.skip(1)
                        .map(data ->
                                resourcesManager.getModelMapper().map(data, TrnsSvcInfoVo.class)
                        )
                        .collect(Collectors.toList());


        /**
         * step 2 : 데이터 수정
         * 1. "," 가 들어있는 String을 "," 없이 만들기
         * 2. 비어있는 데이터 값 채우기
         *
         */

        // 필요 변수
        // (1) 날짜 지정
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMddHHmm");
        Date time = new Date();
        String time1 = format1.format(time);

        // (2) 타겟 날짜 지정
        String filename = file.getOriginalFilename();

        // (2) - 1 : 범위 잘라내기
        int startidx = filename.indexOf("[", 5);
        int middle = filename.indexOf("-", 5);
        int lastidx = filename.indexOf("]", 20);
        startidx++;

        // (2) - 2 : 파일 명이 형식에 맞는지 확인
        if ( lastidx < 0 || startidx < 0 ) {
            log.warn("파일 명이 알맞지 않은 문서입니다.");
            // 추후 에러처리 하기
            return 0;
        }

        // (2) - 3 : 날짜가 유효한지 확인
        String tgdt1 = filename.substring(startidx, middle);
        String tgdt2 = filename.substring(middle+1, lastidx);

        if ( ! tgdt1.substring(0,8).equals(tgdt2.substring(0,8)) ) {
            // 날짜가 같지 않다면

            if ( tgdt1.substring(9,12).equals(tgdt2.substring(9,12)) ) {
                // 근데 뒤의 값이 0000 이 아니라면 범위가 일치 하지 않음
                // 예외처리하기
                //XXX
                log.warn("파일 날짜 범위가 알맞지 않은 문서입니다.");
                return 0;
            }

        } else {
            // 날짜가 같다면
            if ( tgdt1.substring(9,12).equals("0000")  ) {
                // 근데 뒤의 값이 0000 이 아니라면 범위가 일치 하지 않음
                // 예외처리하기
                log.warn("파일 날짜 범위가 알맞지 않은 문서입니다.");
                return 0;
            }
            if ( tgdt2.substring(9,12).equals("2400")  ) {
                // 근데 뒤의 값이 0000 이 아니라면 범위가 일치 하지 않음
                // 예외처리하기
                log.info("파일 날짜 범위가 알맞지 않은 문서입니다.");
                return 0;
            }
        }



        tmp.stream()
                //.skip(1)
                .forEach(data -> {
                            data.setSvcAvgElap(data.getSvcAvgElap().replaceAll(",", ""));
                            data.setSvcErrCnt(data.getSvcErrCnt().replaceAll(",",""));
                            data.setSvcTotalElap(data.getSvcTotalElap().replaceAll(",",""));
                            data.setSvcCnt(data.getSvcCnt().replaceAll(",",""));
                            try {
                                data.setSvcUrl(aes.encrypt(data.getSvcNm()));
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            data.setSvcRgDt(time1);
                            data.setSvcTgDate(tgdt1.substring(0,8));



                            int isslace = data.getSvcNm().indexOf("/", 2);

                            if(isslace > 0){
                                data.setSvcNm(data.getSvcNm().substring(1,isslace));
                            }


                            try {
                                data.setSvcNm(aes.encrypt(data.getSvcNm()));
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            //	data.setSvcNm(data.getSvcNm().concat("_" + data.getSvcTgDate()));


                        }
                );

        /**x
         * step 3 : vo -> dto
         * 숫자로 바꾸는 로직
         */

        List <TrnsSvcInfoDto> vo = (List<TrnsSvcInfoDto>) tmp.stream()
                .map(
                        data -> resourcesManager.getModelMapper().map(data, TrnsSvcInfoDto.class)
                ).collect(Collectors.toList());


        log.info("vo : " + vo.toString());


        /**x
         * step 4 : redis 입력
         * 레디스에 가공한 데이터를 입력
         */


        //AtomicLong tmpCntSvc = new AtomicLong();
        vo.stream()
                .forEach(data -> {

                    // 키설정
                    String key = data.getSvcTgDate()+"_"+data.getSvcUrl();

                    // 레디스 입력 : 만료 3 개월
                    if ( !resourcesManager.getRedisCmd().hput(RedisConstants.TRNS_SCV_INFO + "_" + tgdt1.substring(0,8), key, data, 60 * 60 * 24 * 40 * 3) ) {
                        log.warn("fail data line = {}", data);
                    }
                });

        return 0;
    }

}

