package com.urbanxd.mobilalk_kotprog.data.repository.callback;

@FunctionalInterface
public interface Callback<T> {
    void onComplete(CallbackResult<T> result);
}
