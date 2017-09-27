package bus.passenger.module;

import bus.passenger.base.ApplicationComponent;
import bus.passenger.module.main.MainActivity;
import bus.passenger.module.main.MainFragment;
import bus.passenger.module.main.SearchAddressActivity;
import bus.passenger.module.setting.SettingActivity;
import bus.passenger.module.splash.SplashActivity;
import dagger.Component;

/**
 * Created by Lilaoda on 2017/9/26.
 * Email:749948218@qq.com
 * 通用的注入组件
 */

@CommonScoped
@Component(dependencies = ApplicationComponent.class)
public interface CommonComponent {

    void inject(SplashActivity activity);

    void inject(MainActivity activity);

    void inject(SearchAddressActivity activity);

    void inject(SettingActivity activity);

    void inject(MainFragment mainFragment);

}
