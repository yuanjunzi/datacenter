package com.git.service.yuanjunzi.boot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
public class ReflectUtil {
    public ReflectUtil() {
    }

    private static Class[] getTypes(Object[] params) {
        Class[] paramTypes = new Class[params.length];

        for(int i = 0; i < params.length; ++i) {
            paramTypes[i] = params[i].getClass();
        }

        return paramTypes;
    }

    public static <T> Constructor<T> getConstructor(ClassLoader classLoader, String className, Class... types) {
        try {
            Class<T> theClass = (Class<T>) classLoader.loadClass(className);
            return theClass.getConstructor(types);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> T instance(Constructor<T> constructor, Object... params) {
        try {
            return constructor.newInstance(params);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T getMethod(Object invoker, String methodName, Class... params) {
        try {
            return (T) invoker.getClass().getMethod(methodName, params);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> T invoke(Object invoker, Method method, Object... params) {
        try {
            return (T) method.invoke(invoker, params);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }
}
