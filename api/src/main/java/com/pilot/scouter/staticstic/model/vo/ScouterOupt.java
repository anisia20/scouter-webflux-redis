package com.pilot.scouter.staticstic.model.vo;


import com.pilot.scouter.utils.Command;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public final class ScouterOupt implements Command {

    private ScouterOupt() {}

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class CreateScouterOupt {

        private String svcNm;
        private String svcCnt;
        private String svcErrCnt;
        private String svcTotalElap;
        private String svcAvgElap;
        private String totalCpu;
        private String avgCpu;
        private String totalMem;
        private String avgMem;


        /* 생성자 */
        public CreateScouterOupt(String svcNm,String svcCnt,String svcErrCnt, String svcTotalElap ,
                                 String svcAvgElap, String totalCpu, String avgCpu, String totalMem, String avgMem) {


            this.svcNm = svcNm;
            this.svcCnt = svcCnt;
            this.svcErrCnt = svcErrCnt;
            this.svcTotalElap = svcTotalElap;
            this.svcAvgElap = svcAvgElap;
            this.totalCpu =  totalCpu;
            this.avgCpu = avgCpu;
            this.totalMem = totalMem;
            this.avgMem = avgMem;

        }

    }

}
