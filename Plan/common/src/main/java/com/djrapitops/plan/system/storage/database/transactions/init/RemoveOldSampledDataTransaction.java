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
package com.djrapitops.plan.system.storage.database.transactions.init;

import com.djrapitops.plan.data.store.objects.DateObj;
import com.djrapitops.plan.system.storage.database.operation.ExecStatement;
import com.djrapitops.plan.system.storage.database.operation.Executable;
import com.djrapitops.plan.system.storage.database.queries.objects.TPSQueries;
import com.djrapitops.plan.system.storage.database.sql.tables.PingTable;
import com.djrapitops.plan.system.storage.database.sql.tables.TPSTable;
import com.djrapitops.plan.system.storage.database.transactions.Transaction;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static com.djrapitops.plan.system.storage.database.sql.parsing.Sql.*;

/**
 * Transaction for cleaning up old data from the database.
 *
 * @author Rsl1122
 */
public class RemoveOldSampledDataTransaction extends Transaction {

    private final UUID serverUUID;
    private final long deleteTPSOlderThanMs;
    private final long deletePingOlderThanMs;

    public RemoveOldSampledDataTransaction(
            UUID serverUUID,
            long deleteTPSOlderThanMs,
            long deletePingOlderThanMs
    ) {
        this.serverUUID = serverUUID;
        this.deleteTPSOlderThanMs = deleteTPSOlderThanMs;
        this.deletePingOlderThanMs = deletePingOlderThanMs;
    }

    @Override
    protected void performOperations() {
        Optional<Integer> allTimePeak = query(TPSQueries.fetchAllTimePeakPlayerCount(serverUUID)).map(DateObj::getValue);

        execute(cleanTPSTable(allTimePeak.orElse(-1)));
        execute(cleanPingTable());
    }

    private Executable cleanTPSTable(int allTimePlayerPeak) {
        String sql = DELETE_FROM + TPSTable.TABLE_NAME +
                WHERE + '(' + TPSTable.DATE + "<?)" +
                AND + '(' + TPSTable.PLAYERS_ONLINE + "!=?)";

        return new ExecStatement(sql) {
            @Override
            public void prepare(PreparedStatement statement) throws SQLException {
                statement.setLong(1, System.currentTimeMillis() - deleteTPSOlderThanMs);
                statement.setInt(2, allTimePlayerPeak);
            }
        };
    }

    private Executable cleanPingTable() {
        String sql = DELETE_FROM + PingTable.TABLE_NAME +
                WHERE + '(' + PingTable.DATE + "<?)" +
                OR + '(' + PingTable.MIN_PING + "<0)";

        return new ExecStatement(sql) {
            @Override
            public void prepare(PreparedStatement statement) throws SQLException {
                statement.setLong(1, System.currentTimeMillis() - deletePingOlderThanMs);
            }
        };
    }
}