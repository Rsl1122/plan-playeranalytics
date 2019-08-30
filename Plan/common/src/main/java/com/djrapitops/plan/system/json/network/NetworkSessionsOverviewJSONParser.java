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
package com.djrapitops.plan.system.json.network;

import com.djrapitops.plan.db.Database;
import com.djrapitops.plan.db.access.queries.objects.SessionQueries;
import com.djrapitops.plan.system.storage.database.DBSystem;
import com.djrapitops.plan.utilities.formatting.Formatter;
import com.djrapitops.plan.utilities.formatting.Formatters;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Parses JSON payload for /network-page Sessions tab.
 *
 * @author Rsl1122
 */
@Singleton
public class NetworkSessionsOverviewJSONParser implements NetworkTabJSONParser<Map<String, Object>> {

    private DBSystem dbSystem;

    private Formatter<Long> timeAmount;
    private Formatter<Double> percentage;

    @Inject
    public NetworkSessionsOverviewJSONParser(
            DBSystem dbSystem,
            Formatters formatters
    ) {
        this.dbSystem = dbSystem;

        timeAmount = formatters.timeAmount();
        percentage = formatters.percentage();
    }

    public Map<String, Object> createJSONAsMap() {
        return Collections.singletonMap("insights", createInsightsMap());
    }

    private Map<String, Object> createInsightsMap() {
        Database db = dbSystem.getDatabase();
        long now = System.currentTimeMillis();
        long monthAgo = now - TimeUnit.DAYS.toMillis(30L);

        Map<String, Object> insights = new HashMap<>();

        Long playtime = db.query(SessionQueries.playtime(monthAgo, now));
        Long afkTime = db.query(SessionQueries.afkTime(monthAgo, now));
        insights.put("total_playtime", timeAmount.apply(playtime));
        insights.put("afk_time", timeAmount.apply(afkTime));
        insights.put("afk_time_perc", playtime != 0 ? percentage.apply(1.0 * afkTime / playtime) : "-");

        return insights;
    }
}