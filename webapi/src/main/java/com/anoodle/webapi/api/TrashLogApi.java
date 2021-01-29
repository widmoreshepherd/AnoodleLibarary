package com.anoodle.webapi.api;


import com.anoodle.webapi.Api;
import com.anoodle.webapi.http.ResponseResult;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TrashLogApi {

    @FormUrlEncoded
    @POST(Api.LOG_UPLOAD)
    Observable<ResponseResult<Object>> trashLog(@Field("file") String file,
                                                @Field("deviceSn") String deviceSn);
}
