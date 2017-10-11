package bus.passenger.data.remote;

import bus.passenger.bean.RegisterResult;
import bus.passenger.bean.param.RegistParam;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 */

public interface PushService {

    String BASE_URL = "http://192.168.8.58:8883/";

    @POST("communication/pushOrderConfirm")
    Observable<HttpResult<RegisterResult>> regist(@Body RegistParam param);

}
