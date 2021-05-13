package com.pilot.scouter.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisConstants {
    SCOUTER_H_CLIENT("SCOUTER_H_CLIENT", "클라이언트 정보"),
    SCOUTER_S_KEYGEN_SVRKEY_MEMBER("SCOUTER_S_KEYGEN_SVRKEY_MEMBER", "회원 서버키 ");
    public String key;
    public String desc;

    // trns service
    public static final String TRNS_SCV_INFO = "TRNS_SCV_INFO";
    public static final String TRNS_SCV_UNAVAIL = "TRNS_SCV_UNAVAIL";
}
