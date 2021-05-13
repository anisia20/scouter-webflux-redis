package com.pilot.scouter.staticstic.service;

import com.pilot.scouter.common.constants.RedisConstants;
import com.pilot.scouter.common.model.Result;
import com.pilot.scouter.components.CommonResourceManager;
import com.pilot.scouter.staticstic.model.vo.ScoutStat;
import com.pilot.scouter.staticstic.model.vo.TrnsSvcAvailVo;
import com.pilot.scouter.staticstic.model.vo.TrnsSvcInfoDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@Log4j2
@Service
public class RedisService {


    @Autowired
    CommonResourceManager resourcesManager;


    public Result getScoutTotalStat(String redisKey) {
        Result result = new Result();

        // 레디스에 해당하는 전체키 반환
        Map<Object, Object> mp = resourcesManager.getRedisCmd().hgetAll(redisKey);

        log.debug("mp : " + mp.toString());

        result.setSuccess(mp);
        return result;
    }

    public Result getScoutAvgStat() {

        // 조회할 데이터를 임시로 담고
        Result result = new Result();

        //오늘 날짜
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        Date time = new Date();
        String time1 = format1.format(time);

        // 앞에서 계산한 오늘 날짜로 부터 3개월 정도 정보 조회
        // 레디스에 해당하는 전체키 반환
        String redisKey = "TRNS_SCV_INFO";


        Map<Object, Object> res = new HashMap<>();
        Map<Object, Object> mp;


        // 전체 키 조회
        List<String> keyList = resourcesManager.getRedisCmd().getKeyList("TRNS_SCV_INFO*");

        Collections.sort(keyList);
        // 각 키에 맞는 데이터 조회 후 계산

        if (keyList.size() == 0) {
            result.setResultFail();
            return result;
        }
        for (String obj : keyList) {

            ScoutStat stat = new ScoutStat();

            mp = resourcesManager.getRedisCmd().hgetAll((obj));


            mp.forEach(
                    (key, value) -> {

                        TrnsSvcInfoDto tmpStat = resourcesManager.getModelMapper().map(value, TrnsSvcInfoDto.class);
                        stat.setTotalTrnCnt(stat.getTotalTrnCnt() + tmpStat.getSvcCnt());
                        stat.setErrCnt(stat.getErrCnt() + tmpStat.getSvcErrCnt());
                        stat.setTotalDlyTime(stat.getTotalDlyTime() + tmpStat.getSvcTotalElap());
                        if (tmpStat.getSvcAvgElap() >= 1000) {
                            stat.setResDlyCnt(stat.getResDlyCnt() + tmpStat.getSvcCnt());
                        }

                    }
            );

            log.info(" 총 트렌젝션 수 " + stat.getTotalTrnCnt());
            log.info(" 에러 수 " + stat.getErrCnt());
            log.info(" 총 지연시간 " + stat.getTotalDlyTime());
            log.info(" 총 응답지연 건수 " + stat.getResDlyCnt());
            double ttmp = (double) ((double) stat.getResDlyCnt() / stat.getTotalTrnCnt());
            long tmplong = Math.round(ttmp * 100000);

            // 응답 지연 율 계산
            stat.setResDlyRate(tmplong / 1000.0);

            stat.setTgDate(obj);
            stat.setTgDate(stat.getTgDate().substring(14));


            // 에러율
            stat.setErrRate((Math.round((stat.getErrCnt() / (double) (stat.getTotalTrnCnt())) * 100000)) / 1000.0);

            // 평균 처리량
            stat.setTps(((Math.round((double) stat.getTotalTrnCnt() / (24 * 60 * 60)) * 1000)) / 1000.0);


            Object oobj = resourcesManager.getRedisCmd().get("TRNS_SCV_UNAVAIL_" + stat.getTgDate());

            if (oobj != null && oobj.toString().isEmpty() == false) {
                TrnsSvcAvailVo vvp = resourcesManager.getModelMapper().map(oobj, TrnsSvcAvailVo.class);
                stat.setSvcUnAvail(vvp.getSvcUnAvail());
            }

            stat.setAvlAbility(Math.round((1 - ((double) stat.getSvcUnAvail() / (60 * 60))) * 10000) / 100.0);

            res.put(obj, stat);

        }


        result.setSuccess(res);

        return result;
    }


    /**
     *
     * @return
     */
    public Result getScoutSvcStat() {

        // 조회할 데이터를 임시로 담고
        Result result = new Result();

        // 앞에서 계산한 오늘 날짜로 부터 3개월 정도 정보 조회
        // 레디스에 해당하는 전체키 반환
        String redisKey = "TRNS_SCV_INFO";


        Map<Object, Object> res = new HashMap<>();
        Map<Object, Object> mp;
        // 전체 키 조회


        List<String> keyList = resourcesManager.getRedisCmd().getKeyList("TRNS_SCV_INFO*");

        Collections.sort(keyList);
        // 각 키에 맞는 데이터 조회 후 계산

        for (String obj : keyList) {


            ScoutStat stat = new ScoutStat();

            mp = resourcesManager.getRedisCmd().hgetAll((obj));

            mp.forEach(
                    (key, value) -> {
                        // 데이터 읽기
                        TrnsSvcInfoDto tmpStat = resourcesManager.getModelMapper().map(value, TrnsSvcInfoDto.class);

                    }
            );
        }
        return result;
    }

    public Result getScoutAvgSevStat() throws ParseException {
        // 조회할 데이터를 임시로 담고
        Result result = new Result();

        //오늘 날짜
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        Date time = new Date();
        String time1 = format1.format(time);

        // 앞에서 계산한 오늘 날짜로 부터 3개월 정도 정보 조회
        // 레디스에 해당하는 전체키 반환
        String redisKey = "TRNS_SCV_INFO";


        Map<Object, Object> res = new HashMap<>();
        Map<Object, Object> mp;
        // 전체 키 조회


        List<String> tmpkeyList = resourcesManager.getRedisCmd().getKeyList("TRNS_SCV_INFO*");

        Collections.sort(tmpkeyList);
        // 각 키에 맞는 데이터 조회 후 계산

        Collections.reverse(tmpkeyList);

        List<String> keyList = new ArrayList<>();

        int sizeKeyList = tmpkeyList.size();

        if (tmpkeyList.size() == 0) {
            result.setResultFail();
            return result;
        }

        if (sizeKeyList > 7) {
            sizeKeyList = 7;
        }
        for (int i = 0; i < sizeKeyList; i++) {
            keyList.add(tmpkeyList.get(i));

        }
        ScoutStat stat = new ScoutStat();

        int totalSvcUnAvail = 0;
        for (String obj : keyList) {


            mp = resourcesManager.getRedisCmd().hgetAll((obj));
            int totalUnAvail = 0;

            if (mp == null) {
                result.setResultFail();  //onFail("데이터가 없습니다");
                return result;
            }

            mp.forEach(
                    (key, value) -> {

                        TrnsSvcInfoDto tmpStat = resourcesManager.getModelMapper().map(value, TrnsSvcInfoDto.class);


                        stat.setTotalTrnCnt(stat.getTotalTrnCnt() + tmpStat.getSvcCnt());

                        stat.setErrCnt(stat.getErrCnt() + tmpStat.getSvcErrCnt());
                        stat.setTotalDlyTime(stat.getTotalDlyTime() + tmpStat.getSvcTotalElap());
                        if (tmpStat.getSvcAvgElap() >= 1000) {
                            stat.setResDlyCnt(stat.getResDlyCnt() + tmpStat.getSvcCnt());
                        }

                    }
            );

            //TODO: 로직 생성
            Object oobj = resourcesManager.getRedisCmd().get("TRNS_SCV_UNAVAIL_" + stat.getTgDate());

            if (oobj != null && oobj.toString().isEmpty() == false) {
                TrnsSvcAvailVo vvp = resourcesManager.getModelMapper().map(oobj, TrnsSvcAvailVo.class);
                totalSvcUnAvail += vvp.getSvcUnAvail();

            }


        }

        stat.setAvlAbility(Math.round((1 - ((double) totalSvcUnAvail / (60 * 60))) * 10000) / 100.0);
        double ttmp = (double) ((double) stat.getResDlyCnt() / stat.getTotalTrnCnt());
        long tmplong = Math.round(ttmp * 100000);
        // 응답 지연 율 계산
        stat.setResDlyRate(tmplong / 1000.0);

        stat.setTgDate(keyList.get(0).substring(14) + keyList.get(sizeKeyList - 1).substring(14));
        // 에러율
        stat.setErrRate((Math.round((stat.getErrCnt() / (double) (stat.getTotalTrnCnt())) * 100000)) / 1000.0);

        // 평균 처리량
        stat.setTps(((Math.round((double) stat.getTotalTrnCnt() / (sizeKeyList * 24 * 60 * 60)) * 1000)) / 1000.0);


        stat.setAvlAbility(Math.round((1 - ((double) stat.getSvcUnAvail() / (60 * 60))) * 10000) / 100.0);


        res.put("soutSvdt", stat);
        result.setResultFail();

        return result;


    }


    public Result setSvcAvail(TrnsSvcAvailVo data) {

        Result result = new Result();

        if (!resourcesManager.getRedisCmd().set(RedisConstants.TRNS_SCV_UNAVAIL + "_" + data.getSvcTgDate(),
                data, 60 * 60 * 24 * 40 * 3)) {


            log.warn("fail data line = {}", data);
            result.setResultFail();
            return result;
        }

        result.setSuccess();
        return result;

    }

}
