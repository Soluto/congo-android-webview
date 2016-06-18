package soluto.congo.android.webview;

import android.webkit.WebView;

import com.google.gson.Gson;

import rx.Completable;
import rx.Notification;
import rx.Scheduler;
import rx.functions.Action0;
import soluto.congo.core.RemoteCallResponder;
import soluto.congo.core.RemoteCallResult;

public class AndroidWebViewRemoteCallResponder implements RemoteCallResponder {
    private WebView webview;
    private Scheduler responseScheduler;

    public AndroidWebViewRemoteCallResponder(WebView webView, Scheduler responseScheduler) {
        this.webview = webView;
        this.responseScheduler = responseScheduler;
    }

    @Override
    public Completable respond(final RemoteCallResult remoteCallResult) {
        return Completable.fromAction(new Action0() {
            @Override
            public void call() {
                if (remoteCallResult.notification.getKind() == Notification.Kind.OnNext) {
                    webview.loadUrl("javascript:var callback = window['android_" + remoteCallResult.correlationId + "_next']; if (callback) callback(" + new Gson().toJson(remoteCallResult.notification.getValue()) + ")");
                }
                if (remoteCallResult.notification.getKind() == Notification.Kind.OnCompleted) {
                    webview.loadUrl("javascript:var callback = window['android_" + remoteCallResult.correlationId + "_complete']; if (callback) callback()");
                }
                if (remoteCallResult.notification.getKind() == Notification.Kind.OnError) {
                    String errorMessageJson = new Gson().toJson(new ErrorMessage(remoteCallResult.notification.getThrowable().getMessage()));
                    webview.loadUrl("javascript:var callback = window['android_" + remoteCallResult.correlationId + "_error']; if (callback) callback(" + errorMessageJson + ")");
                }
            }
        })
        .subscribeOn(responseScheduler);
    }
}
