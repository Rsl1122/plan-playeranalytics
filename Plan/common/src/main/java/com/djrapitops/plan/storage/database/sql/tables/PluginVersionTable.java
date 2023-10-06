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
package com.djrapitops.plan.storage.database.sql.tables;

import com.djrapitops.plan.storage.database.DBType;
import com.djrapitops.plan.storage.database.sql.building.CreateTableBuilder;
import com.djrapitops.plan.storage.database.sql.building.Sql;

/**
 * Represents plan_plugin_versions table.
 * <p>
 * Keeps track of plugin version history.
 *
 * @author AuroraLS3
 */
public class PluginVersionTable {

    public static final String TABLE_NAME = "plan_plugin_versions";

    public static final String ID = "id";
    public static final String SERVER_ID = "server_id";
    public static final String PLUGIN_NAME = "plugin_name";
    public static final String VERSION = "version";
    public static final String MODIFIED = "modified";

    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_VERSION_LENGTH = 255;

    public static final String INSERT_STATEMENT = "INSERT INTO " + TABLE_NAME + " ("
            + SERVER_ID + ','
            + PLUGIN_NAME + ','
            + VERSION + ','
            + MODIFIED
            + ") VALUES (" + ServerTable.SELECT_SERVER_ID + ", ?, ?, ?)";

    private PluginVersionTable() {
        /* Static information class */
    }

    public static String createTableSQL(DBType dbType) {
        return CreateTableBuilder.create(TABLE_NAME, dbType)
                .column(ID, Sql.INT).primaryKey()
                .column(SERVER_ID, Sql.INT).notNull()
                .column(PLUGIN_NAME, Sql.varchar(100)).notNull()
                .column(VERSION, Sql.varchar(255))
                .column(MODIFIED, Sql.LONG).notNull().defaultValue("0")
                .foreignKey(SERVER_ID, ServerTable.TABLE_NAME, ServerTable.ID)
                .toString();
    }

}
