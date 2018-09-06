package com.keda.amap.traffic;

import com.keda.amap.traffic.config.AmapConfig;
import com.keda.amap.traffic.model.entity.Parts;
import com.keda.amap.traffic.service.amap.PartsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrafficApplicationTests {
    @Autowired
    PartsService partsService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AmapConfig amapConfig;

    @Test
    public void contextLoads() {
        List<Parts> partsList = partsService.getInRegionParts();
        Parts parts = partsList.get(0);



    }

}
