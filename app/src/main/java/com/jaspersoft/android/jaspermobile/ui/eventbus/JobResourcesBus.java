package com.jaspersoft.android.jaspermobile.ui.eventbus;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@PerActivity
public class JobResourcesBus {

    private List<EventListener> mListenerList;

    @Inject
    public JobResourcesBus() {
        mListenerList = new ArrayList<>();
    }

    public void subscribe(EventListener eventListener) {
        mListenerList.add(eventListener);
    }

    public void sendSelectEvent(int jobId){
        for (EventListener eventListener : mListenerList) {
            eventListener.onSelect(jobId);
        }
    }

    public void sendDeleteRequestEvent(int jobId){
        for (EventListener eventListener : mListenerList) {
            eventListener.onDeleteRequest(jobId);
        }
    }

    public interface EventListener {
        void onSelect(int id);
        void onDeleteRequest(int id);
    }
}
