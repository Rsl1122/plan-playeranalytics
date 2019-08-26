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
package com.djrapitops.plan.extension.implementation.storage.queries;

import com.djrapitops.plan.db.SQLDB;
import com.djrapitops.plan.db.access.Query;
import com.djrapitops.plan.db.access.QueryStatement;
import com.djrapitops.plan.db.sql.tables.*;
import com.djrapitops.plan.extension.FormatType;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.implementation.results.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.djrapitops.plan.db.sql.parsing.Sql.*;

/**
 * Query Extension data of x most recent players on a server.
 * <p>
 * Returns Map: Player UUID - ExtensionTabData (container for provider based data)
 *
 * @author Rsl1122
 */
public class ExtensionServerPlayerDataTableQuery implements Query<Map<UUID, ExtensionTabData>> {

    private final UUID serverUUID;
    private final int xMostRecentPlayers;

    public ExtensionServerPlayerDataTableQuery(UUID serverUUID, int xMostRecentPlayers) {
        this.serverUUID = serverUUID;
        this.xMostRecentPlayers = xMostRecentPlayers;
    }

    @Override
    public Map<UUID, ExtensionTabData> executeQuery(SQLDB db) {
        return db.query(fetchPlayerData());
    }

    private Query<Map<UUID, ExtensionTabData>> fetchPlayerData() {
        String selectLimitedNumberOfPlayerUUIDsByLastSeenDate = SELECT +
                SessionsTable.TABLE_NAME + '.' + SessionsTable.USER_UUID +
                ",MAX(" + SessionsTable.SESSION_END + ") as last_seen" +
                FROM + SessionsTable.TABLE_NAME +
                GROUP_BY + SessionsTable.TABLE_NAME + '.' + SessionsTable.USER_UUID + ',' + SessionsTable.SESSION_END +
                ORDER_BY + SessionsTable.SESSION_END + " DESC LIMIT ?";

        String sql = SELECT +
                "v1." + ExtensionPlayerValueTable.USER_UUID + " as uuid," +
                "v1." + ExtensionPlayerValueTable.BOOLEAN_VALUE + " as boolean_value," +
                "v1." + ExtensionPlayerValueTable.DOUBLE_VALUE + " as double_value," +
                "v1." + ExtensionPlayerValueTable.PERCENTAGE_VALUE + " as percentage_value," +
                "v1." + ExtensionPlayerValueTable.LONG_VALUE + " as long_value," +
                "v1." + ExtensionPlayerValueTable.STRING_VALUE + " as string_value," +
                "p1." + ExtensionProviderTable.PROVIDER_NAME + " as provider_name," +
                "p1." + ExtensionProviderTable.TEXT + " as text," +
                "p1." + ExtensionProviderTable.FORMAT_TYPE + " as format_type," +
                "p1." + ExtensionProviderTable.IS_PLAYER_NAME + " as is_player_name," +
                "i1." + ExtensionIconTable.ICON_NAME + " as provider_icon_name," +
                "i1." + ExtensionIconTable.FAMILY + " as provider_icon_family" +
                FROM + ExtensionPlayerValueTable.TABLE_NAME + " v1" +
                INNER_JOIN + '(' + selectLimitedNumberOfPlayerUUIDsByLastSeenDate + ") as last_seen_q on last_seen_q.uuid=v1." + ExtensionPlayerValueTable.USER_UUID +
                INNER_JOIN + ExtensionProviderTable.TABLE_NAME + " p1 on p1." + ExtensionProviderTable.ID + "=v1." + ExtensionPlayerValueTable.PROVIDER_ID +
                INNER_JOIN + ExtensionPluginTable.TABLE_NAME + " e1 on e1." + ExtensionPluginTable.ID + "=p1." + ExtensionProviderTable.PLUGIN_ID +
                LEFT_JOIN + ExtensionIconTable.TABLE_NAME + " i1 on i1." + ExtensionIconTable.ID + "=p1." + ExtensionProviderTable.ICON_ID +
                WHERE + "e1." + ExtensionPluginTable.SERVER_UUID + "=?" +
                AND + "p1." + ExtensionProviderTable.SHOW_IN_PLAYERS_TABLE + "=?" +
                AND + "p1." + ExtensionProviderTable.IS_PLAYER_NAME + "=?";

        return new QueryStatement<Map<UUID, ExtensionTabData>>(sql, 1000) {
            @Override
            public void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, xMostRecentPlayers);       // Limit to x most recently seen players
                statement.setString(2, serverUUID.toString());
                statement.setBoolean(3, true);                  // Select only values that should be shown
                statement.setBoolean(4, false);                 // Don't select player_name String values
            }

            @Override
            public Map<UUID, ExtensionTabData> processResults(ResultSet set) throws SQLException {
                return extractDataByPlayer(set);
            }
        };
    }

    private Map<UUID, ExtensionTabData> extractDataByPlayer(ResultSet set) throws SQLException {
        Map<UUID, ExtensionTabData.Factory> dataByPlayer = new HashMap<>();

        while (set.next()) {
            UUID playerUUID = UUID.fromString(set.getString("uuid"));
            ExtensionTabData.Factory data = dataByPlayer.getOrDefault(playerUUID, new ExtensionTabData.Factory(null));

            ExtensionDescriptive extensionDescriptive = extractDescriptive(set);
            extractAndPutDataTo(data, extensionDescriptive, set);

            dataByPlayer.put(playerUUID, data);
        }
        return dataByPlayer.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().build()));
    }

    private void extractAndPutDataTo(ExtensionTabData.Factory extensionTab, ExtensionDescriptive descriptive, ResultSet set) throws SQLException {
        boolean booleanValue = set.getBoolean(ExtensionServerValueTable.BOOLEAN_VALUE);
        if (!set.wasNull()) {
            extensionTab.putBooleanData(new ExtensionBooleanData(descriptive, booleanValue));
            return;
        }

        double doubleValue = set.getDouble(ExtensionPlayerValueTable.DOUBLE_VALUE);
        if (!set.wasNull()) {
            extensionTab.putDoubleData(new ExtensionDoubleData(descriptive, doubleValue));
            return;
        }

        double percentageValue = set.getDouble(ExtensionServerValueTable.PERCENTAGE_VALUE);
        if (!set.wasNull()) {
            extensionTab.putPercentageData(new ExtensionDoubleData(descriptive, percentageValue));
            return;
        }

        long numberValue = set.getLong(ExtensionPlayerValueTable.LONG_VALUE);
        if (!set.wasNull()) {
            FormatType formatType = FormatType.getByName(set.getString(ExtensionProviderTable.FORMAT_TYPE)).orElse(FormatType.NONE);
            extensionTab.putNumberData(new ExtensionNumberData(descriptive, formatType, numberValue));
            return;
        }

        String stringValue = set.getString(ExtensionPlayerValueTable.STRING_VALUE);
        if (stringValue != null) {
            boolean isPlayerName = false;
            extensionTab.putStringData(new ExtensionStringData(descriptive, isPlayerName, stringValue));
        }
    }

    private ExtensionDescriptive extractDescriptive(ResultSet set) throws SQLException {
        String name = set.getString("provider_name");
        String text = set.getString(ExtensionProviderTable.TEXT);

        String iconName = set.getString("provider_icon_name");
        Family family = Family.getByName(set.getString("provider_icon_family")).orElse(Family.SOLID);
        Icon icon = new Icon(family, iconName, Color.NONE);

        return new ExtensionDescriptive(name, text, null, icon, 0);
    }
}