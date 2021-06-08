package com.pilot.scouter.member.module;

import com.pilot.scouter.common.daemon.DaemonG;
import com.pilot.scouter.config.redis.command.RedisCmd;
import com.pilot.scouter.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ExamDamonG extends DaemonG {
    @Autowired
    RedisCmd redisCmd;

    public ExamDamonG() {
        setName(getClass().getSimpleName());
        log.info("{} start.", getName());
        nextInit();
    }

    @Override
    public void init(){
        // 데몬이 필요한 db 컨넥션등을 체크
        // 실패시 nextfail
        if(redisCmd.getConnection() == null) {
            log.warn("redis connect fail");
            nextFail();
            return;
        }

        nextExecute();
    }

    @Override
    public void execute(){
        //model 은 롬복 데이터 생성 필요
        //redisCmd.push("DB_INSERT_Q_DATE", "asdfasdf");

        Object model = redisCmd.pop("DB_INSERT_Q_DATE");
        if(model == null){
            nextFail();
            return;
        }

        if(Util.isNullOrEmpty(model)){
            Util.sleep(100);
            return;
        }

        //model을 가지고 DB에 입력

        //실질적으로 일함
        //예외시 nextfail

        nextExecute();
    }

    @Override
    public void fail(){
        //실패 후속처리 io close, connection close 등등
        Util.sleep(5000);
        nextInit();
    }

    @Override
    public void stop(){
        //진짜 종료전 처리 되야 될 부분
        // 후속 처리등

        return;
    }

}
