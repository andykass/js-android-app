package com.jaspersoft.android.jaspermobile.ui.eventbus;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobResource;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

    public void sendSelectEvent(JobResource job){
        for (EventListener eventListener : mListenerList) {
            eventListener.onSelect(job);
        }
    }

    public void sendEditRequestEvent(int jobId){
        for (EventListener eventListener : mListenerList) {
            eventListener.onEditRequest(jobId);
        }
    }

    public void sendDeleteRequestEvent(int jobId){
        for (EventListener eventListener : mListenerList) {
            eventListener.onDeleteRequest(jobId);
        }
    }

    public interface EventListener {
        void onSelect(JobResource job);
        void onEditRequest(int id);
        void onDeleteRequest(int id);
    }
}
