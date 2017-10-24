package bus.passenger.base;

import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;

import lhy.lhylibrary.base.LhyApplication;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 */

public class BaseApplication extends LhyApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        initLeakCanary();
        initLogger();
        FileDownloader.setup(this);
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }


    private void initLogger() {
        Logger.init()
                .methodCount(5)
//                .hideThreadInfo()
                .logLevel(LogLevel.FULL)
                .methodOffset(5);
    }



}
