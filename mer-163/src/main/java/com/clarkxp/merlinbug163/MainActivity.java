package com.clarkxp.merlinbug163;

import android.content.Intent;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void afterCreate() {

    }

    @OnClick(R.id.btnFirst)
    public void openFirst() {
        Intent intent = new Intent(this, FirstActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnSecond)
    public void openSecond() {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);

    }
}
