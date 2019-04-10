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
package com.djrapitops.plan.db.sql.tables;

import com.djrapitops.plan.db.DBType;
import com.djrapitops.plan.db.sql.parsing.CreateTableParser;
import com.djrapitops.plan.db.sql.parsing.Sql;

import static com.djrapitops.plan.db.sql.parsing.Sql.INT;

/**
 * Table information about 'plan_extension_user_table_values'.
 *
 * @author Rsl1122
 */
public class ExtensionPlayerTableValueTable {

    public static final String TABLE_NAME = "plan_extension_user_table_values";

    public static final String ID = "id";
    public static final String TABLE_ID = "table_id";
    public static final String USER_UUID = "uuid";

    // All values can be null
    public static final String VALUE_1 = "col_1_value";
    public static final String VALUE_2 = "col_2_value";
    public static final String VALUE_3 = "col_3_value";
    public static final String VALUE_4 = "col_4_value";
    public static final String VALUE_5 = "col_5_name";

    private ExtensionPlayerTableValueTable() {
        /* Static information class */
    }

    public static String createTableSQL(DBType dbType) {
        return CreateTableParser.create(TABLE_NAME, dbType)
                .column(ID, INT).primaryKey()
                .column(USER_UUID, Sql.varchar(36)).notNull()
                .column(VALUE_1, Sql.varchar(50))
                .column(VALUE_2, Sql.varchar(50))
                .column(VALUE_3, Sql.varchar(50))
                .column(VALUE_4, Sql.varchar(50))
                .column(VALUE_5, Sql.varchar(50))
                .column(TABLE_ID, INT).notNull()
                .foreignKey(TABLE_ID, ExtensionTableProviderTable.TABLE_NAME, ExtensionPluginTable.ID)
                .build();
    }
}