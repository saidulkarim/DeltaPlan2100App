package com.cegis.deltaplan2100.utility;

import com.cegis.deltaplan2100.API;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetJsonResponse {
    public static OkHttpClient client = API.getUnsafeOkHttpClient();

    public static String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
