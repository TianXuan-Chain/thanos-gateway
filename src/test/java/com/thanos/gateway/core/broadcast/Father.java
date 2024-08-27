package com.thanos.gateway.core.broadcast;

/**
 * Father.java description：
 *
 * @Author laiyiyu create on 2021-04-15 11:05:36
 */
public class Father  {

    String namespace;

    public Father() {
        this.namespace = this.getClass().getSimpleName();
    }

    public String getNamespace() {
        return namespace;
    }
}
