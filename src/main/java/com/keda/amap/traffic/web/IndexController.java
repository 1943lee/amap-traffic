package com.keda.amap.traffic.web;

import com.keda.amap.traffic.config.AmapConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by liChenYu on 2018/9/5
 */
@Controller
public class IndexController {
    private final AmapConfig amapConfig;

    @Autowired
    public IndexController(AmapConfig amapConfig) {
        this.amapConfig = amapConfig;
    }

    @RequestMapping("/")
    public String index(ModelMap map) {
        // 加入一个属性，用来在模板中读取
        map.addAttribute("key", amapConfig.getWebKey());
        // return模板文件的名称，对应src/main/resources/templates/index.html
        return "index";
    }
}
