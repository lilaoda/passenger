package bus.passenger.module.splash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import bus.passenger.R;
import bus.passenger.base.BaseActivity;
import bus.passenger.base.BaseApplication;
import bus.passenger.data.SpManager;
import bus.passenger.module.DaggerCommonComponent;
import bus.passenger.module.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.utils.StatusBarUtil;


public class SplashActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.fl_root)
    FrameLayout flRoot;
    @Inject
    SpManager mSpManager;

    private ArrayList<View> list;
    private String mVersionName;
    private int mVersionCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //防止用户安装应用时点击打开而不是完成，按home键时导致应用重启的问题
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        DaggerCommonComponent.builder().applicationComponent(BaseApplication.getApplicationComponent()).build().inject(this);

        boolean isStarted = mSpManager.getBoolean(SpManager.IS_STARTED);
        if (!isStarted) {
            initView();
            initData();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    private void initData() {
        mVersionName = getVersionName();
        mVersionCode = getVersionCode();
    }

    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 这里必须传一个activity对象
        builder.setTitle("发现新版本:" + mVersionName);
        builder.setMessage("这是新版本啊啊啊");
        builder.setPositiveButton("立即更新",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadApk();
                    }
                });
        builder.setNegativeButton("以后再说",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void downloadApk() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/waqudao.apk";
            // 开始下载
        } else {
            Toast.makeText(this, "没有找到sdcard!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        View startView = LayoutInflater.from(this).inflate(R.layout.indicator_start, null);
        Button bt_start = (Button) startView.findViewById(R.id.btn_start);
        bt_start.setOnClickListener(this);
        ImageView view1 = new ImageView(this);
        view1.setBackgroundResource(R.mipmap.login_bg);

        list = new ArrayList<>();
        list.add(view1);
        list.add(startView);
        viewPager.setAdapter(new SplashAdapter());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            mSpManager.putBoolean(SpManager.IS_STARTED, true);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private class SplashAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = list.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }
}
