package com.cegis.deltaplan2100;

import com.cegis.deltaplan2100.models.InvestmentProjectList;
import com.cegis.deltaplan2100.models.MacroEconIndicatorPivotData;
import com.cegis.deltaplan2100.models.MacroEconIndicatorsList;
import com.cegis.deltaplan2100.models.ModelComponentLevelOne;
import com.cegis.deltaplan2100.models.ModelComponentLevelThree;
import com.cegis.deltaplan2100.models.ModelComponentLevelTwo;
import com.cegis.deltaplan2100.models.ModelSendFeedback;

import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface API {
    String BASE_URL = "https://130.180.3.215:8080/api/";
    //String BASE_URL = "https://130.180.3.215/api/";
    //String BASE_URL = "http://130.180.3.146/BDP2100API/api/";
    //String BASE_URL = "https://202.53.173.179/BDP2100API/api/"; //online server

    //String MAP_BASE_URL = "http://130.180.3.215:7669/AppMaps/";
    //String MAP_BASE_URL = "https://130.180.3.215/AppMaps/";
    String MAP_BASE_URL = "https://202.53.173.179/BDP2100AppMap/AppMaps/"; //online server
    //String MAP_BASE_URL = "http://130.180.3.146/BDP2100API/AppMaps/"; //online server

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

    @GET("Content/GetMacroEconIndicator/{parent_id}/{parent_level}/{mei_type}")
    Call<List<MacroEconIndicatorsList>> getMacroEconIndicatorList(@Path("parent_id") int parent_id,
                                                                  @Path("parent_level") int parent_level,
                                                                  @Path("mei_type") int mei_type);

    @GET("Content/GetFiscalYearList")
    Call<List<String>> getFiscalYearList();

    @GET("Content/MacroEconIndicatorPivotData/{indicator_name}")
    Call<List<MacroEconIndicatorPivotData>> getMacroEconIndiPivotDataList(@Path("indicator_name") String indicator_name);

//    @FormUrlEncoded
//    @POST("Content/SendFeedback")
//    Call<String> sendFeedback(@Body ModelSendFeedback modelSendFeedback);

//    @FormUrlEncoded
//    @POST("Content/SendFeedback")
//    Call<String> sendFeedback(@Field("user_name") String uName, @Field("phone_no") String phoneNo, @Field("user_email") String uEmail, @Field("user_comment") String uComment);

    @POST("Content/SendFeedback")
    Call<String> sendFeedback(String user_name, String phone_no, String user_email, String user_comment);

    @Headers("Content-Type: application/json")
    @GET("Map/GetLgedMapLayer")
    Call<String> getLgedMapLayer();

    @GET("Content/InvestmentProjectHotspotList")
    Call<List<InvestmentProjectList>> getInvestmentProjectHotspotList();

    @GET("Content/InvestmentProjectList/{hotspot}")
    Call<List<InvestmentProjectList>> getInvestmentProjectList(@Path("hotspot") String hotspot);

    @GET("Content/InvestmentProjectLayer/{code}")
    Call<String> getInvestmentProjectLayer(@Path("code") String code);

    @GET("Content/BwdbProjectLayer")
    Call<String> getBwdbProjectLayer();
}


