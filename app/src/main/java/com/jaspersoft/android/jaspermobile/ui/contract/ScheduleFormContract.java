package com.jaspersoft.android.jaspermobile.ui.contract;

import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public interface ScheduleFormContract {
    interface View {
        void showForm(JobFormViewBundle formBundle);
        void showFormLoadingMessage();
        void hideFormLoadingMessage();
        void showSubmitMessage();
        void hideSubmitMessage();
        void showSubmitSuccess();
        JobFormViewBundle takeForm();
    }

    interface EventListener {
        void onViewReady();
        void onSubmitClick(JobFormViewBundle form);
    }

    interface Model {
        void load();
        void submit(JobFormViewBundle form);

        void bind(Callback callbacks);
        void unbind();

        interface Callback {
            void onFormLoadSuccess(JobFormViewBundle form);
            void onFormLoadError(Throwable error);

            void onFormSubmitSuccess();
            void onFormSubmitError(Throwable error);
        }
    }
}
