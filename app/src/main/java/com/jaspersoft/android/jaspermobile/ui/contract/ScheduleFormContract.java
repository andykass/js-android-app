package com.jaspersoft.android.jaspermobile.ui.contract;

import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormViewEntity;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public interface ScheduleFormContract {
    interface View {
        void showForm(JobFormViewEntity form);
        void showFormLoadingMessage();
        void hideFormLoadingMessage();
        void showSubmitMessage();
        void hideSubmitMessage();
        void showSubmitSuccess();
        JobFormViewEntity takeForm();
    }

    interface EventListener {
        void onViewReady();
        void onSubmitClick(JobFormViewEntity form);
    }

    interface Model {
        void load();
        void submit(JobFormViewEntity form);

        void bind(Callback callbacks);
        void unbind();

        interface Callback {
            void onFormLoadSuccess(JobFormViewEntity form);
            void onFormLoadError(Throwable error);

            void onFormSubmitSuccess();
            void onFormSubmitError(Throwable error);
        }
    }
}
