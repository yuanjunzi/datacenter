package com.git.service.yuanjunzi.datacenter.application.service;

import java.util.Map;

/**
 * Created by yuanjunzi on 2019/4/24.
 * 模版翻译策略
 */
public interface TranslationStrategy {

    void translate(Map<String, String> req, Map<String, Map<String, Map<String, String>>> templateMap, String queryTemplate);
}
