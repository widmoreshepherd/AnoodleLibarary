package com.anoodle.webapi.api;


import com.anoodle.webapi.Api;
import com.anoodle.webapi.bean.TrashDetailsPo;
import com.anoodle.webapi.http.ResponseResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrashBaseApi {

    @GET(Api.TRASH_BASE)
    Observable<ResponseResult<List<TrashDetailsPo>>> trashBase(@Query("trashId") String trashId);
}
