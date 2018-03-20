package com.clarkxp.merlinbug163;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.NetworkStatus;

import butterknife.ButterKnife;

/**
 * Created by clarkxp on 05-01-18.
 */

public abstract class BaseActivity extends AppCompatActivity implements Connectable, Disconnectable, Bindable {

    private MerlinsBeard merlinsBeard;
    protected Merlin merlin;

    private NETWORK network;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        merlin = new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
        merlinsBeard = MerlinsBeard.from(this);
        afterCreate();
    }

    @LayoutRes
    public abstract int getLayout();

    public abstract void afterCreate();

    @Override
    protected void onResume() {
        super.onResume();
        merlin.registerBindable(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        merlin.bind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        merlin.unbind();

    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (!networkStatus.isAvailable()) {
            onDisconnect();
        }
    }

    @Override
    public void onConnect() {
        if (merlinsBeard.isConnectedToWifi()) {
            showSnackBar("Connected to WiFi");
            network = NETWORK.WIFI;
        } else if (merlinsBeard.isConnectedToMobileNetwork()) {
            showSnackBar("Connected to Mobile Network: " + merlinsBeard.getMobileNetworkSubtypeName());
            network = NETWORK.MOBILE;
        }
    }

    private void showSnackBar(String message) {
       /* if(getAttachView()!=null)
            Snackbar.make(getAttachView(), message, Snackbar.LENGTH_SHORT).show();
        else*/
        showToast(this, message);
    }

    Toast toast;

    public void showToast(Context context, String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onDisconnect() {
        if (network != null) {
            switch (network) {
                case WIFI:
                    showSnackBar("WIFI has been disconnected");
                    break;
                case MOBILE:
                    showSnackBar("Mobile Network has been disconnected");
                    break;
            }
        }
    }

    private enum NETWORK {
        WIFI, MOBILE,
    }
}
