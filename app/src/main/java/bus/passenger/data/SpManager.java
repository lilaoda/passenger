package bus.passenger.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import bus.passenger.bean.PoiInfo;
import lhy.lhylibrary.base.LhyApplication;

/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * SP 管理者
 */


@Singleton
public class SpManager {

    private static final String SP_FILE_NAME = "webus_passenger";
    private static final String HISTORY_POIINFO = "history_poiInfo";
    public static final String IS_STARTED = "is_started";//是否已经启动过

    private SharedPreferences mSP;
    private Gson mGson;

    @Inject
    public SpManager() {
        mSP = LhyApplication.getContext().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    //保存历史地址搜索记录
    public void putHistoryAddress(List<PoiInfo> list) {
        mSP.edit().putString(HISTORY_POIINFO, mGson.toJson(list)).apply();
    }

    public List<PoiInfo> getHistoryAddress() {
        return mGson.fromJson(mSP.getString(HISTORY_POIINFO, "[]"), new TypeToken<List<PoiInfo>>() {
        }.getType());
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
