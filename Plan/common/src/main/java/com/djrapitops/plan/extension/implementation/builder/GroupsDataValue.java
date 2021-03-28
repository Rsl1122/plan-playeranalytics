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
package com.djrapitops.plan.extension.implementation.builder;

import com.djrapitops.plan.extension.builder.DataValue;
import com.djrapitops.plan.extension.implementation.ProviderInformation;

public class GroupsDataValue implements DataValue<String[]> {

    private final String[] value;
    private final ProviderInformation information;

    public GroupsDataValue(String[] value, ProviderInformation information) {
        this.value = value;
        this.information = information;
    }

    @Override
    public String[] getValue() {
        return value;
    }

    public ProviderInformation getInformation() {
        return information;
    }
}
