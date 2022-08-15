package com.rpc.client.route.impl;

import com.rpc.client.handler.RpcClientHandler;
import com.rpc.client.route.RpcLoadBalance;
import com.rpc.common.protocol.RpcProtocol;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *  LRU load balance
 */
public class RpcLoadBalanceLRU extends RpcLoadBalance {

    private ConcurrentMap<String, LinkedHashMap<RpcProtocol, RpcProtocol>> LRUMap =
            new ConcurrentHashMap<>();
    private long CACHE_VALID_TIME = 0;


    public RpcProtocol doRoute(String serviceKey, List<RpcProtocol> addressList) {
        // clear cache
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            LRUMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }

        // init LRU
        LinkedHashMap<RpcProtocol, RpcProtocol> LRUHashMap = LRUMap.get(serviceKey);
        if (LRUHashMap == null) {
            /**
             *   1、accessOrder: true=访问顺序排序（get/put时顺序）/ACCESS-LAST；false=插入顺序排序/FIFO；
             *   2、removeEldestEntry: 新增元素时调用，返回true时会删除最老元素；
             *       可封装LinkedHashMap并重写该方法，比如定义最大容量，超出时返回true即可实现固定长度的LRU算法；
             */
            LRUHashMap = new LinkedHashMap<RpcProtocol, RpcProtocol>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<RpcProtocol, RpcProtocol> eldest) {
                    if (super.size() > 1000) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            LRUMap.putIfAbsent(serviceKey, LRUHashMap);
        }

        // put new
        for (RpcProtocol address : addressList) {
            if (!LRUHashMap.containsKey(address)) {
                LRUHashMap.put(address, address);
            }
        }

        // remove old
        List<RpcProtocol> delKeys = new ArrayList<>();
        for (RpcProtocol existKey : LRUHashMap.keySet()) {
            if (!addressList.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (delKeys.size() > 0) {
            for (RpcProtocol delKey : delKeys) {
                LRUHashMap.remove(delKey);
            }
        }

        // load
        RpcProtocol eldestKey = LRUHashMap.entrySet().iterator().next().getKey();
        RpcProtocol eldestValue = LRUHashMap.get(eldestKey);
        return eldestValue;
    }

    @Override
    public RpcProtocol route(String serviceKey, Map<RpcProtocol, RpcClientHandler> connectedServerNodes) throws Exception {
        Map<String, List<RpcProtocol>> serviceMap = getServiceMap(connectedServerNodes);
        List<RpcProtocol> addressList = serviceMap.get(serviceKey);
        if (addressList != null && addressList.size() > 0) {
            return doRoute(serviceKey, addressList);
        } else {
            throw new Exception("Can not find connection for service: " + serviceKey);
        }
    }
}
