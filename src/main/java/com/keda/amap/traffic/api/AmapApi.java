package com.keda.amap.traffic.api;

import com.keda.amap.traffic.model.dto.AmapSearchResponse;
import com.keda.amap.traffic.model.entity.Parts;
import com.keda.amap.traffic.model.param.SearchParam;
import com.keda.amap.traffic.service.amap.PartsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liChenYu on 2018/9/5
 */
@Slf4j
@RestController
@RequestMapping("api")
public class AmapApi {
    @Autowired
    PartsService partsService;

    @PostMapping("/search")
    public ResponseEntity<?> getSearchResultViaAjax(
            @RequestBody SearchParam searchParam) {
        AmapSearchResponse result = new AmapSearchResponse();

        Integer[] row = searchParam.getRow();
        Integer[] col = searchParam.getCol();
        List<Parts> partsList = partsService.getParts(row, col);

        result.setTotal(partsList.size());
        List<double[][]> pointList = new ArrayList<>();

        partsList.forEach(parts -> {
            double[][] points = new double[4][2];

            points[0][0] = parts.getXminGcj();
            points[0][1] = parts.getYminGcj();
            points[1][0] = parts.getXmaxGcj();
            points[1][1] = parts.getYminGcj();
            points[2][0] = parts.getXmaxGcj();
            points[2][1] = parts.getYmaxGcj();
            points[3][0] = parts.getXminGcj();
            points[3][1] = parts.getYmaxGcj();

            pointList.add(points);
        });
        result.setPoints(pointList);

        return ResponseEntity.ok(result);
    }
}
