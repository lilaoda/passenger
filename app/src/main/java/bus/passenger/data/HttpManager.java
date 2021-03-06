package bus.passenger.data;


import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import bus.passenger.BuildConfig;
import bus.passenger.data.remote.HeadIntercepter;
import bus.passenger.data.remote.PassengerApi;
import bus.passenger.data.remote.PushApi;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HttpManager {

    private static final int CONNECTIMEOUT = 10000;
    private static final int READTIMEOUT = 10000;
    private static final String CACHE_DIRECTORY_NAME = "file_cache";
    private static final long CACHE_MAX_SIZE = 1024 * 1024 * 100;//100M

    private static HttpManager instance;
    private Retrofit.Builder mRetrofitBuilder;
    private PassengerApi mPassengerService;
    private PushApi mPushService;

    private HttpManager() {
        mRetrofitBuilder = new Retrofit.Builder()
                .baseUrl(PassengerApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttp());
    }

    public static synchronized HttpManager instance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new HttpManager();
                }
            }
        }
        return instance;
    }

    private OkHttpClient getOkHttp() {
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECTIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READTIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
//                 .cache(new Cache(FileUtils.getCacheFile(LhyApplication.getContext(), CACHE_DIRECTORY_NAME), CACHE_MAX_SIZE))
//                .addInterceptor(new CacheIntercepter(LhyApplication.getContext()))
                .addInterceptor(new HeadIntercepter(DbManager.instance()))
                .addInterceptor(getHttpLogIntercept())
                .connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
                .build();
    }

    private LoggingInterceptor getHttpLogIntercept() {
        return new LoggingInterceptor.Builder()
                .loggable(BuildConfig.DEBUG)
                .setLevel(Level.BASIC)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .addHeader("version", android.support.graphics.drawable.animated.BuildConfig.VERSION_NAME)
                .build();
    }

    public PassengerApi getPassengerService() {
        if (mPassengerService == null) {
            mPassengerService = mRetrofitBuilder.build().create(PassengerApi.class);
        }
        return mPassengerService;
    }

    public PushApi getPushService() {
        if (mPushService == null) {
            mPushService = mRetrofitBuilder.baseUrl(PushApi.BASE_URL).build().create(PushApi.class);
        }
        return mPushService;
    }

}
