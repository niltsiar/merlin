package com.novoda.merlin;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class MerlinService extends Service {

    private static boolean isBound;

    private IBinder binder = new LocalBinder();

    private List<ConnectivityChangesRegister> connectivityChangesRegisters = new ArrayList<>();
    private List<ConnectivityChangesForwarder> connectivityChangesForwarders = new ArrayList<>();

    public static boolean isBound() {
        return isBound;
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;

        binder = null;
        return super.onUnbind(intent);
    }

    private void start(ConnectivityChangesRegister connectivityChangesRegister, ConnectivityChangesForwarder connectivityChangesForwarder) {
        assertDependenciesBound(connectivityChangesRegister, connectivityChangesForwarder);
        connectivityChangesForwarder.forwardInitialNetworkStatus();
        connectivityChangesRegister.register(((ConnectivityChangesNotifier) binder));
    }

    private void assertDependenciesBound(ConnectivityChangesRegister connectivityChangesRegister, ConnectivityChangesForwarder connectivityChangesForwarder) {
        if (!MerlinService.this.connectivityChangesRegisters.contains(connectivityChangesRegister)) {
            throw MerlinServiceDependencyMissingException.missing(ConnectivityChangesRegister.class);
        }

        if (!MerlinService.this.connectivityChangesForwarders.contains(connectivityChangesForwarder)) {
            throw MerlinServiceDependencyMissingException.missing(ConnectivityChangesForwarder.class);
        }
    }

    private void forward(ConnectivityChangeEvent connectivityChangeEvent) {
        for(ConnectivityChangesForwarder connectivityChangesForwarder : connectivityChangesForwarders) {
            connectivityChangesForwarder.forward(connectivityChangeEvent);
        }
    }

    public interface ConnectivityChangesNotifier {

        boolean canNotify();

        void notify(ConnectivityChangeEvent connectivityChangeEvent);

    }

    class LocalBinder extends Binder implements ConnectivityChangesNotifier {

        @Override
        public boolean canNotify() {
            return MerlinService.isBound();
        }

        @Override
        public void notify(ConnectivityChangeEvent connectivityChangeEvent) {
            if (!canNotify()) {
                throw new IllegalStateException("You must call canNotify() before calling notify(ConnectivityChangeEvent)");
            }
            MerlinService.this.forward(connectivityChangeEvent);
        }

        void addConnectivityChangesRegister(ConnectivityChangesRegister connectivityChangesRegister) {
            MerlinService.this.connectivityChangesRegisters.add(connectivityChangesRegister);
        }

        void addConnectivityChangesForwarder(ConnectivityChangesForwarder connectivityChangesForwarder) {
            MerlinService.this.connectivityChangesForwarders.add(connectivityChangesForwarder);
        }

        void onBindComplete(ConnectivityChangesRegister connectivityChangesRegister, ConnectivityChangesForwarder connectivityChangesForwarder) {
            MerlinService.this.start(connectivityChangesRegister, connectivityChangesForwarder);
        }

        void removeConnectivityChangesRegister(ConnectivityChangesRegister connectivityChangesRegister) {
            connectivityChangesRegister.unregister();
            MerlinService.this.connectivityChangesRegisters.remove(connectivityChangesRegister);
        }

        void removeConnectivityChangesForwarder(ConnectivityChangesForwarder connectivityChangesForwarder) {
            MerlinService.this.connectivityChangesForwarders.remove(connectivityChangesForwarder);
        }
    }

}
