package com.git.service.yuanjunzi.datacenter.domain.converter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by yuanjunzi on 2019/4/24.
 * 转换器，负责各层级数据对象转换
 */
public class Converter<T, U> {

    private Function<T, U> fromT;

    private Function<U, T> fromU;

    Converter(final Function<T, U> fromT, final Function<U, T> fromU) {
        this.fromT = fromT;
        this.fromU = fromU;
    }

    /**
     * 由U转换为T
     */
    public final T convertToFirstOne(final U u) {
        return fromU.apply(u);
    }

    /**
     * 由T转换为U
     */
    public final U convertToSecondOne(final T t) {
        return fromT.apply(t);
    }

    /**
     * 批量由T转换为U
     */
    public final List<U> batchConvertToSecondOne(final List<T> tList) {
        return tList.stream().map(this::convertToSecondOne).collect(Collectors.toList());
    }

    /**
     * 批量由U转换为T
     */
    public final List<T> batchConvertToFirstOne(final List<U> uList) {
        return uList.stream().map(this::convertToFirstOne).collect(Collectors.toList());
    }
}
