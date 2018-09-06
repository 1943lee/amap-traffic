package com.keda.amap.traffic.util;

import com.keda.amap.traffic.config.EsConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Created by liChenYu on 2018/9/5
 */
@Slf4j
@Service
@Lazy
public class EsClient {

    private EsConfig esConfig;

    private RestClient restClient = null;

    @Autowired
    public EsClient(EsConfig esConfig) {
        this.esConfig = esConfig;
        setRestClient();
    }

    public RestClient getRestClient() {
        return restClient;
    }

    private void setRestClient() {
        String[] serverList = esConfig.getHosts().split(";");
        String username = esConfig.getUsername();
        String password = esConfig.getPassword();

        if(serverList.length > 0) {
            HttpHost[] hosts = new HttpHost[serverList.length];
            for (int i = 0; i < serverList.length; i++) {
                String[] ip_port = serverList[i].split(":");
                if (ip_port.length == 2)
                {
                    String ip = ip_port[0];
                    int port = Integer.valueOf(ip_port[1]);
                    hosts[i] = new HttpHost(ip, port, "http");
                }
            }

            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            restClient = RestClient.builder(hosts)
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                    .build();
            log.info("Elasticsearch连接成功");
        }
        else
        {
            log.error("Elasticsearch服务地址未配置");
        }
    }
}
