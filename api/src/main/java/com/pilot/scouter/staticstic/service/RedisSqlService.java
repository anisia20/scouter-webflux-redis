package com.pilot.scouter.staticstic.service;

import com.pilot.scouter.common.model.Result;
import com.pilot.scouter.components.CommonResourceManager;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
public class RedisSqlService {

    @Autowired
    CommonResourceManager resourcesManager;

    public Result getScoutSqlStat(int index, String date) {

        // 조회할 데이터를 임시로 담고
        Result result = new Result();

        List<Object> ndata = new ArrayList<>();

        List<keyListError> res = new ArrayList<>();
        Set<Object> data = new LinkedHashSet<>();


        List<String> keyList = resourcesManager.getRedisCmd().getKeyList("TRNS_SQL_INFO_" + date);

        if (keyList.size() == 0) {
            result.setResultFail();
            return result;
        }


        for (String obj : keyList) {
            data = resourcesManager.getRedisCmd().hgetAllField(obj);
        }

        for (Object ob : data) {
            res.add(new keyListError(Integer.parseInt(ob.toString().substring(0, ob.toString().indexOf("_"))), ob));
        }

        Collections.sort(res, new Comparator<keyListError>() {
            @Override
            public int compare(keyListError o1, keyListError o2) {
                return o2.getCount() - o1.getCount();
            }
        });

        for (int i = 0; i < index; i++) {
            ndata.add(resourcesManager.getRedisCmd().hget(keyList.get(0), res.get(i).getContents().toString()));
        }

        ndata.size();
        result.setSuccess(ndata);
        return result;
    }


}

@Data
class keyListError {

    private int count;
    private Object contents;

    public keyListError(int substring, Object ob) {
        this.count = substring;
        this.contents = ob;
    }
}


class MapComparator implements Comparator<HashMap<String, String>> {

    private final String key;

    public MapComparator(String key) {
        this.key = key;
    }

    @Override
    public int compare(HashMap<String, String> first, HashMap<String, String> second) {
        int result = first.get(key).compareTo(second.get(key));
        return result;
    }
}

