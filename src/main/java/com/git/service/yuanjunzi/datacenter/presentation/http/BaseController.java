package com.git.service.yuanjunzi.datacenter.presentation.http;

import com.git.service.yuanjunzi.datacenter.infrastructure.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by wangshenxiang on 2017/4/20.
 */
public class BaseController {
    public static final String ERR_MSG = "errMsg";
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    @Resource
    private CacheService cacheService;

    public ModelAndView paramErr() {
        return cusErr("参数错误");
    }

    public ModelAndView networkErr() {
        return cusErr("请求异常，请稍后重试");
    }

    public ModelAndView ssoErr() {
        return cusErr("认证失败");
    }

    public ModelAndView unknownErr() {
        return cusErr("未知错误");
    }

    public ModelAndView envErr() {
        return cusErr("环境错误");
    }

    public ModelAndView cusErr(String msg) {
        ModelAndView mav = new ModelAndView();
        mav.addObject(ERR_MSG, msg);
        return mav;
    }

    public String redirect(String url) {
        return "redirect:" + url;
    }
}
