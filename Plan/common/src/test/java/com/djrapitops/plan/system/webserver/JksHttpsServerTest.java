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
package com.djrapitops.plan.system.webserver;

import com.djrapitops.plan.data.WebUser;
import com.djrapitops.plan.system.PlanSystem;
import com.djrapitops.plan.system.settings.config.PlanConfig;
import com.djrapitops.plan.system.settings.config.paths.WebserverSettings;
import com.djrapitops.plan.system.storage.database.transactions.commands.RegisterWebUserTransaction;
import com.djrapitops.plan.utilities.PassEncryptUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import utilities.RandomData;
import utilities.TestResources;
import utilities.mocks.PluginMockComponent;

import java.io.File;
import java.nio.file.Path;

@RunWith(JUnitPlatform.class)
class JksHttpsServerTest implements HttpsServerTest {

    private static final int TEST_PORT_NUMBER = RandomData.randomInt(9005, 9500);

    private static PlanSystem system;

    @BeforeAll
    static void setUpClass(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("PlanCert.jks").toFile();
        TestResources.copyResourceIntoFile(file, "/PlanCert.jks");
        String absolutePath = file.getAbsolutePath();

        PluginMockComponent component = new PluginMockComponent(tempDir);
        system = component.getPlanSystem();

        PlanConfig config = system.getConfigSystem().getConfig();

        config.set(WebserverSettings.CERTIFICATE_PATH, absolutePath);
        config.set(WebserverSettings.CERTIFICATE_KEYPASS, "MnD3bU5HpmPXag0e");
        config.set(WebserverSettings.CERTIFICATE_STOREPASS, "wDwwf663NLTm73gL");
        config.set(WebserverSettings.CERTIFICATE_ALIAS, "DefaultPlanCert");

        config.set(WebserverSettings.PORT, TEST_PORT_NUMBER);

        system.enable();

        WebUser webUser = new WebUser("test", PassEncryptUtil.createHash("testPass"), 0);
        system.getDatabaseSystem().getDatabase().executeTransaction(new RegisterWebUserTransaction(webUser));
    }

    @AfterAll
    static void tearDownClass() {
        if (system != null) {
            system.disable();
        }
    }

    @Override
    public WebServer getWebServer() {
        return system.getWebServerSystem().getWebServer();
    }

    @Override
    public int testPortNumber() {
        return TEST_PORT_NUMBER;
    }
}