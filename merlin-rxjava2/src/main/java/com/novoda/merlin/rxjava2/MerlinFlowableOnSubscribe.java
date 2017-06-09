package com.novoda.merlin.rxjava2;

import android.support.annotation.NonNull;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;

class MerlinFlowableOnSubscribe implements FlowableOnSubscribe<NetworkStatus> {

    private Merlin merlin;
    private Connectable connectable;
    private Disconnectable disconnectable;
    private Bindable bindable;

    MerlinFlowableOnSubscribe(Merlin merlin) {
        this.merlin = merlin;
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<NetworkStatus> emitter) throws Exception {
        createRegisterables(emitter);
        merlin.registerConnectable(connectable);
        merlin.registerDisconnectable(disconnectable);
        merlin.registerBindable(bindable);

        emitter.setCancellable(createCancellable());

        merlin.bind();
    }

    private void createRegisterables(FlowableEmitter<NetworkStatus> emitter) {
        connectable = createConnectable(emitter);
        disconnectable = createDisconnectable(emitter);
        bindable = createBindable(emitter);
    }


    private Connectable createConnectable(final FlowableEmitter<NetworkStatus> emitter) {
        return new Connectable() {
            @Override
            public void onConnect() {
                emitter.onNext(NetworkStatus.newAvailableInstance());
            }
        };
    }

    private Disconnectable createDisconnectable(final FlowableEmitter<NetworkStatus> emitter) {
        return new Disconnectable() {
            @Override
            public void onDisconnect() {
                emitter.onNext(NetworkStatus.newUnavailableInstance());
            }
        };
    }

    private Bindable createBindable(final FlowableEmitter<NetworkStatus> emitter) {
        return new Bindable() {
            @Override
            public void onBind(NetworkStatus current) {
                emitter.onNext(current);
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
