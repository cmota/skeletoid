package com.mindera.skeletoid.rxbindings.searchview.support;

import android.support.v7.widget.SearchView;
import android.view.View;

import com.jakewharton.rxbinding2.InitialValueObservable;

import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

public class SearchViewFocusChangeObservable extends InitialValueObservable<Boolean> {

    private final SearchView view;

    public SearchViewFocusChangeObservable(SearchView view) {
        this.view = view;
    }

    @Override
    protected void subscribeListener(Observer<? super Boolean> observer) {
        Listener listener = new Listener(view, observer);
        observer.onSubscribe(listener);
        view.setOnQueryTextFocusChangeListener(listener);
    }

    @Override
    protected Boolean getInitialValue() {
        return view.hasFocus();
    }

    static final class Listener extends MainThreadDisposable
            implements SearchView.OnFocusChangeListener {

        private final SearchView view;
        private final Observer<? super Boolean> observer;

        Listener(SearchView view, Observer<? super Boolean> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!isDisposed()) {
                observer.onNext(hasFocus);
            }
        }

        @Override
        protected void onDispose() {
            view.setOnFocusChangeListener(null);
        }
    }
}
