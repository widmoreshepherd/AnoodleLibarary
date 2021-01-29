package com.anoodle.webapi.http;

import com.anoodle.webapi.utils.LogUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 网络日志拦截
 * Creator：杜凯
 * Date：2018/3/14/17:36
 */

public class LoggingInterceptor implements Interceptor {

    private final String TAG = "--------------------";
    private final String ISO8859_1 = "ISO8859-1";
    private final String UTF_8 = "UTF-8";
    private final Charset CHARSET_UTF8 = Charset.forName(UTF_8);

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        RequestBody requestBody = request.body();

        String body = null;

        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            MediaType contentType = requestBody.contentType();
            body = URLEncoder.encode(buffer.readString(Charset.forName(ISO8859_1)));
            body = URLDecoder.decode(body,UTF_8);
        }
        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        String rBody = null;

        if (HttpHeaders.hasBody(response)) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = CHARSET_UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(CHARSET_UTF8);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }
            }
            rBody = buffer.clone().readString(charset);
        }

        LogUtils.e(TAG + "收到响应",response.code() + "," + response.message() + "," + tookMs);
        LogUtils.e(TAG + "请求url",response.request().url().toString());
        if(body!=null){
            LogUtils.e(TAG + "请求body",body);
        }
        //LogUtils.e("响应body",rBody);

        return response;
    }
}