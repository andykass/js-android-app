package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.domain.AppCredentials;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class SimpleVisualizeComponentTest {

    @Mock
    VisualizeEvents mVisualizeEvents;

    private SimpleVisualizeComponent mSimpleVisualizeComponent;
    private WebView webView;

    @Before
    public void setUp() throws Exception {
        webView = spy(new WebView(RuntimeEnvironment.application));
        mSimpleVisualizeComponent = new SimpleVisualizeComponent(webView, mVisualizeEvents);
    }

    @Test
    public void testRun() throws Exception {
        AppCredentials credentials = AppCredentials.builder()
                .setOrganization("org")
                .setPassword("1234")
                .setUsername("user")
                .create();
        VisualizeExecOptions options = new VisualizeExecOptions.Builder()
                .setUri("/my/uri").setParams("{}")
                .setAppCredentials(credentials)
                .setDiagonal(10)
                .build();
        mSimpleVisualizeComponent.run(options);
        verify(webView).loadUrl("javascript:MobileReport.configure({ \"auth\": {\"username\": \"user\",\"password\": \"1234\",\"organization\": \"org\"}, \"diagonal\": 10.0 }).run({\"uri\": \"/my/uri\",\"params\": {}})");
    }

    @Test
    public void testLoadPage() throws Exception {
        mSimpleVisualizeComponent.loadPage("1");
        verify(webView).loadUrl("javascript:MobileReport.selectPage(1)");
    }

    @Test
    public void testUpdate() throws Exception {
        mSimpleVisualizeComponent.update("{}");
        verify(webView).loadUrl("javascript:MobileReport.applyReportParams({})");
    }

    @Test
    public void testRefresh() throws Exception {
        mSimpleVisualizeComponent.refresh();
        verify(webView).loadUrl("javascript:MobileReport.refresh()");
    }
}