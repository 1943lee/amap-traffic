package com.keda.amap.traffic;

import com.keda.amap.traffic.model.entity.Parts;
import com.keda.amap.traffic.service.amap.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Created by liChenYu on 2018/9/6
 */
@ComponentScan
@Order(2)
public class DoWork implements CommandLineRunner {
    @Autowired
    PartsService partsService;

    @Override
    public void run(String... args) throws Exception {
        List<Parts> partsList = partsService.getInRegionParts();

    }
}
