package com.rpc.common.codec;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.io.Serializable;

/**
 * RPC Response
 */
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 8215493329459772524L;

    private String requestId;
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
