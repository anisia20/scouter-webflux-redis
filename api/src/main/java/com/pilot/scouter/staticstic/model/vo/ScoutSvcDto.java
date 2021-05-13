package com.pilot.scouter.staticstic.model.vo;

import lombok.Data;

@Data
public class ScoutSvcDto {

    private String summaryKey;

    private String summaryKeyName;

    private String count;

    private String errorCount;

    private String elapsedSum;

    private String cpuSum;

    private String memorySum;

}

