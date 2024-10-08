# 介绍
天玄区块链中的节点程序主要由两部分组成：
* 节点应用服务
* 节点网关

本仓库代码为节点网关部分，主要任务是负责接收外部交易并缓存到交易池、使用 P2P 协议对加以进行广播、推送交易池中的交易到节点中进行共识、执行。

# 编译
拉取仓库后，进入文件夹，执行下面指令
```sh
mvn clean install -Dmaven.test.skip=true
```
指令执行成功后，会在 `target` 文件夹内产生 `thanos-gateway.jar` 文件。

注意， 节点应用服务编译打包需要依赖 `thanos-common.jar` 包。

# 教程
打包编译教程：
* [在线文档 - 打包可执行文件](https://tianxuan.blockchain.163.com/installation-manual/tianxaun-chain/executable-file.html)
* [文档仓库 - 打包可执行文件](https://github.com/TianXuan-Chain/tianxuan-docs/blob/new-pages/tools/blockchain-browser/installation-manual/tianxaun-chain/executable-file.md)

安装部署教程：
* [在线文档 - 天玄节点网关安装](https://tianxuan.blockchain.163.com/installation-manual/tianxaun-gateway/)
* [文档仓库 - 天玄节点网关安装](https://github.com/TianXuan-Chain/tianxuan-docs/tree/main/installation-manual/tianxaun-gateway)

# License
Apache 2.0
