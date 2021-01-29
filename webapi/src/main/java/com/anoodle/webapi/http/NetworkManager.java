package com.anoodle.webapi.http;

import com.anoodle.webapi.Api;
import com.anoodle.webapi.api.ClassifyTypeApi;
import com.anoodle.webapi.api.CnbotGarbageApi;
import com.anoodle.webapi.api.TrashBaseApi;
import com.anoodle.webapi.api.TrashLogApi;
import com.anoodle.webapi.api.UploadApi;
import com.anoodle.webapi.bean.CnAnswer;
import com.anoodle.webapi.bean.TrashDetailsPo;
import com.anoodle.webapi.bean.WasteCategory;
import com.anoodle.webapi.subscribers.ProgressSubscriber;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class NetworkManager {

    private static final int DEFAULT_TIMEOUT = 120;
    private static volatile NetworkManager instance;
    private Retrofit retrofit;

    private NetworkManager(){
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).
                readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okHttpClientBuilder.addInterceptor(new LoggingInterceptor());
        retrofit = new Retrofit.Builder()
                .client(okHttpClientBuilder.build())
                .addConverterFactory(ResponseConvertFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Api.BASE_URL).build();
    }

    public static NetworkManager getInstance(){
        if(instance==null){
            synchronized (NetworkManager.class){
                if(instance==null){
                    instance = new NetworkManager();
                }
            }
        }
        return instance;
    }

    private <T> void toSubscribe(Observable<T> observable, Observer<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void trashBase(ProgressSubscriber<ResponseResult<List<TrashDetailsPo>>> progressSubscriber, String trashId){
        TrashBaseApi api = retrofit.create(TrashBaseApi.class);
        Observable<ResponseResult<List<TrashDetailsPo>>> observable = api.trashBase(trashId);
        toSubscribe(observable,progressSubscriber);
    }

    public void classifyType(ProgressSubscriber<ResponseResult<List<WasteCategory>>> progressSubscriber, String name){
        ClassifyTypeApi classifyTypeApi = retrofit.create(ClassifyTypeApi.class);
        Observable<ResponseResult<List<WasteCategory>>> observable = classifyTypeApi.classifyType(name);
        toSubscribe(observable,progressSubscriber);
    }

    public void uploadFile(ProgressSubscriber<ResponseResult<String>> progressSubscriber, RequestBody body) {
        UploadApi uploadApi = retrofit.create(UploadApi.class);
        Observable<ResponseResult<String>> observable = uploadApi.uploadFile(body);
        toSubscribe(observable, progressSubscriber);
    }

    public void trashLog(ProgressSubscriber<ResponseResult<Object>> progressSubscriber, String file,String deviceSn) {
        TrashLogApi trashLogApi = retrofit.create(TrashLogApi.class);
        Observable<ResponseResult<Object>> observable = trashLogApi.trashLog(file,deviceSn);
        toSubscribe(observable, progressSubscriber);
    }

    public void cnbotGarbage(ProgressSubscriber<CnAnswer> progressSubscriber, RequestBody body){
        CnbotGarbageApi api = retrofit.create(CnbotGarbageApi.class);
        Observable<CnAnswer> observable = api.cnbotGarbage(body);
        toSubscribe(observable,progressSubscriber);
    }

}
