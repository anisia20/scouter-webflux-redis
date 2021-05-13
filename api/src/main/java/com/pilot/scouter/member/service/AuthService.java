package com.pilot.scouter.member.service;

import com.pilot.scouter.common.constants.RedisConstants;
import com.pilot.scouter.common.model.Result;
import com.pilot.scouter.config.redis.command.RedisCmd;
import com.pilot.scouter.member.component.AuthManager;
import com.pilot.scouter.member.model.Auth;
import com.pilot.scouter.member.model.AuthResult;
import com.pilot.scouter.utils.ADMSHA512Hash;
import com.pilot.scouter.utils.Util;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import com.pilot.scouter.common.code.ResultCode;
import com.pilot.scouter.common.command.JwtCmd;
import com.pilot.scouter.common.model.redis.ClientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import com.pilot.scouter.components.CommonResourceManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;

@Data
@Service
@Log4j2
public class AuthService {

    @Autowired
    RedisCmd redisCmd;

    @Autowired
    JwtCmd jwtCmd;

    @Autowired
    CommonResourceManager commonResourceManager;

    @Autowired
    AuthManager authenticationManager;

    private String name=AuthService.class.getSimpleName();

    public Mono<Object> authClient(ServerHttpRequest request, ServerHttpResponse response, Auth ar) {
        Result result = new Result();
        String ip = Util.getRemoteIpAddr(request);

        log.debug("auth token params(Auth)={}, ip={}", ar, ip);

        // validation 체크
        if (Util.isValid(ar, result) == false) {
            log.warn("validation check faild. C=[{}] err=[{}/{}]",
                    ar.getId(), result.getResult(), result.getResultDescription());

            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return Mono.just(result);
        }

        // ID, PWD 체크
        ClientDto clientDetails = (ClientDto) redisCmd.hget(RedisConstants.SCOUTER_H_CLIENT.key, ar.getId());
        if (clientDetails == null) {
            log.debug("cannot found cliId. C={}, ip={}", ar.getId());
            result.setResultFail(ResultCode.R_100.r, ResultCode.R_100.rd+"_C_"+ar.getId());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.just(result);
        }

        // token 생성
        String token = jwtCmd.getToken( clientDetails.getId(),"order,member", false );
        if ("".contentEquals(token)) {
            result.setResultFail(ResultCode.R_100.r, ResultCode.R_100.rd+"_token");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.just(result);
        }

        /////////////////////////

        String refresh_token = jwtCmd.getToken(
                clientDetails.getId(),"auth", true);
        if ("".contentEquals(refresh_token)) {
            result.setResultFail(ResultCode.R_100.r, ResultCode.R_100.rd+"_refreshtoken");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.just(result);
        }

        result.put(AuthResult.FIELD_TOKEN, token);
        result.put(AuthResult.FIELD_REFRESH_TOKEN, refresh_token);

        result.setSuccess();
        log.debug("auth token C={}, ip={}, result={}", ar.getId(), ip, result);

        return Mono.just(result);
    }

    public Mono<Object> refreshCli(@AuthenticationPrincipal String id, ServerWebExchange swe,
                                   ServerHttpRequest request) {

        Result result = new Result();

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Bearer 체크
        if (authHeader == null || authHeader.startsWith("Bearer ") == false)
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        String authToken = authHeader.substring(7);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
        auth.setDetails(swe);

        // token 생성 및 리턴
        return this.authenticationManager.authenticate(auth).flatMap((authentication) -> {
            Claims claims = (Claims) authentication.getDetails();
            String subject = (String) authentication.getPrincipal();

            log.debug("auth token refresh claims = {}", claims);

            // token 생성
            String token = jwtCmd.getToken(subject, "order,member", false);
            if (StringUtils.hasText(token) == false) {
                log.warn("token generate error. C={}", subject);
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            }

            result.put(AuthResult.FIELD_TOKEN, token);
            result.setSuccess();

            log.debug("auth token refresh result = [{}]", result);
            return Mono.just(result);

        });
    }

    public static String myIp () {
        String ipStr = "";
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            ipStr = ip.getHostName();
            System.out.println("Host Name = [" + ip.getHostName() + "]");
            System.out.println("Host Address = [" + ip.getHostAddress() + "]");
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return ipStr;
    }

}
