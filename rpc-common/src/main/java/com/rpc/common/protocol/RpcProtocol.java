package com.rpc.common.protocol;

import com.rpc.common.annotation.RpcService;

import java.util.List;

public class RpcProtocol {
    private static final long serialVersionUID = -1102180003395190700L;
    // service host
    private String host;
    // service port
    private String port;
    // service info list
    private List<RpcServiceInfo> serviceInfoList;


}
