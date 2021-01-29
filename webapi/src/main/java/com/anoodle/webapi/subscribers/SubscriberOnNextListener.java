package com.anoodle.webapi.subscribers;

public interface SubscriberOnNextListener<T> {
    void onNext(T t);
}
