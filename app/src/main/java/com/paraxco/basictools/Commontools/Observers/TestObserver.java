package com.paraxco.basictools.Commontools.Observers;

import com.paraxco.commontools.ObserverBase.ObserverHandlerBase;

import java.util.List;

/**
 * Created by Amin on 13/02/2018.
 */

public class TestObserver extends ObserverHandlerBase<TestObserver.ObserverTest, List<String>> {
    private static TestObserver instance;

    public static TestObserver getInstance() {
        if (instance == null)
            instance = new TestObserver();
        return instance;
    }

    @Override
    protected void informObserverInternal(ObserverTest observe, List<String> data) {
        observe.observeChanges(data);
    }

    public interface ObserverTest {
        void observeChanges(List<String> list);
    }
}
