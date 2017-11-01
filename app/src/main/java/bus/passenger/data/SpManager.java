package bus.passenger.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import bus.passenger.bean.City;
import bus.passenger.bean.PoiInfo;
import lhy.lhylibrary.base.LhyApplication;

/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * SP 管理者
 */


public class SpManager {

    private static final String SP_FILE_NAME = "webus_passenger";
    private static final String HISTORY_POIINFO = "history_poiInfo";
    public static final String HISTORY_SELECTED_CITY = "history_selected";
    public static final String IS_STARTED = "is_started";//是否已经启动过

    private static SpManager instance;
    private SharedPreferences mSP;
    private final Gson mGson;

    public static synchronized SpManager instance() {
        if (instance == null) {
            synchronized (SpManager.class) {
                if (instance == null) {
                    instance = new SpManager();
                }
            }
        }
        return instance;
    }

    public SpManager() {
        mSP = LhyApplication.getContext().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    //保存历史地址搜索记录
    public void putHistoryAddress(@Nullable List<PoiInfo> list) {
        if (list == null) {
            return;
        }
        mSP.edit().putString(HISTORY_POIINFO, mGson.toJson(list)).apply();
    }

    @NonNull
    public List<PoiInfo> getHistoryAddress() {
        return mGson.fromJson(mSP.getString(HISTORY_POIINFO, "[]"), new TypeToken<List<PoiInfo>>() {
        }.getType());
    }

    //保存历史地址搜索记录
    public void putSelectedCity(@Nullable City city) {
        if (city == null) {
            return;
        }
        mSP.edit().putString(HISTORY_SELECTED_CITY, mGson.toJson(city)).apply();
    }

    @Nullable
    public City getSelectedCity() {
        return mGson.fromJson(mSP.getString(HISTORY_SELECTED_CITY, null), City.class);
    }

    public void putString(String key, String value) {
        mSP.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return mSP.getString(key, "");
    }

    public void putInt(String key, int value) {
        mSP.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return mSP.getInt(key, 0);
    }

    public void putBoolean(String key, boolean value) {
        mSP.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return mSP.getBoolean(key, false);
    }

}
