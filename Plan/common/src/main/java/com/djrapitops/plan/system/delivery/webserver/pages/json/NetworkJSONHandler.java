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
package com.djrapitops.plan.system.delivery.webserver.pages.json;

import com.djrapitops.plan.exceptions.WebUserAuthException;
import com.djrapitops.plan.system.delivery.rendering.json.JSONFactory;
import com.djrapitops.plan.system.delivery.rendering.json.network.NetworkOverviewJSONParser;
import com.djrapitops.plan.system.delivery.rendering.json.network.NetworkPlayerBaseOverviewJSONParser;
import com.djrapitops.plan.system.delivery.rendering.json.network.NetworkSessionsOverviewJSONParser;
import com.djrapitops.plan.system.delivery.rendering.json.network.NetworkTabJSONParser;
import com.djrapitops.plan.system.delivery.webserver.RequestTarget;
import com.djrapitops.plan.system.delivery.webserver.auth.Authentication;
import com.djrapitops.plan.system.delivery.webserver.pages.TreePageHandler;
import com.djrapitops.plan.system.delivery.webserver.response.ResponseFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Root handler for different JSON end points.
 *
 * @author Rsl1122
 */
@Singleton
public class NetworkJSONHandler extends TreePageHandler {

    @Inject
    public NetworkJSONHandler(
            ResponseFactory responseFactory,
            JSONFactory jsonFactory,
            NetworkOverviewJSONParser networkOverviewJSONParser,
            NetworkPlayerBaseOverviewJSONParser playerBaseOverviewJSONParser,
            NetworkSessionsOverviewJSONParser sessionsOverviewJSONParser
    ) {
        super(responseFactory);

        registerPage("overview", networkOverviewJSONParser);
        registerPage("playerbaseOverview", playerBaseOverviewJSONParser);
        registerPage("sessionsOverview", sessionsOverviewJSONParser);
        registerPage("servers", jsonFactory::serversAsJSONMaps);
        registerPage("pingTable", jsonFactory::pingPerGeolocation);
    }

    private <T> void registerPage(String identifier, NetworkTabJSONParser<T> tabJSONParser) {
        registerPage(identifier, new NetworkTabJSONHandler<>(tabJSONParser));
    }

    @Override
    public boolean isAuthorized(Authentication auth, RequestTarget target) throws WebUserAuthException {
        return true;
    }
}