package com.cegis.deltaplan2100;

import com.cegis.deltaplan2100.models.ContentData;
import com.cegis.deltaplan2100.models.ModelComponentLevelOne;
import com.cegis.deltaplan2100.models.ModelComponentLevelThree;
import com.cegis.deltaplan2100.models.ModelComponentLevelTwo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface Api {
    String BASE_URL = "http://130.180.3.215:8080/api/";

    @GET("Component/GetComLevelOne")
    Call<List<ModelComponentLevelOne>> getComLevelOne();

    @GET("Component/GetComLevelOne/{id}")
    Call<List<ModelComponentLevelOne>> getComLevelOne(@Path("id") int id);

    @GET("Component/GetComLevelTwo")
    Call<List<ModelComponentLevelTwo>> getComLevelTwo();

    @GET("Component/GetComLevelTwo/{id}")
    Call<List<ModelComponentLevelTwo>> getComLevelTwo(@Path("id") int id);

    @GET("Component/GetComLevelThree")
    Call<List<ModelComponentLevelThree>> getComLevelThree();

    @GET("Component/GetComLevelThree/{id}")
    Call<List<ModelComponentLevelThree>> getComLevelThree(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("Content/GetTextTableHtmlContent/{parent_id}/{parent_level}/{content_type}")
    Call<String> getTextTableHtmlContent(@Path("parent_id") int parent_id,
                                              @Path("parent_level") int parent_level,
                                              @Path("content_type") String content_type);

    @GET("Content/Get/{id}")
    Call<String> getTextGet(@Path("id") int id);
}
