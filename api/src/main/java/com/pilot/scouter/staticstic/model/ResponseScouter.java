package com.pilot.scouter.staticstic.model;

import com.pilot.scouter.staticstic.model.vo.ScoutSvcDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@Builder
@ToString
public class ResponseScouter {

    private String status;

    private String requestId;

    private String resultCode;

    private String message;

    private Map<String, Map<String, ScoutSvcDto>> result;


}

