package com.clarkxp.merlinbug163;

import android.content.Intent;

import butterknife.OnClick;

/**
 * Created by clarkxp on 20-03-18.
 */

public class SecondActivity extends OtherActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_other_third;
    }

    @OnClick(R.id.btnThird)
    public void openThird() {
        Intent intent = new Intent(this, ThirdActivity.class);
        startActivity(intent);
    }
}
