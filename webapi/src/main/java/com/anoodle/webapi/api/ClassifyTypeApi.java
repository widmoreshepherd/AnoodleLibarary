package com.anoodle.webapi.api;


import com.anoodle.webapi.Api;
import com.anoodle.webapi.bean.WasteCategory;
import com.anoodle.webapi.http.ResponseResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClassifyTypeApi {

    @GET(Api.CLASSFIFY_TYPE)
    Observable<ResponseResult<List<WasteCategory>>> classifyType(@Query("name") String name);
}
