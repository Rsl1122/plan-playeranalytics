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

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.FormatType;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.builder.DataValue;
import com.djrapitops.plan.extension.builder.ValueBuilder;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.implementation.ProviderInformation;

public class ExtValueBuilder implements ValueBuilder {

    // TODO add Conditional stuff so that annotation implementation can use builders
    private final String pluginName;
    private final String text;
    private String description;
    private int priority = 0;
    private boolean showInPlayerTable = false;
    private Icon icon;
    private String tabName;

    private boolean formatAsPlayerName = false;
    private FormatType formatType = FormatType.NONE;

    public ExtValueBuilder(String text, DataExtension extension) {
        this.text = text;
        pluginName = extension.getClass().getAnnotation(PluginInfo.class).name();
    }

    @Override
    public ValueBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ValueBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public ValueBuilder showInPlayerTable() {
        this.showInPlayerTable = true;
        return this;
    }

    @Override
    public ValueBuilder icon(Icon icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public ValueBuilder showOnTab(String tabName) {
        this.tabName = tabName;
        return this;
    }

    @Override
    public ValueBuilder format(FormatType formatType) {
        this.formatType = formatType;
        return this;
    }

    @Override
    public ValueBuilder showAsPlayerPageLink() {
        formatAsPlayerName = true;
        return this;
    }

    private ProviderInformation getProviderInformation() {
        return getProviderInformation(false, null);
    }

    private ProviderInformation getBooleanProviderInformation(String providedCondition) {
        return getProviderInformation(false, providedCondition);
    }

    private ProviderInformation getPercentageProviderInformation() {
        return getProviderInformation(true, null);
    }

    private ProviderInformation getProviderInformation(boolean percentage, String providedCondition) {
        ProviderInformation.Builder builder = ProviderInformation.builder(pluginName)
                .setName(text.toLowerCase().replaceAll("\\s", ""))
                .setText(text)
                .setDescription(description)
                .setPriority(priority)
                .setIcon(icon)
                .setShowInPlayersTable(showInPlayerTable)
                .setTab(tabName)
                .setPlayerName(formatAsPlayerName)
                .setFormatType(formatType);

        if (percentage) {
            builder = builder.setAsPercentage();
        }

        if (providedCondition != null) {
            builder = builder.setProvidedCondition(providedCondition);
        }

        return builder.build();
    }

    @Override
    public DataValue<Boolean> buildBoolean(boolean value) {
        return new BooleanDataValue(value, getProviderInformation());
    }

    @Override
    public DataValue<Boolean> buildBooleanProvidingCondition(boolean value, String providedCondition) {
        return new BooleanDataValue(value, getBooleanProviderInformation(providedCondition));
    }

    @Override
    public DataValue<String> buildString(String value) {
        return new StringDataValue(value, getProviderInformation());
    }

    @Override
    public DataValue<Long> buildNumber(long value) {
        return new NumberDataValue(value, getProviderInformation());
    }

    @Override
    public DataValue<Double> buildDouble(double value) {
        return new DoubleDataValue(value, getProviderInformation());
    }

    @Override
    public DataValue<Double> buildPercentage(double value) {
        return new DoubleDataValue(value, getPercentageProviderInformation());
    }

    @Override
    public DataValue<String[]> buildGroup(String[] groups) {
        return new GroupsDataValue(groups, getProviderInformation());
    }
}
