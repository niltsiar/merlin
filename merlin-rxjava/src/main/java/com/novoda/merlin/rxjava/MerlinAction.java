package com.novoda.merlin.rxjava;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import rx.Emitter;
import rx.functions.Action1;
import rx.functions.Cancellable;

class MerlinAction implements Action1<Emitter<NetworkStatus>> {

    private Merlin merlin;
    private Connectable connectable;
    private Disconnectable disconnectable;
    private Bindable bindable;

    MerlinAction(Merlin merlin) {
        this.merlin = merlin;
    }

    @Override
    public void call(Emitter<NetworkStatus> stateEmitter) {
        createRegisterables(stateEmitter);
        merlin.registerConnectable(connectable);
        merlin.registerDisconnectable(disconnectable);
        merlin.registerBindable(bindable);

        stateEmitter.setCancellation(createCancellable());

        merlin.bind();
    }

    private void createRegisterables(Emitter<NetworkStatus> stateEmitter) {
        connectable = createConnectable(stateEmitter);
        disconnectable = createDisconnectable(stateEmitter);
        bindable = createBindable(stateEmitter);
    }

    private Connectable createConnectable(final Emitter<NetworkStatus> stateEmitter) {
        return new Connectable() {
            @Override
            public void onConnect() {
                stateEmitter.onNext(NetworkStatus.newAvailableInstance());
            }
        };
    }

    private Disconnectable createDisconnectable(final Emitter<NetworkStatus> stateEmitter) {
        return new Disconnectable() {
            @Override
            public void onDisconnect() {
                stateEmitter.onNext(NetworkStatus.newUnavailableInstance());
            }
        };
    }

    private Bindable createBindable(final Emitter<NetworkStatus> stateEmitter) {
        return new Bindable() {
            @Override
            public void onBind(NetworkStatus current) {
                stateEmitter.onNext(current);
            }
        };
    }

    private Cancellable createCancellable() {
        return new Cancellable() {
            @Override
            public void cancel() throws Exception {
                merlin.unbind();
                connectable = null;
                disconnectable = null;
                bindable = null;
            }
        };
    }
}
