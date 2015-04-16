(function() {
  define('js.mobile.android.report.callback', ['require'],function(require) {
    var ReportCallback;
    return ReportCallback = (function() {
      function ReportCallback() {}

      ReportCallback.prototype.onScriptLoaded = function() {
        Android.onScriptLoaded();
      };

      ReportCallback.prototype.onLoadStart = function() {
        Android.onLoadStart();
      };

      ReportCallback.prototype.onLoadDone = function(parameters) {
        Android.onLoadDone(parameters);
      };

      ReportCallback.prototype.onLoadError = function(error) {
        Android.onLoadError(error);
      };

      ReportCallback.prototype.onTotalPagesLoaded = function(pages) {
        Android.onTotalPagesLoaded(pages);
      };

      ReportCallback.prototype.onPageChange = function(page) {
        Android.onPageChange(page);
      };

      ReportCallback.prototype.onReferenceClick = function(location) {
        Android.onReferenceClick(location);
      };

      ReportCallback.prototype.onReportExecutionClick = function(reportUri, params) {
        Android.onReportExecutionClick(reportUri, params);
      };

      ReportCallback.prototype.onExportGetResourcePath = function(link) {
        return Android.onExportGetResourcePath(link);
      };

      return ReportCallback;

    })();
  });

}).call(this);

(function() {
  define('js.mobile.logger', [],function() {
    var Logger;
    return Logger = (function() {
      function Logger() {}

      Logger.prototype.log = function(message) {};

      return Logger;

    })();
  });

}).call(this);

(function() {
  var extend = function(child, parent) { for (var key in parent) { if (hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
    hasProp = {}.hasOwnProperty;

  define('js.mobile.android.logger', ['js.mobile.logger'], function(Logger) {
    var AndroidLogger;
    return AndroidLogger = (function(superClass) {
      extend(AndroidLogger, superClass);

      function AndroidLogger() {
        return AndroidLogger.__super__.constructor.apply(this, arguments);
      }

      AndroidLogger.prototype.log = function(message) {
        return console.log(message);
      };

      return AndroidLogger;

    })(Logger);
  });

}).call(this);

(function() {
  define('js.mobile.context', [],function() {
    var Context;
    return Context = (function() {
      function Context(options) {
        this.logger = options.logger, this.callback = options.callback;
      }

      Context.prototype.setWindow = function(window) {
        this.window = window;
      };

      return Context;

    })();
  });

}).call(this);

(function() {
  define('js.mobile.session', [],function() {
    var Session;
    return Session = (function() {
      function Session(options) {
        this.username = options.username, this.password = options.password, this.organization = options.organization;
      }

      Session.prototype.authOptions = function() {
        return {
          auth: {
            name: this.username,
            password: this.password,
            organization: this.organization
          }
        };
      };

      return Session;

    })();
  });

}).call(this);

(function() {
  var bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };

  define('js.mobile.report.controller', [],function() {
    var ReportController;
    return ReportController = (function() {
      function ReportController(options) {
        this._exportResource = bind(this._exportResource, this);
        this._notifyPageChange = bind(this._notifyPageChange, this);
        this._openRemoteLink = bind(this._openRemoteLink, this);
        this._navigateToPage = bind(this._navigateToPage, this);
        this._navigateToAnchor = bind(this._navigateToAnchor, this);
        this._startReportExecution = bind(this._startReportExecution, this);
        this._processLinkClicks = bind(this._processLinkClicks, this);
        this._processErrors = bind(this._processErrors, this);
        this._processSuccess = bind(this._processSuccess, this);
        this._processChangeTotalPages = bind(this._processChangeTotalPages, this);
        this._executeReport = bind(this._executeReport, this);
        this.context = options.context, this.session = options.session, this.uri = options.uri, this.params = options.params, this.pages = options.pages;
        this.callback = this.context.callback;
        this.logger = this.context.logger;
        this.logger.log(this.uri);
        this.params || (this.params = {});
        this.totalPages = 0;
        this.pages || (this.pages = '1');
      }

      ReportController.prototype.selectPage = function(page) {
        if (this.loader != null) {
          return this.loader.pages(page).run().done(this._processSuccess).fail(this._processErrors);
        }
      };

      ReportController.prototype.runReport = function() {
        this.callback.onLoadStart();
        return visualize(this.session.authOptions(), this._executeReport);
      };

      ReportController.prototype.exportReport = function(format) {
        return this.loader["export"]({
          outputFormat: format
        }).done(this._exportResource);
      };

      ReportController.prototype.destroyReport = function() {
        console.log("destroy");
        return this.loader.destroy();
      };

      ReportController.prototype._executeReport = function(visualize) {
        this.loader = visualize.report({
          resource: this.uri,
          params: this.params,
          pages: this.pages,
          container: "#container",
          scale: "width",
          linkOptions: {
            events: {
              click: this._processLinkClicks
            }
          },
          error: this._processErrors,
          events: {
            changeTotalPages: this._processChangeTotalPages
          },
          success: this._processSuccess
        });
        return window.loader = this.loader;
      };

      ReportController.prototype._processChangeTotalPages = function(totalPages) {
        this.totalPages = totalPages;
        return this.callback.onTotalPagesLoaded(this.totalPages);
      };

      ReportController.prototype._processSuccess = function(parameters) {
        return this.callback.onLoadDone(parameters);
      };

      ReportController.prototype._processErrors = function(error) {
        this.logger.log(error);
        return this.callback.onLoadError(error);
      };

      ReportController.prototype._processLinkClicks = function(event, link) {
        var type;
        type = link.type;
        switch (type) {
          case "ReportExecution":
            return this._startReportExecution(link);
          case "LocalAnchor":
            return this._navigateToAnchor(link);
          case "LocalPage":
            return this._navigateToPage(link);
          case "Reference":
            return this._openRemoteLink(link);
        }
      };

      ReportController.prototype._startReportExecution = function(link) {
        var params, paramsAsString, reportUri;
        params = link.parameters;
        reportUri = params._report;
        paramsAsString = JSON.stringify(params, null, 2);
        return this.callback.onReportExecutionClick(reportUri, paramsAsString);
      };

      ReportController.prototype._navigateToAnchor = function(link) {
        return window.location.hash = link.href;
      };

      ReportController.prototype._navigateToPage = function(link) {
        var href, matches, numberPattern, pageNumber;
        href = link.href;
        numberPattern = /\d+/g;
        matches = href.match(numberPattern);
        if (matches != null) {
          pageNumber = matches.join("");
          return this._loadPage(pageNumber);
        }
      };

      ReportController.prototype._openRemoteLink = function(link) {
        var href;
        href = link.href;
        return this.callback.onReferenceClick(href);
      };

      ReportController.prototype._loadPage = function(page) {
        return this.loader.pages(page).run().fail(this._processErrors).done(this._notifyPageChange);
      };

      ReportController.prototype._notifyPageChange = function() {
        return this.callback.onPageChange(this.loader.pages());
      };

      ReportController.prototype._exportReport = function(format) {
        console.log("export with format: " + format);
        return this.loader["export"]({
          outputFormat: format
        }).done(this._exportResource);
      };

      ReportController.prototype._exportResource = function(link) {
        return this.callback.onExportGetResourcePath(link.href);
      };

      return ReportController;

    })();
  });

}).call(this);

(function() {
  define('js.mobile.report', ['require','js.mobile.session','js.mobile.report.controller'],function(require) {
    var MobileReport, ReportController, Session, root;
    Session = require('js.mobile.session');
    ReportController = require('js.mobile.report.controller');
    MobileReport = (function() {
      MobileReport._instance = null;

      MobileReport.getInstance = function(context) {
        return this._instance || (this._instance = new MobileReport(context));
      };

      MobileReport.authorize = function(options) {
        return this._instance.authorize(options);
      };

      MobileReport.destroy = function() {
        return this._instance.destroyReport();
      };

      MobileReport.run = function(options) {
        return this._instance.run(options);
      };

      MobileReport.selectPage = function(page) {
        return this._instance.selectPage(page);
      };

      MobileReport.exportReport = function(format) {
        return this._instance.exportReport(format);
      };

      function MobileReport(context1) {
        this.context = context1;
        this.context.callback.onScriptLoaded();
      }

      MobileReport.prototype.authorize = function(options) {
        return this.session = new Session(options);
      };

      MobileReport.prototype.selectPage = function(page) {
        if (this.reportController) {
          return this.reportController.selectPage(page);
        }
      };

      MobileReport.prototype.run = function(options) {
        console.log("run report with options" + options);
        options.session = this.session;
        options.context = this.context;
        this.reportController = new ReportController(options);
        return this.reportController.runReport();
      };

      MobileReport.prototype.exportReport = function(format) {
        return this.reportController.exportReport(format);
      };

      MobileReport.prototype.destroyReport = function() {
        return this.reportController.destroyReport();
      };

      return MobileReport;

    })();
    root = typeof window !== "undefined" && window !== null ? window : exports;
    return root.MobileReport = MobileReport;
  });

}).call(this);

(function() {
  define('js.mobile.android.report.client', ['require','js.mobile.android.report.callback','js.mobile.android.logger','js.mobile.context','js.mobile.report'],function(require) {
    var AndroidLogger, Context, MobileReport, ReportCallback, ReportClient;
    ReportCallback = require('js.mobile.android.report.callback');
    AndroidLogger = require('js.mobile.android.logger');
    Context = require('js.mobile.context');
    MobileReport = require('js.mobile.report');
    return ReportClient = (function() {
      function ReportClient() {}

      ReportClient.prototype.run = function() {
        var context;
        context = new Context({
          callback: new ReportCallback(),
          logger: new AndroidLogger()
        });
        return MobileReport.getInstance(context);
      };

      return ReportClient;

    })();
  });

}).call(this);

(function() {
  require(['js.mobile.android.report.client'], function(ReportClient) {
    return new ReportClient().run();
  });

}).call(this);

define("android/report/main.js", function(){});

