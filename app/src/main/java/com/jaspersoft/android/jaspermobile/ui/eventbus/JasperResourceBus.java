package com.jaspersoft.android.jaspermobile.ui.eventbus;

import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@PerActivity
public class JasperResourceBus {

    private List<EventListener> mListenerList;

    @Inject
    public JasperResourceBus() {
        mListenerList = new ArrayList<>();
    }

    public void subscribe(EventListener eventListener) {
        mListenerList.add(eventListener);
    }

    public void sendSelectEvent(JasperResource resource){
        for (EventListener eventListener : mListenerList) {
            eventListener.onSelect(resource);
        }
    }

    public interface EventListener {
        void onSelect(JasperResource resource);
    }
}
