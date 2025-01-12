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
package com.djrapitops.plan.storage.database.sql.tables.extension.graph;

import com.djrapitops.plan.storage.database.DBType;
import com.djrapitops.plan.storage.database.sql.building.CreateTableBuilder;
import com.djrapitops.plan.storage.database.sql.building.Sql;
import com.djrapitops.plan.storage.database.sql.tables.extension.ExtensionProviderTable;

import java.util.Optional;

import static com.djrapitops.plan.storage.database.sql.building.Sql.*;

/**
 * Represents extension_graph_color table.
 *
 * @author AuroraLS3
 */
public class ExtensionGraphColorTable {

    public static final String TABLE_NAME = "extension_graph_color";

    public static final String ID = "id";
    public static final String COLOR = "color";

    public static final String SELECT_ID_STATEMENT = SELECT + ID + FROM + TABLE_NAME + WHERE + COLOR + "=?";
    public static final String INSERT_STATEMENT = "INSERT INTO " + TABLE_NAME + " (" + COLOR + ") VALUES (?)";
    public static final int COLOR_MAX_LENGTH = 7;

    private ExtensionGraphColorTable() {
        /* Static sql utility class */
    }

    public static String createTableSQL(DBType dbType) {
        return CreateTableBuilder.create(TABLE_NAME, dbType)
                .column(ID, Sql.INT).primaryKey()
                .column(COLOR, Sql.varchar(COLOR_MAX_LENGTH))
                .build();
    }

    public static Optional<String> selectInSql(int n) {
        if (n == 0) {
            return Optional.empty();
        }
        return Optional.of(SELECT + COLOR + FROM + TABLE_NAME + WHERE + COLOR + " IN (" + Sql.nParameters(n) + ")");
    }

    /**
     * Represents extension_graph_color_to_graph table that joins extension_graph_color to a specific graph and column.
     */
    public static class ToProviderTable {
        public static final String TABLE_NAME = "extension_graph_color_to_graph";

        public static final String ID = "id";
        public static final String COLOR_ID = "color_id";
        public static final String PROVIDER_ID = "provider_id";
        public static final String COLUMN_INDEX = "column_index";

        public static final String INSERT_STATEMENT = "INSERT INTO " + TABLE_NAME + " (" + COLOR_ID + ',' + COLUMN_INDEX + ',' + PROVIDER_ID +
                ") VALUES (" + SELECT_ID_STATEMENT + ",?," + ExtensionProviderTable.STATEMENT_SELECT_PROVIDER_ID + ")";
        public static final String UPDATE_STATEMENT = "UPDATE " + TABLE_NAME + " SET " + COLOR_ID + "=" + SELECT_ID_STATEMENT +
                WHERE + COLUMN_INDEX + "=?" +
                AND + PROVIDER_ID + "=" + ExtensionProviderTable.STATEMENT_SELECT_PROVIDER_ID;
        public static final String DELETE_STATEMENT = "DELETE" + FROM + TABLE_NAME +
                WHERE + PROVIDER_ID + "=" + ExtensionProviderTable.STATEMENT_SELECT_PROVIDER_ID +
                AND + COLUMN_INDEX + "=?";
        public static final String SELECT_COLUMN_COUNT = SELECT + "COUNT(*)" +
                FROM + TABLE_NAME +
                WHERE + PROVIDER_ID + "=" + ExtensionProviderTable.STATEMENT_SELECT_PROVIDER_ID;

        private ToProviderTable() {
            /* Static sql utility class */
        }

        public static String createTableSQL(DBType dbType) {
            return CreateTableBuilder.create(TABLE_NAME, dbType)
                    .column(ID, Sql.INT).primaryKey()
                    .column(COLOR_ID, Sql.INT)
                    .column(PROVIDER_ID, Sql.INT)
                    .column(COLUMN_INDEX, Sql.INT).notNull()
                    .foreignKey(COLOR_ID, ExtensionGraphColorTable.TABLE_NAME, ExtensionGraphColorTable.ID)
                    .foreignKey(PROVIDER_ID, ExtensionProviderTable.TABLE_NAME, ExtensionProviderTable.ID)
                    .build();
        }
    }
}
