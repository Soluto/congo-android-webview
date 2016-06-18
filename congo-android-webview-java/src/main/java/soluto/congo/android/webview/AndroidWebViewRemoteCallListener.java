package soluto.congo.android.webview;

import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

import rx.Observable;
import rx.Scheduler;
import rx.subjects.PublishSubject;
import soluto.congo.core.RemoteCall;
import soluto.congo.core.RemoteCallListener;

public class AndroidWebViewRemoteCallListener implements RemoteCallListener {
    private PublishSubject<RemoteCall> mRemoteCalls = PublishSubject.create();

    public AndroidWebViewRemoteCallListener(Scheduler incomingCallScheduler) {
        mRemoteCalls.observeOn(incomingCallScheduler);
    }

    @JavascriptInterface
    public void send(String serializedRemoteCall) {
        RemoteCall remoteCall = new Gson().fromJson(serializedRemoteCall, RemoteCall.class);
        mRemoteCalls.onNext(remoteCall);
    }

    public Observable<RemoteCall> getRemoteCalls() {
        return mRemoteCalls;
    }
}
