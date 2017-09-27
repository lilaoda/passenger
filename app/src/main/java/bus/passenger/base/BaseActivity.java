package bus.passenger.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import bus.passenger.R;
import lhy.lhylibrary.base.LhyActivity;

/**
 * Created by Liheyu on 2017/9/20.
 * Email:liheyu999@163.com
 */

public class BaseActivity extends LhyActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textTitle = (TextView) findViewById(R.id.toolbar_title);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        textTitle.setText(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
