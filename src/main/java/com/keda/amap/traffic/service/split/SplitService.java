package com.keda.amap.traffic.service.split;

import com.keda.amap.traffic.config.SourceConfig;
import com.keda.amap.traffic.model.entity.Parts;
import com.keda.amap.traffic.util.GeoConvert;
import io.github.biezhi.anima.Anima;
import io.github.biezhi.anima.Model;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static io.github.biezhi.anima.Anima.select;

/**
 * 划分目标区域
 *
 * Created by liChenYu on 2018/9/4
 */
@Slf4j
@Service
public class SplitService {

    private final SourceConfig sourceConfig;

    @Autowired
    RegionService regionService;

    @Autowired
    public SplitService(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    /**
     * 构造分割后的矩形
     */
    public void init() {
        long count = select().from(Parts.class).count();
        if(count > 0) {
            log.info("t_parts has been initialized, total number is {}", count);
            return;
        }

        double xmin = sourceConfig.getXmin();
        double ymin = sourceConfig.getYmin();
        double xmax = sourceConfig.getXmax();
        double ymax = sourceConfig.getYmax();

        double width = sourceConfig.getWidth();
        double height = sourceConfig.getHeight();

        double[] leftBottomMercatorPoint = GeoConvert.wgs84ToWebMercator(xmin, ymin);
        double[] rightTopMercatorPoint = GeoConvert.wgs84ToWebMercator(xmax, ymax);

        int xTimes = (int) Math.ceil((rightTopMercatorPoint[0] - leftBottomMercatorPoint[0]) / width);
        int yTimes = (int) Math.ceil((rightTopMercatorPoint[1] - leftBottomMercatorPoint[1]) / height);

        int limit = 5000;
        long operateCount = 0;
        long outOfRegionCount = 0;
        List<Parts> partsList = new ArrayList<>();
        for(int i = 0; i < xTimes; i++) {
            double xmin_mercator = leftBottomMercatorPoint[0] + i * width;
            double xmax_mercator = xmin_mercator + width;
            for(int j = 0; j < yTimes; j++) {
                double ymin_mercator = leftBottomMercatorPoint[1] + j * height;
                double ymax_mercator = ymin_mercator + height;

                double[] xminYmin = GeoConvert.webMercatorToWgs84(xmin_mercator, ymin_mercator);
                double[] xmaxYmax = GeoConvert.webMercatorToWgs84(xmax_mercator, ymax_mercator);

                double[] xminYminGcj02 = GeoConvert.wgs84togcj02(xminYmin[0], xminYmin[1]);
                double[] xmaxYmaxGcj02 = GeoConvert.wgs84togcj02(xmaxYmax[0], xmaxYmax[1]);

                Parts parts = new Parts();
                parts.setRow(i);
                parts.setCol(j);

                parts.setXmin(xminYmin[0]);
                parts.setYmin(xminYmin[1]);
                parts.setXmax(xmaxYmax[0]);
                parts.setYmax(xmaxYmax[1]);
                parts.setXminMer(xmin_mercator);
                parts.setYminMer(ymin_mercator);
                parts.setXmaxMer(xmax_mercator);
                parts.setYmaxMer(ymax_mercator);
                parts.setXminGcj(xminYminGcj02[0]);
                parts.setYminGcj(xminYminGcj02[1]);
                parts.setXmaxGcj(xmaxYmaxGcj02[0]);
                parts.setYmaxGcj(xmaxYmaxGcj02[1]);

                parts.setInRegion(regionService.inRegion(xminYmin[0], xminYmin[1], xmaxYmax[0], xmaxYmax[1]));
                // 不在指定范围内的矩形，不入库
                if(!parts.getInRegion()) {
                    outOfRegionCount++;
                    continue;
                }

                partsList.add(parts);

                limit--;
                if(limit == 0) {
                    operateCount += 5000;
                    Anima.atomic(() -> partsList.forEach(Model::save));
                    log.info("本次插入{},已插入{},区域外数据{},总量{}", 5000, operateCount, outOfRegionCount, xTimes * yTimes);
                    partsList.clear();
                    limit = 5000;
                }
            }
        }

        if(partsList.size() > 0) {
            operateCount += partsList.size();
            Anima.atomic(() -> partsList.forEach(Model::save));
            log.info("本次插入{},已插入{},区域外数据{},总量{}", partsList.size(), operateCount, outOfRegionCount, xTimes * yTimes);
        }

    }
}
