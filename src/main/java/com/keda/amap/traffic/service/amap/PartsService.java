package com.keda.amap.traffic.service.amap;

import com.keda.amap.traffic.model.entity.Parts;
import io.github.biezhi.anima.Anima;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liChenYu on 2018/9/5
 */
@Service
public class PartsService {
    public List<Parts> getParts(Integer[] row, Integer[] col) {

        Integer rowStart = row[0];
        Integer rowEnd = row.length > 1 ? row[1] : rowStart;

        Integer colStart = col[0];
        Integer colEnd = col.length > 1 ? col[1] : colStart;

        return Anima.select().from(Parts.class)
                .where(Parts::getRow).gte(rowStart)
                .and(Parts::getRow).lte(rowEnd)
                .and(Parts::getCol).gte(colStart)
                .and(Parts::getCol).lte(colEnd)
                .and(Parts::getInRegion).eq(true)
                .all();
    }
}
