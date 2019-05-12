package com.git.service.yuanjunzi.datacenter.infrastructure.utils;

import javafx.util.Pair;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by Eason Yang on 17/03/2018.
 * OkHttp 工具类
 * OkHttp 官网：http://square.github.io/okhttp/
 */
public class OkHttpUtil {
    /**
     * 预置的 JSON 请求类型
     */
    public final static String JSON_TYPE = "application/json; charset=UTF-8";

    /**
     * 单例 OkHttpClient 对象
     */
    private volatile static OkHttpClient singletonClient;

    /**
     * 单例模式获取或以默认参数初始化 OkHttpClient
     *
     * @return OkHttpClient 单例对象
     */
    public static OkHttpClient getInstance() {
        if (singletonClient == null) {
            synchronized (OkHttpUtil.class) {
                if (singletonClient == null) {
                    singletonClient = new OkHttpClient();
                }
            }
        }
        return singletonClient;
    }

    /**
     * POST JSON 串
     *
     * @param url     请求 url
     * @param body    请求体
     * @param headers 请求头
     * @return 响应体的内容
     * @throws IOException IO 异常
     */
    public static String postJSON(String url,
                                  String body,
                                  List<Pair<String, String>> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(JSON_TYPE), body))
                .build();
        return transformResponseToString(handleSyncPost(getInstance(), setupHeaders(request, headers)));
    }

    /**
     * POST JSON 串
     *
     * @param url     请求 url
     * @param body    请求体
     * @param headers 请求头
     * @return 响应体的内容
     * @throws IOException IO 异常
     */
    public static Response postJSONWithResponse(String url,
                                                String body,
                                                List<Pair<String, String>> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(JSON_TYPE), body))
                .build();
        return handleSyncPost(getInstance(), setupHeaders(request, headers));
    }

    /**
     * POST 字符串
     *
     * @param url       请求 url
     * @param body      请求体
     * @param mediaType 内容类型
     * @param headers   请求头
     * @return 响应体的内容
     * @throws IOException IO 异常
     */
    public static String post(String url,
                              String body,
                              String mediaType,
                              List<Pair<String, String>> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(mediaType), body))
                .build();
        return transformResponseToString(handleSyncPost(getInstance(), setupHeaders(request, headers)));
    }

    /**
     * POST 自定义请求体
     *
     * @param url     请求 url
     * @param body    请求体
     * @param headers 请求头
     * @return 响应体的内容
     * @throws IOException IO 异常
     */
    public static String post(String url,
                              RequestBody body,
                              List<Pair<String, String>> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return transformResponseToString(handleSyncPost(getInstance(), setupHeaders(request, headers)));
    }

    /**
     * GET 获取内容
     *
     * @param url     请求 url
     * @param headers 请求头
     * @return 响应体的内容
     * @throws IOException IO 异常
     */
    public static String get(String url,
                             List<Pair<String, String>> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return transformResponseToString(handleSyncGet(getInstance(), setupHeaders(request, headers)));
    }

    /**
     * POST 获取内容
     *
     * @param request 自定义请求体
     * @throws IOException IO 异常
     */
    public static Response post(Request request) throws IOException {
        return handleSyncPost(getInstance(), request);
    }

    /**
     * GET 获取内容
     *
     * @param request 自定义请求体
     * @throws IOException IO 异常
     */
    public static Response get(Request request) throws IOException {
        return handleSyncGet(getInstance(), request);
    }

    /**
     * 异步 POST JSON 串
     *
     * @param url      请求 url
     * @param body     请求体
     * @param headers  请求头
     * @param callback 回调函数
     */
    public static void postJSONAsync(String url,
                                     String body,
                                     List<Pair<String, String>> headers,
                                     Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(JSON_TYPE), body))
                .build();
        handleAsyncPost(getInstance(), setupHeaders(request, headers), callback);
    }

    /**
     * 异步 POST 字符串
     *
     * @param url       请求 url
     * @param body      请求体
     * @param mediaType 内容类型
     * @param headers   请求头
     * @param callback  回调函数
     */
    public static void postAsync(String url,
                                 String body,
                                 String mediaType,
                                 List<Pair<String, String>> headers,
                                 Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(mediaType), body))
                .build();
        handleAsyncPost(getInstance(), setupHeaders(request, headers), callback);
    }

    /**
     * 异步 POST 自定义请求体
     *
     * @param url      请求 url
     * @param body     请求体
     * @param headers  请求头
     * @param callback 回调函数
     */
    public static void postAsync(String url,
                                 RequestBody body,
                                 List<Pair<String, String>> headers,
                                 Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        handleAsyncPost(getInstance(), setupHeaders(request, headers), callback);
    }

    /**
     * 异步 GET 获取内容
     *
     * @param url      请求 url
     * @param headers  请求头
     * @param callback 回调函数
     */
    public static void getAsync(String url,
                                List<Pair<String, String>> headers,
                                Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        handleAsyncGet(getInstance(), setupHeaders(request, headers), callback);
    }

    /**
     * 异步 POST 获取内容
     *
     * @param request  请求体
     * @param callback 回调函数
     */
    public static void postAsync(Request request, Callback callback) {
        handleAsyncPost(getInstance(), request, callback);
    }

    /**
     * 异步 GET 获取内容
     *
     * @param request  自定义请求体
     * @param callback 回调函数
     */
    public static void getAsync(Request request, Callback callback) {
        handleAsyncGet(getInstance(), request, callback);
    }

    /**
     * 设置请求体的请求头
     *
     * @param request 请求体对象
     * @param headers 请求头
     * @return 处理后的请求体
     */
    public static Request setupHeaders(Request request, List<Pair<String, String>> headers) {
        for (Pair<String, String> header : headers) {
            request = request.newBuilder().header(header.getKey(), header.getValue()).build();
        }
        return request;
    }

    /**
     * 获取响应中的响应体内容
     *
     * @param response 响应对象
     * @return 响应体中的内容
     * @throws IOException IO 异常
     */
    private static String transformResponseToString(Response response) throws IOException {
        try {
            if (response != null && response.body() != null) {
                return response.body().string();
            }
            return "";
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 同步处理 post 请求
     *
     * @param client  OkHttpClient 对象
     * @param request 请求体
     */
    public static Response handleSyncPost(OkHttpClient client, Request request) throws IOException {
        return client.newCall(request).execute();
    }

    /**
     * 同步处理 get 请求
     *
     * @param client  OkHttpClient 对象
     * @param request 请求体
     */
    public static Response handleSyncGet(OkHttpClient client, Request request) throws IOException {
        return client.newCall(request).execute();
    }

    /**
     * 异步处理 post 请求
     *
     * @param client   OkHttpClient 对象
     * @param request  请求体
     * @param callback 回调
     */
    public static void handleAsyncPost(OkHttpClient client, Request request, Callback callback) {
        client.newCall(request).enqueue(callback);
    }

    /**
     * 异步处理 get 请求
     *
     * @param client   OkHttpClient 对象
     * @param request  请求体
     * @param callback 回调
     */
    public static void handleAsyncGet(OkHttpClient client, Request request, Callback callback) {
        client.newCall(request).enqueue(callback);
    }
}
