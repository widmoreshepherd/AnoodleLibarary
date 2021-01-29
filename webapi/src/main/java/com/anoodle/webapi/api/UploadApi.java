package com.anoodle.webapi.api;


import com.anoodle.webapi.Api;
import com.anoodle.webapi.http.ResponseResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UploadApi {

    @POST(Api.UPLOAD)
    Observable<ResponseResult<String>> uploadFile(@Body RequestBody Body);
}
