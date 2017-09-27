package bus.passenger.base;

import com.google.gson.Gson;

import javax.inject.Singleton;

import bus.passenger.data.AMapManager;
import bus.passenger.data.DbManager;
import bus.passenger.data.HttpManager;
import bus.passenger.data.SpManager;
import dagger.Component;

/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * //全局组件 ，所有的单例；
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    HttpManager getHttpManager();

    AMapManager getAMapManager();

    SpManager getSpManager();

    DbManager getDbManager();

    Gson getGson();
}
