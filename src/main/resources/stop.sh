#!/bin/bash
    weth_pid=`ps aux|grep "thanos-gateway-1.0.0.jar"|grep -v grep|awk '{print $2}'`
    kill_cmd="kill -9 ${weth_pid}"
    echo "${kill_cmd}"
    eval ${kill_cmd}