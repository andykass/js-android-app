package com.jaspersoft.android.jaspermobile.ui.view.entity;

import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobFormatOutputs {
    private final List<JobFormViewEntity.OutputFormat> mFormats;
    private final String[] mLabels;
    private JobFormatOutputMapper mMapper;

    @Inject
    public JobFormatOutputs(JobFormatOutputMapper mapper) {
        mMapper = mapper;
        mFormats = generateFormats();
        mLabels = generateAllLabels();
    }

    public boolean[] getSelected(List<JobFormViewEntity.OutputFormat> selectedFormats) {
        List<JobFormViewEntity.OutputFormat> supportedFormats = mFormats;
        boolean[] selected = new boolean[supportedFormats.size()];
        for (JobFormViewEntity.OutputFormat selectedFormat : selectedFormats) {
            int index = supportedFormats.indexOf(selectedFormat);
            selected[index] = true;
        }
        return selected;
    }

    public String[] getLabels() {
        return mLabels;
    }

    private String[] generateAllLabels() {
        List<String> labels = new ArrayList<>(mFormats.size());
        for (JobFormViewEntity.OutputFormat format : mFormats) {
            labels.add(format.getLabel());
        }

        String[] result = new String[labels.size()];
        labels.toArray(result);
        return result;
    }

    private List<JobFormViewEntity.OutputFormat> generateFormats() {
        JobOutputFormat[] values = JobOutputFormat.values();
        List<JobFormViewEntity.OutputFormat> formats = new ArrayList<>(values.length);

        for (JobOutputFormat value : values) {
            JobFormViewEntity.OutputFormat format = new JobFormViewEntity.OutputFormat(
                    value.name(), mMapper.formatToString(value));
            formats.add(format);
        }

        return formats;
    }

    public int size() {
        return mFormats.size();
    }

    public JobFormViewEntity.OutputFormat get(int which) {
        return mFormats.get(which);
    }
}
