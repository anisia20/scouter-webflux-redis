package com.pilot.scouter.staticstic.model.vo;

import lombok.Data;

@Data
public class ScoutSqlDto {

    private String summaryKey;

    private String summaryKeyName;

    private String count;

    private String errorCount;

    private String elapsedSum;
}

