package bus.passenger.data.remote;

import java.util.List;

import bus.passenger.bean.OrderInfo;
import io.reactivex.Observable;
import retrofit2.http.POST;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 */

public interface PushService {

    String BASE_URL = "http://192.168.8.58:8883/";

    /**
     * 通知乘客司机已经接单
     * @return
     */
    @POST("communication/pushOrderConfirm")
    Observable<HttpResult<List<OrderInfo>>> pushOrderConfirm();

}
