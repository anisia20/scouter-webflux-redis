package com.pilot.scouter.member.schedules;

import com.pilot.scouter.common.daemon.DaemonG;
import com.pilot.scouter.member.module.ExamDamonG;
import com.pilot.scouter.utils.Util;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EnableScheduling
@Component
public class Schedules {

    List<DaemonG> list = new ArrayList<DaemonG>();

    @Scheduled(fixedDelay = 5000)
    public void doExam(){

        if(list.isEmpty()){
            ExamDamonG start = new ExamDamonG();
            Thread dg = new Thread(start);
            dg.start();
            list.add(start);
        }

        for(DaemonG d : list){
            if((new Date()).getTime() - d.getLastCheckTime() > 10 * 1000){
                d.nextStop();
            }

            if(d.getStatus() == DaemonG.Status.STOP ) {
                list.remove(d);
                ExamDamonG start = new ExamDamonG();
                Thread dg = new Thread(start);
                dg.start();
                list.add(start);
            }

        }

    }
}
