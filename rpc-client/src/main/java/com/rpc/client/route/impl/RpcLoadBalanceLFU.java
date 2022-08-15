package com.rpc.client.route.impl;

import com.rpc.client.handler.RpcClientHandler;
import com.rpc.client.route.RpcLoadBalance;
import com.rpc.common.protocol.RpcProtocol;
import com.rpc.common.serializer.Serializer;
import org.checkerframework.checker.units.qual.C;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *  LFU load balance
 */
public class RpcLoadBalanceLFU extends RpcLoadBalance {

    private ConcurrentMap<String, HashMap<RpcProtocol, Integer>> LFUMap = new ConcurrentHashMap<>();
    private long CACHE_VALID_TIME = 0;

    public RpcProtocol doRoute(String serviceKey, List<RpcProtocol> addressList) {
        // clear cache
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            LFUMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }

        // LFU item init
        HashMap<RpcProtocol, Integer> LFUItemMap = LFUMap.get(serviceKey);
        if (LFUItemMap == null) {
            LFUItemMap = new HashMap<RpcProtocol, Integer>();
            LFUMap.putIfAbsent(serviceKey, LFUItemMap);  // 避免重复覆盖
        }

        // put new
        for (RpcProtocol address : addressList) {
            if (!LFUItemMap.containsKey(address) || LFUItemMap.get(address) > 1000000) {
                LFUItemMap.put(address, 0);
            }
        }

        // remove old
        List<RpcProtocol> delKeys = new ArrayList<>();
        for (RpcProtocol existKey : LFUItemMap.keySet()) {
            if (!addressList.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (delKeys.size() > 0) {
            for (RpcProtocol delKey : delKeys) {
                LFUItemMap.remove(delKey);
            }
        }

        // load least used count address
        List<Map.Entry<RpcProtocol, Integer>> LFUItemList = new ArrayList<>(LFUItemMap.entrySet());
        Collections.sort(LFUItemList, new Comparator<Map.Entry<RpcProtocol, Integer>>() {
            @Override
            public int compare(Map.Entry<RpcProtocol, Integer> o1, Map.Entry<RpcProtocol, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        Map.Entry<RpcProtocol, Integer> addressItem = LFUItemList.get(0);
        RpcProtocol minAddress = addressItem.getKey();
        addressItem.setValue(addressItem.getValue() + 1);

        return minAddress;
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
