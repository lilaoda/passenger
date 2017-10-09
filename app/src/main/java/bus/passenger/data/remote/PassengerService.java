package bus.passenger.data.remote;

import java.util.List;

import bus.passenger.bean.LoginResult;
import bus.passenger.bean.OrderInfo;
import bus.passenger.bean.RegisterResult;
import bus.passenger.bean.param.CallCarParam;
import bus.passenger.bean.param.LoginParam;
import bus.passenger.bean.param.RegistParam;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 * 乘客服务API
 */

public interface PassengerService {

    String BASE_URL = "http://192.168.8.41:8183/";

    @POST("passenger/register")
    Observable<HttpResult<RegisterResult>> regist(@Body RegistParam param);

    @POST("passenger/login")
    Observable<HttpResult<LoginResult>> login(@Body LoginParam param);

    @POST("passenger/callCar")
    Observable<HttpResult<LoginResult>> callCar(@Body CallCarParam param);

    @POST("passenger/findTrip")
    Observable<HttpResult<List<OrderInfo>>> findTrip();


}