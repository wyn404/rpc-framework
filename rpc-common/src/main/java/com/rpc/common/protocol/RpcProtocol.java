package com.rpc.common.protocol;

import com.rpc.common.annotation.RpcService;
import com.rpc.common.util.JsonUtil;

import java.util.List;
import java.util.Objects;

public class RpcProtocol {
    private static final long serialVersionUID = -1102180003395190700L;
    // service host
    private String host;
    // service port
    private int port;
    // service info list
    private List<RpcServiceInfo> serviceInfoList;

    public String toJson() {
        String json = JsonUtil.objectToJson(this);
        return json;
    }

    public static RpcProtocol fromJson(String json) {
        return JsonUtil.jsonToObject(json, RpcProtocol.class);
    }

    private boolean isListEquals(List<RpcServiceInfo> thatList, List<RpcServiceInfo> thisList){
        if (thisList == null && thatList == null) return true;
        if ((thisList == null && thatList != null)
                || (thisList != null && thatList == null)
                || (thisList.size() != thatList.size())) {
            return false;
        }
        return thatList.containsAll(thisList) && thisList.containsAll(thatList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, serviceInfoList.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RpcProtocol that = (RpcProtocol) obj;
        return port == that.port && Objects.equals(host, that.host) &&
                isListEquals(serviceInfoList, that.getServiceInfoList());
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<RpcServiceInfo> getServiceInfoList() {
        return serviceInfoList;
    }

    public void setServiceInfoList(List<RpcServiceInfo> serviceInfoList) {
        this.serviceInfoList = serviceInfoList;
    }
}
