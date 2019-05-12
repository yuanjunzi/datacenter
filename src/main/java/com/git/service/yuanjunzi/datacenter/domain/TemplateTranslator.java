package com.git.service.yuanjunzi.datacenter.domain;

import com.git.service.yuanjunzi.datacenter.application.service.TranslationStrategy;

import java.util.Map;

/**
 * Created by yuanjunzi on 2019/4/24.
 * 模版翻译器
 */
public class TemplateTranslator {

    private TranslationStrategy strategy;

    public TemplateTranslator(TranslationStrategy strategy) {
        this.strategy = strategy;
    }

    public void translate (Map<String, String> req, Map<String, Map<String, Map<String, String>>> templateMap, String queryTemplate) {
        strategy.translate(req, templateMap, queryTemplate);
    }
}
