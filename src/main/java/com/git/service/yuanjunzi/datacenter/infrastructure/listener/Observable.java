package com.git.service.yuanjunzi.datacenter.infrastructure.listener;


import com.git.service.yuanjunzi.datacenter.domain.Observer;

import java.util.Map;

/**
 * Created by yuanjunzi on 2019/3/27.
 * 被观察者
 */
public interface Observable<T extends Observer> {
    boolean addObserver(T observer);

    boolean removeObserver(T observer);

    void notifyObserver(Map<String, Map<String, Map<String, String>>> message);
}
