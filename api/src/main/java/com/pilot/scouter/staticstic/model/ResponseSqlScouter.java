package com.pilot.scouter.staticstic.model;


import com.pilot.scouter.staticstic.model.vo.ScoutSqlDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@Builder
@ToString
public class ResponseSqlScouter {
    private String status;

    private String requestId;

    private String resultCode;

    private String message;

    private Map<String, Map<String, ScoutSqlDto>> result;
}
