package com.jaspersoft.android.jaspermobile.activities.save;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.data.entity.ExportBundle;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ComponentProviderDelegate;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import java.util.Date;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */

@EService
public class SaveReportService extends Service implements ReportDownloadManager.ReportDownloadCallback {

    public final static String ACTION_SAVE_REPORT = "saveReport";
    public final static String ACTION_CANCEL_SAVING = "cancelSaving";

    public final static String EXPORT_BUNDLE_EXTRA = "exportBundle";
    public final static String ITEM_URI_EXTRA = "itemUri";

    private static final int LOADING_NOTIFICATION_ID = 434;

    @Inject
    protected Analytics analytics;

    @SystemService
    protected NotificationManager mNotificationManager;
    private ReportDownloadManager mReportDownloadManager;

    public static void start(Context context, ExportBundle bundle) {
        Intent startIntent = SaveReportService_.intent(context).get();
        startIntent.setAction(ACTION_SAVE_REPORT);
        startIntent.putExtra(EXPORT_BUNDLE_EXTRA, bundle);
        context.startService(startIntent);
    }

    public static void cancel(Context context, Uri reportUri) {
        Intent cancelIntent = SaveReportService_.intent(context).get();
        cancelIntent.setAction(ACTION_CANCEL_SAVING);
        cancelIntent.putExtra(ITEM_URI_EXTRA, reportUri);
        context.startService(cancelIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ComponentProviderDelegate.INSTANCE
                .getProfileComponent(this)
                .inject(this);

        mReportDownloadManager = new ReportDownloadManager(this);
        mReportDownloadManager.setReportDownloadCallback(this);

        startForegroundNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Bundle extras = intent.getExtras();

        if (SaveReportService.ACTION_CANCEL_SAVING.equals(action)) {
            Uri reportUri = extras.getParcelable(SaveReportService.ITEM_URI_EXTRA);
            mReportDownloadManager.cancelDownloading(reportUri);
        } else {
            ExportBundle bundle = extras.getParcelable(SaveReportService.EXPORT_BUNDLE_EXTRA);
            mReportDownloadManager.downloadReport(bundle);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDownloadCountChange(int count) {
        if (count > 0) {
            updateDownloadingNotification(count);
        } else{
            stopSelf();
        }
    }

    @Override
    public void onDownloadComplete(String reportName) {
        notifySaveResult(reportName, android.R.drawable.stat_sys_download_done, getString(R.string.sr_t_report_saved));
        analytics.sendEvent(Analytics.EventCategory.RESOURCE.getValue(), Analytics.EventAction.SAVED.getValue(), Analytics.EventLabel.DONE.getValue());
    }

    @Override
    public void onDownloadFailed(String reportName) {
        notifySaveResult(reportName, android.R.drawable.ic_dialog_alert, getString(R.string.sdr_saving_error_msg));
        analytics.sendEvent(Analytics.EventCategory.RESOURCE.getValue(), Analytics.EventAction.SAVED.getValue(), Analytics.EventLabel.FAILED.getValue());
    }

    @Override
    public void onDownloadCanceled() {
        analytics.sendEvent(Analytics.EventCategory.RESOURCE.getValue(), Analytics.EventAction.SAVED.getValue(), Analytics.EventLabel.CANCELED.getValue());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelForegroundNotification();
    }

    private void startForegroundNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(getString(R.string.sdr_starting_downloading_msg));
        startForeground(LOADING_NOTIFICATION_ID, mBuilder.build());
    }

    private void cancelForegroundNotification() {
        mNotificationManager.cancel(LOADING_NOTIFICATION_ID);
    }

    private void updateDownloadingNotification(int downloadingCount) {
        String savingTitle = downloadingCount > 1 ? getString(R.string.sdr_saving_multiply_msg, downloadingCount) : getString(R.string.sdr_saving_msg);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(savingTitle)
                .setContentIntent(getSavedItemIntent());

        mNotificationManager.notify(LOADING_NOTIFICATION_ID, mBuilder.build());
    }

    private void notifySaveResult(String reportName, int iconId, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(iconId)
                .setContentTitle(reportName)
                .setContentText(message)
                .setContentIntent(getSavedItemIntent())
                .setAutoCancel(true);

        mNotificationManager.notify(createNotificationId(), mBuilder.build());
    }

    private int createNotificationId() {
        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last5Str = tmpStr.substring(tmpStr.length() - 6);
        return Integer.valueOf(last5Str);
    }

    private PendingIntent getSavedItemIntent() {
        Intent notificationIntent = NavigationActivity_.intent(this)
                .currentSelection(R.id.vg_saved_items)
                .get();

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
