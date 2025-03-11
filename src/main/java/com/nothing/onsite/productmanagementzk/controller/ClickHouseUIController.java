package com.nothing.onsite.productmanagementzk.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/clickhouse")
public class ClickHouseUIController {

    @Value("${clickhouse.play-ui.url}")
    private String playUiUrl;

    @Value("${clickhouse.tabix-ui.url}")
    private String tabixUiUrl;

    @GetMapping("/ui-info")
    @ResponseBody
    public Map<String, String> getClickHouseUIInfo() {
        Map<String, String> uiInfo = new HashMap<>();
        uiInfo.put("playUiUrl", playUiUrl);
        uiInfo.put("tabixUiUrl", tabixUiUrl);
        return uiInfo;
    }

    @GetMapping("/play")
    public String redirectToPlayUI() {
        return "redirect:" + playUiUrl;
    }

    @GetMapping("/tabix")
    public String redirectToTabixUI() {
        return "redirect:" + tabixUiUrl;
    }
} 