package com.pilot.scouter.components;

import java.util.Hashtable;

import com.pilot.scouter.common.command.JsonCmd;
import com.pilot.scouter.common.command.JwtCmd;
import com.pilot.scouter.common.config.ModelMapperG;
import com.pilot.scouter.common.constants.RedisConstants;
import com.pilot.scouter.common.util.UuidMaker;
import com.pilot.scouter.config.redis.command.RedisCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Configuration
@Data
@Log4j2
public class CommonResourceManager {
    protected static Hashtable<String, Object> resources = new Hashtable<String, Object>();

    @Autowired
    RedisCmd redisCmd;

    @Autowired
    JsonCmd jsonCmd;

    @Autowired
    JwtCmd jwtCmd;

    @Autowired
    ModelMapperG modelMapper;

    @Bean
    public UuidMaker getKeyMaker(){
        try {
            Object obj = resources.get(RedisConstants.SCOUTER_S_KEYGEN_SVRKEY_MEMBER.key);
            if(obj==null) {
                long num = getRedisCmd().incValue(RedisConstants.SCOUTER_S_KEYGEN_SVRKEY_MEMBER.key);
                if (num < 0 || num > 99) {
                    getRedisCmd().set(RedisConstants.SCOUTER_S_KEYGEN_SVRKEY_MEMBER.key,0);
                    num = 0;
                }

                UuidMaker km = new UuidMaker((int) num);
                resources.put(RedisConstants.SCOUTER_S_KEYGEN_SVRKEY_MEMBER.key, km);
                return km;
            } else {
                UuidMaker km = (UuidMaker) obj;
                return km;
            }
        } catch (Exception e) {
            log.error("UuidMaker gathering fail. err={}", e.toString(), e);
            return null;
        }
    }
    public synchronized void put(String key, Object obj){
        try {
            if(resources.containsKey(key))
                throw new Exception("key=["+key+"] already exists");

            resources.put(key, obj);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public Object get(String key){
        try {
            Object obj = resources.get(key);
            if(obj==null)
                throw new Exception("key=["+key+"] not found.");

            return resources.get(key);
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }
}
