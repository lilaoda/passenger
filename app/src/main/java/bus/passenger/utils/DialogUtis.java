package bus.passenger.utils;

import android.app.Activity;

import com.bigkoo.pickerview.TimePickerView;

import java.util.Calendar;

/**
 * Created by Lilaoda on 2017/11/1.
 * Email:749948218@qq.com
 */

public class DialogUtis {

    public static TimePickerView createTimePickView(Activity context, TimePickerView.OnTimeSelectListener listener) {
        //显示预约时间 显示三天之内
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_MONTH, 2);
        return new TimePickerView.Builder(context, listener)
                .setType(new boolean[]{false, true, true, true, true, false})// 默认全部显示
                .setContentSize(15)//滚轮文字大小
                .isCyclic(false)//是否循环滚动
                .setRangDate(startCalendar, endCalendar)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(true)
                .build();
    }

//    public static OptionsPickerView createCityPickView(Activity context, TimePickerView.OnTimeSelectListener listener) {
//
//        return new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3, View v) {
//            }
//        })
//                .setSubmitText("确定")//确定按钮文字
//                .setCancelText("取消")//取消按钮文字
//                .setTitleText("城市选择")//标题
//                .setSubCalSize(18)//确定和取消文字大小
//                .setTitleSize(20)//标题文字大小
//                .setTitleColor(Color.BLACK)//标题文字颜色
//                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
//                .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
//                .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
//                .setContentTextSize(18)//滚轮文字大小
////                .setLinkage(true)//设置是否联动，默认true
//                .setLabels("省", "市", "区")//设置选择的三级单位
//                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                .setCyclic(false, false, false)//循环与否
//                .setSelectOptions(1, 1, 1)  //设置默认选中项
//                .setOutSideCancelable(false)//点击外部dismiss default true
//                .isDialog(true)//是否显示为对话框样式
//                .build();
//
//        //  pvOptions.setPicker(options1Items, options2Items, options3Items);//添加数据源
//    }
}
