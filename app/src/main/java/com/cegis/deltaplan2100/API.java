package com.cegis.deltaplan2100;

import com.cegis.deltaplan2100.models.ModelComponentLevelOne;
import com.cegis.deltaplan2100.models.ModelComponentLevelThree;
import com.cegis.deltaplan2100.models.ModelComponentLevelTwo;

import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface API {
    //String BASE_URL = "http://130.180.3.215:8080/api/";
    String BASE_URL = "https://130.180.3.215/api/";

    static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = okHttpClient.newBuilder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
}


