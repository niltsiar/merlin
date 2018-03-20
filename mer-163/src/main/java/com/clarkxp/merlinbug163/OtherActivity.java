package com.clarkxp.merlinbug163;

import android.widget.TextView;

import butterknife.BindView;

/**
 * Created by clarkxp on 20-03-18.
 */

public abstract class OtherActivity extends BaseActivity {

    @BindView(R.id.tvActivity)
    TextView tvActivity;

    @Override
    public int getLayout() {
        return R.layout.activity_other;
    }

    @Override
    public void afterCreate() {
        tvActivity.setText(getClass().getSimpleName());
    }
}
