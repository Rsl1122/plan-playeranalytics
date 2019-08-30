/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.system;

import com.djrapitops.plan.api.PlanAPI;
import com.djrapitops.plan.capability.CapabilityServiceImplementation;
import com.djrapitops.plan.exceptions.EnableException;
import com.djrapitops.plan.extension.ExtensionService;
import com.djrapitops.plan.extension.ExtensionServiceImplementation;
import com.djrapitops.plan.query.QueryServiceImplementation;
import com.djrapitops.plan.system.delivery.DeliveryUtilities;
import com.djrapitops.plan.system.delivery.export.ExportSystem;
import com.djrapitops.plan.system.delivery.webserver.WebServer;
import com.djrapitops.plan.system.delivery.webserver.WebServerSystem;
import com.djrapitops.plan.system.gathering.cache.CacheSystem;
import com.djrapitops.plan.system.gathering.importing.ImportSystem;
import com.djrapitops.plan.system.gathering.listeners.ListenerSystem;
import com.djrapitops.plan.system.identification.Server;
import com.djrapitops.plan.system.identification.ServerInfo;
import com.djrapitops.plan.system.processing.Processing;
import com.djrapitops.plan.system.settings.ConfigSystem;
import com.djrapitops.plan.system.settings.locale.LocaleSystem;
import com.djrapitops.plan.system.storage.database.DBSystem;
import com.djrapitops.plan.system.storage.database.queries.objects.ServerQueries;
import com.djrapitops.plan.system.storage.file.PlanFiles;
import com.djrapitops.plan.system.tasks.TaskSystem;
import com.djrapitops.plan.system.version.VersionCheckSystem;
import com.djrapitops.plugin.logging.L;
import com.djrapitops.plugin.logging.error.ErrorHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * PlanSystem contains everything Plan needs to run.
 * <p>
 * This is an abstraction layer on top of Plugin instances so that tests can be run with less mocks.
 *
 * @author Rsl1122
 */
@Singleton
public class PlanSystem implements SubSystem {

    private boolean enabled = false;

    private final PlanFiles files;
    private final ConfigSystem configSystem;
    private final VersionCheckSystem versionCheckSystem;
    private final LocaleSystem localeSystem;
    private final DBSystem databaseSystem;
    private final CacheSystem cacheSystem;
    private final ListenerSystem listenerSystem;
    private final TaskSystem taskSystem;
    private final ServerInfo serverInfo;
    private final WebServerSystem webServerSystem;

    private final Processing processing;

    private final ImportSystem importSystem;
    private final ExportSystem exportSystem;
    private final DeliveryUtilities deliveryUtilities;
    private final ExtensionServiceImplementation extensionService;
    private final QueryServiceImplementation queryService;
    private final PlanAPI planAPI;
    private final ErrorHandler errorHandler;

    @Inject
    public PlanSystem(
            PlanFiles files,
            ConfigSystem configSystem,
            VersionCheckSystem versionCheckSystem,
            LocaleSystem localeSystem,
            DBSystem databaseSystem,
            CacheSystem cacheSystem,
            ListenerSystem listenerSystem,
            TaskSystem taskSystem,
            ServerInfo serverInfo,
            WebServerSystem webServerSystem,
            Processing processing,
            ImportSystem importSystem,
            ExportSystem exportSystem,
            DeliveryUtilities deliveryUtilities,
            ExtensionServiceImplementation extensionService,
            QueryServiceImplementation queryService,
            PlanAPI planAPI,
            ErrorHandler errorHandler
    ) {
        this.files = files;
        this.configSystem = configSystem;
        this.versionCheckSystem = versionCheckSystem;
        this.localeSystem = localeSystem;
        this.databaseSystem = databaseSystem;
        this.cacheSystem = cacheSystem;
        this.listenerSystem = listenerSystem;
        this.taskSystem = taskSystem;
        this.serverInfo = serverInfo;
        this.webServerSystem = webServerSystem;
        this.processing = processing;
        this.importSystem = importSystem;
        this.exportSystem = exportSystem;
        this.deliveryUtilities = deliveryUtilities;
        this.extensionService = extensionService;
        this.queryService = queryService;
        this.planAPI = planAPI;
        this.errorHandler = errorHandler;
    }

    public static String getMainAddress(WebServer webServer, DBSystem dbSystem) {
        return dbSystem.getDatabase().query(ServerQueries.fetchProxyServerInformation())
                .map(Server::getWebAddress)
                .orElse(webServer.getAccessAddress());
    }

    public String getMainAddress() {
        return PlanSystem.getMainAddress(webServerSystem.getWebServer(), databaseSystem);
    }

    @Override
    public void enable() throws EnableException {
        CapabilityServiceImplementation.initialize();

        enableSystems(
                files,
                configSystem,
                localeSystem,
                versionCheckSystem,
                databaseSystem,
                webServerSystem,
                processing,
                serverInfo,
                importSystem,
                exportSystem,
                cacheSystem,
                listenerSystem,
                taskSystem
        );
        queryService.register();
        extensionService.register();
        enabled = true;
    }

    private void enableSystems(SubSystem... systems) throws EnableException {
        for (SubSystem system : systems) {
            system.enable();
        }
    }

    @Override
    public void disable() {
        enabled = false;
        disableSystems(
                taskSystem,
                cacheSystem,
                listenerSystem,
                importSystem,
                exportSystem,
                processing,
                databaseSystem,
                webServerSystem,
                serverInfo,
                localeSystem,
                configSystem,
                files,
                versionCheckSystem
        );
    }

    private void disableSystems(SubSystem... systems) {
        for (SubSystem system : systems) {
            try {
                if (system != null) {
                    system.disable();
                }
            } catch (Exception e) {
                errorHandler.log(L.WARN, this.getClass(), e);
            }
        }
    }

    // Accessor methods.

    public VersionCheckSystem getVersionCheckSystem() {
        return versionCheckSystem;
    }

    public ConfigSystem getConfigSystem() {
        return configSystem;
    }

    public PlanFiles getPlanFiles() {
        return files;
    }

    public DBSystem getDatabaseSystem() {
        return databaseSystem;
    }

    public ListenerSystem getListenerSystem() {
        return listenerSystem;
    }

    public TaskSystem getTaskSystem() {
        return taskSystem;
    }

    public WebServerSystem getWebServerSystem() {
        return webServerSystem;
    }

    public ImportSystem getImportSystem() {
        return importSystem;
    }

    public ExportSystem getExportSystem() {
        return exportSystem;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public CacheSystem getCacheSystem() {
        return cacheSystem;
    }

    public PlanAPI getPlanAPI() {
        return planAPI;
    }

    public Processing getProcessing() {
        return processing;
    }

    public LocaleSystem getLocaleSystem() {
        return localeSystem;
    }

    public DeliveryUtilities getDeliveryUtilities() {
        return deliveryUtilities;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ExtensionService getExtensionService() {
        return extensionService;
    }
}