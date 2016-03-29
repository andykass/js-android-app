package com.jaspersoft.android.jaspermobile.data;

import com.jaspersoft.android.jaspermobile.data.cache.profile.CredentialsCache;
import com.jaspersoft.android.jaspermobile.data.cache.profile.JasperServerCache;
import com.jaspersoft.android.jaspermobile.domain.AppCredentials;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.sdk.network.AuthorizedClient;
import com.jaspersoft.android.sdk.network.Credentials;
import com.jaspersoft.android.sdk.network.Server;
import com.jaspersoft.android.sdk.network.SpringCredentials;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.exception.StatusCodes;
import com.jaspersoft.android.sdk.service.filter.FiltersService;
import com.jaspersoft.android.sdk.service.report.ReportService;
import com.jaspersoft.android.sdk.service.report.schedule.ReportScheduleService;
import com.jaspersoft.android.sdk.service.repository.RepositoryService;
import com.jaspersoft.android.sdk.service.rx.filter.RxFiltersService;
import com.jaspersoft.android.sdk.service.rx.report.RxReportService;
import com.jaspersoft.android.sdk.service.rx.report.schedule.RxReportScheduleService;
import com.jaspersoft.android.sdk.service.rx.repository.RxRepositoryService;

import java.net.CookieHandler;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class RealJasperRestClient implements JasperRestClient {
    private final Server.Builder mServerBuilder;
    private final CookieHandler mCookieHandler;
    private final Profile mProfile;
    private final CredentialsCache mCredentialsCache;
    private final JasperServerCache mServerCache;

    public RealJasperRestClient(
            Server.Builder serverBuilder,
            CookieHandler cookieHandler,
            Profile profile,
            CredentialsCache credentialsCache,
            JasperServerCache serverCache
    ) {
        mServerBuilder = serverBuilder;
        mCookieHandler = cookieHandler;
        mProfile = profile;
        mCredentialsCache = credentialsCache;
        mServerCache = serverCache;
    }

    @Override
    public ReportService syncReportService() {
        AuthorizedClient client = createAuthorizedClient().toBlocking().first();
        return ReportService.newService(client);
    }

    @Override
    public FiltersService syncFilterService() {
        AuthorizedClient client = createAuthorizedClient().toBlocking().first();
        return FiltersService.newService(client);
    }

    @Override
    public RepositoryService syncRepositoryService() {
        AuthorizedClient client = createAuthorizedClient().toBlocking().first();
        return RepositoryService.newService(client);
    }

    @Override
    public ReportScheduleService syncScheduleService() {
        AuthorizedClient client = createAuthorizedClient().toBlocking().first();
        return ReportScheduleService.newService(client);
    }

    @Override
    public Observable<RxReportService> reportService() {
        return createAuthorizedClient().map(new Func1<AuthorizedClient, RxReportService>() {
            @Override
            public RxReportService call(AuthorizedClient authorizedClient) {
                return RxReportService.newService(authorizedClient);
            }
        });
    }

    @Override
    public Observable<RxRepositoryService> repositoryService() {
        return createAuthorizedClient().map(new Func1<AuthorizedClient, RxRepositoryService>() {
            @Override
            public RxRepositoryService call(AuthorizedClient authorizedClient) {
                return RxRepositoryService.newService(authorizedClient);
            }
        });
    }

    @Override
    public Observable<RxFiltersService> filtersService() {
        return createAuthorizedClient().map(new Func1<AuthorizedClient, RxFiltersService>() {
            @Override
            public RxFiltersService call(AuthorizedClient authorizedClient) {
                return RxFiltersService.newService(authorizedClient);
            }
        });
    }

    @Override
    public Observable<RxReportScheduleService> scheduleService() {
        return createAuthorizedClient().map(new Func1<AuthorizedClient, RxReportScheduleService>() {
            @Override
            public RxReportScheduleService call(AuthorizedClient authorizedClient) {
                return RxReportScheduleService.newService(authorizedClient);
            }
        });
    }

    private Observable<AuthorizedClient> createAuthorizedClient() {
        return Observable.defer(new Func0<Observable<AuthorizedClient>>() {
            @Override
            public Observable<AuthorizedClient> call() {
                AppCredentials appCredentials = mCredentialsCache.get(mProfile);
                boolean passwordMissing = AppCredentials.NO_PASSWORD.equals(appCredentials.getPassword());
                if (passwordMissing) {
                    ServiceException serviceException =
                            new ServiceException("User is not authorized", null, StatusCodes.AUTHORIZATION_ERROR);
                    return Observable.error(serviceException);
                }

                Credentials credentials = SpringCredentials.builder()
                        .withUsername(appCredentials.getUsername())
                        .withPassword(appCredentials.getPassword())
                        .withOrganization(appCredentials.getOrganization())
                        .build();

                AuthorizedClient authorizedClient = provideServer().newClient(credentials)
                        .withCookieHandler(mCookieHandler)
                        .create();
                return Observable.just(authorizedClient);
            }
        });
    }

    private Server provideServer() {
        JasperServer server = providesJasperServer();
        return mServerBuilder
                .withBaseUrl(server.getBaseUrl())
                .build();
    }

    private JasperServer providesJasperServer() {
        return mServerCache.get(mProfile);
    }
}
