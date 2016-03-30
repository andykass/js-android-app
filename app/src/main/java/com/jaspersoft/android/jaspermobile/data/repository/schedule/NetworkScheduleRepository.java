package com.jaspersoft.android.jaspermobile.data.repository.schedule;

import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.sdk.service.data.schedule.JobData;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;
import com.jaspersoft.android.sdk.service.report.schedule.ReportScheduleService;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public class NetworkScheduleRepository implements ScheduleRepository {
    private final JasperRestClient mRestClient;

    @Inject
    public NetworkScheduleRepository(JasperRestClient restClient) {
        mRestClient = restClient;
    }

    @Override
    public JobForm readForm(int id) throws Exception {
        ReportScheduleService service = mRestClient.syncScheduleService();
        return service.readJob(id);
    }

    @Override
    public JobData createForm(JobForm form) throws Exception {
        ReportScheduleService service = mRestClient.syncScheduleService();
        return service.createJob(form);
    }

    @Override
    public JobForm updateForm(int jobId, JobForm form) throws Exception {
        ReportScheduleService service = mRestClient.syncScheduleService();
        service.updateJob(jobId, form);
        return form;
    }
}
