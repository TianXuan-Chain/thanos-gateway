
#MaxTenurningThreshold 的最大值为15，应为java 对象头只用2 字节存储
java  --server.port=8087  -Xmx2048m -Xms2048m -Xmn4g -Xss4M -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=15  -jar thanos-gateway-1.0.0.jar