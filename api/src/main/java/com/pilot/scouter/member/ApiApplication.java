package com.pilot.scouter.member;

import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.pilot.scouter.common.command.JsonCmd;
import com.pilot.scouter.common.constants.RedisConstants;
import com.pilot.scouter.common.model.redis.ClientDto;
import com.pilot.scouter.config.redis.command.RedisCmd;
import com.pilot.scouter.utils.ADMSHA512Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.core.env.AbstractEnvironment;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableScheduling
@Log4j2
@ComponentScans(value = {
        @ComponentScan("com.pilot.scouter"),
})
@EnableWebFlux
public class ApiApplication {

    @Autowired
    JsonCmd jsonCmd;

    @Autowired
    RedisCmd redisCmd;

    static String ACCESS_LOG_ENABLED = "reactor.netty.http.server.accessLogEnabled";
    public static void main(String[] args) {
        if (System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME) == null) {
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "local");
        }

        if (System.getProperty(ACCESS_LOG_ENABLED) == null) {
            System.setProperty(ACCESS_LOG_ENABLED, "true");
        }

        SpringApplication.run(ApiApplication.class, args);
    }

    @PostConstruct
    public void onStartup() {
        log.info("################ System-up start ################");

        //최초 아이디 admin admin
        ClientDto tmp = new ClientDto();
        tmp.setId("admin");
        tmp.setPwd(ADMSHA512Hash.getDigest("admin"));
        redisCmd.hput(RedisConstants.SCOUTER_H_CLIENT.key, "admin", tmp);

        try {
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            jsonCmd.initObjectMapper();

        } catch (Exception e) {
            log.error("", e);
        }
        log.info("################ System-up complete ################");
    }

    @PreDestroy
    public void onExit() {
        log.info("################ System down start ################");
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();

        } catch (Exception e) {
            log.error("", e);
        }
        log.info("################ System down end ################");
    }
}
