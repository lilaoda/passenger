package bus.passenger.module.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import bus.passenger.R;
import bus.passenger.base.BaseActivity;
import bus.passenger.data.DbManager;
import bus.passenger.data.entity.User;
import bus.passenger.module.customerservice.CustomerServiceActivity;
import bus.passenger.module.route.RouteActivity;
import bus.passenger.module.setting.SettingActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.base.GlideApp;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    DbManager mDbManager;

    private MainFragment mMainFragment;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDbManager = DbManager.instance();
        initView();
        initContent();
    }

    private void initContent() {
        if (mMainFragment == null) {
            mMainFragment = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, mMainFragment).commit();
        }
    }

    private void initView() {
        initNavHeadView();
        toolbar.setTitle("");
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(mDrawerListener);
        setSupportActionBar(toolbar);
        navView.setNavigationItemSelectedListener(this);
        toolbar.setOnMenuItemClickListener(this);
        updateDrawerToggle(true);
    }

    private void initNavHeadView() {
        User user = mDbManager.getUser();
        View headerView = navView.getHeaderView(0);
        ImageView userIcon = (ImageView) headerView.findViewById(R.id.img_photo);
        TextView userName = (TextView) headerView.findViewById(R.id.text_name);
        if (user == null) {
            userName.setText("请登陆");
        } else {
            userName.setText(user.getPhone());
            GlideApp.with(this).load(user.getIconUrl()).error(R.mipmap.icon_user_default).into(userIcon);
        }
    }

    private final DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerClosed(View drawerView) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerStateChanged(newState);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerOpened(drawerView);
        }
    };


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        mMainFragment.onBackCallback();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //   toolbar.inflateMenu(R.menu.main);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
//        if (item != null && item.getItemId() == R.id.action_settings) {
//            ToastUtils.showString("setting!!!!");
//            return true;
//        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // If not handled by drawerToggle, home needs to be handled by returning to previous
        if (item != null && item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item != null && item.getItemId() == R.id.action_settings) {
            ToastUtils.showString("setting");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_route) {
            gotoActivity(RouteActivity.class);
        } else if (id == R.id.nav_service) {
            gotoActivity(CustomerServiceActivity.class);
        } else if (id == R.id.nav_setting) {
            gotoActivity(SettingActivity.class);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void gotoActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    @Override
    public void setStatusBar() {
        DrawerLayout drawlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        StatusBarUtil.setColorForDrawerLayout(this, drawlayout, Color.WHITE);
    }

    public void updateDrawerToggle(boolean isRoot) {
        if (mDrawerToggle == null) {
            return;
        }
//        boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
        mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
            getSupportActionBar().setHomeButtonEnabled(!isRoot);
        }
        if (isRoot) {
            mDrawerToggle.syncState();
        }
    }

}
