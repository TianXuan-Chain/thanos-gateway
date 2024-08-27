package com.thanos.gateway.core.broadcast;

/**
 * Son.java description：
 *
 * @Author laiyiyu create on 2021-04-15 11:05:20
 */
public class Son extends Father {


    public static  void main(String[] args) {
        Father father = new Son();
        System.out.println(father.getNamespace());
    }



}
