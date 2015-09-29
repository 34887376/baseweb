package com.ms.redis.util;

import java.util.HashMap;
import java.util.Map;

public class ZookeeperDomain {
    private Map<String,String> data = new HashMap<String,String>();
    private Map<String,String> zother = new HashMap<String,String>();

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getZother() {
        return zother;
    }

    public void setZother(Map<String, String> zother) {
        this.zother = zother;
    }
}

