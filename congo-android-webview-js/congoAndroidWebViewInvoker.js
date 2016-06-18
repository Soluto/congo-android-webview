var Observable = require('rx').Observable;

module.exports = function(javascriptInterfaceName) {
    return {
        invoke: function(remoteCall) {
            return Observable.create(function (observer)  {
                window["android_" + remoteCall.correlationId + "_next"] = function(result) {
                    observer.onNext(result);
                };
                window["android_" + remoteCall.correlationId + "_complete"] = function() {
                    observer.onCompleted();
                    delete window["android_" + remoteCall.correlationId + "_complete"];
                    delete window["android_" + remoteCall.correlationId + "_error"];
                };
                window["android_" + remoteCall.correlationId + "_error"] = function(exception) {
                    var error = new Error();
                    error.nativeException = exception;
                    observer.onError(error);
                    delete window["android_" + remoteCall.correlationId + "_complete"];
                    delete window["android_" + remoteCall.correlationId + "_error"];
                };
                console.log("remote call invocation: " + JSON.stringify(remoteCall));
                window[javascriptInterfaceName].send(JSON.stringify(remoteCall));

                return function() {
                    var cancelRemoteCall = {};
                    for (var property in remoteCall) {
                        cancelRemoteCall[property] = remoteCall[property];
                    }
                    cancelRemoteCall.isCancelled = true;
                    window[javascriptInterfaceName].send(JSON.stringify(remoteCall));
                }
            });
        }
    }
}
