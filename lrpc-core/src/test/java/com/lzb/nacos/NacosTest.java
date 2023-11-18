package com.lzb.nacos;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NacosTest {


    public static void main(String[] args) {
        String s = "NacosInetAddress(host=10.33.91.150, port=9998)";
        s = s.substring(s.indexOf("(") + 1, s.length() - 1);
        System.out.println(s);
        String[] strings = s.split(",");
        String host = strings[0].split("=")[1];
        String post = strings[1].split("=")[1];
        System.out.println(host + "  " + post);



        /*Gson GSON = new Gson();
        String rpcServiceName = "HelloController";
        String DEFAULT_GROUP = "DEFAULT_GROUP";
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8081);
        try {
            ConfigService configService = NacosUtils.getConfigService();
            String content = configService.getConfig(rpcServiceName, DEFAULT_GROUP, 5000);
            boolean isPublishOk;
            if (content == null) {
                isPublishOk = configService.publishConfig(rpcServiceName, DEFAULT_GROUP,
                        GSON.toJson(Collections.singletonList(new NacosInetAddress(inetSocketAddress))));
            } else {
                List<NacosInetAddress> nacosInetAddressList = GSON.fromJson(content, new TypeToken<List<NacosInetAddress>>() {
                }.getType());
                nacosInetAddressList.add(new NacosInetAddress(inetSocketAddress));
                isPublishOk = configService.publishConfig(rpcServiceName, DEFAULT_GROUP,
                        GSON.toJson(nacosInetAddressList));
            }
            log.info("[{}]服务注册: {}", rpcServiceName, isPublishOk);
        } catch (NacosException e) {
            log.error("服务注册失败: [{}] [{}] [{}]", rpcServiceName, inetSocketAddress.toString(), e.getMessage());
        }*/
    }
}
