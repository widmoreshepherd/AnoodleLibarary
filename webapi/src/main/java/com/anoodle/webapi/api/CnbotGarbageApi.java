package com.anoodle.webapi.api;

import com.anoodle.webapi.Api;
import com.anoodle.webapi.bean.CnAnswer;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CnbotGarbageApi {

    @POST(Api.CNBOT_GARBAGE_CLASSIFY)
    Observable<CnAnswer> cnbotGarbage(@Body RequestBody body);
}
