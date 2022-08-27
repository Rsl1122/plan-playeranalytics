import React, {useEffect} from 'react';

import {linegraphButtons, tooltip} from "../../../util/graphs";
import Highcharts from "highcharts/highstock";
import NoDataDisplay from "highcharts/modules/no-data-to-display"
import {useTranslation} from "react-i18next";
import {useTheme} from "../../../hooks/themeHook";
import {withReducedSaturation} from "../../../util/colors";
import Accessibility from "highcharts/modules/accessibility";

const CpuRamPerformanceGraph = ({id, data, dataSeries}) => {
    const {t} = useTranslation();
    const {graphTheming, nightModeEnabled} = useTheme();

    useEffect(() => {
        const spline = 'spline'

        const series = {
            playersOnline: {
                name: t('html.label.playersOnline'),
                type: 'areaspline',
                tooltip: tooltip.zeroDecimals,
                data: dataSeries.playersOnline,
                color: nightModeEnabled ? withReducedSaturation(data.colors.playersOnline) : data.colors.playersOnline,
                yAxis: 0
            }, cpu: {
                name: t('html.label.cpu'),
                type: spline,
                tooltip: tooltip.twoDecimals,
                data: dataSeries.cpu,
                color: nightModeEnabled ? withReducedSaturation(data.colors.cpu) : data.colors.cpu,
                yAxis: 1
            }, ram: {
                name: t('html.label.ram'),
                type: spline,
                tooltip: tooltip.zeroDecimals,
                data: dataSeries.ram,
                color: nightModeEnabled ? withReducedSaturation(data.colors.ram) : data.colors.ram,
                yAxis: 2
            }
        };

        NoDataDisplay(Highcharts);
        Accessibility(Highcharts);
        Highcharts.setOptions({lang: {noData: t('html.label.noDataToDisplay')}})
        Highcharts.setOptions(graphTheming);
        Highcharts.stockChart(id, {
            rangeSelector: {
                selected: 1, // TODO Sync range selectors state
                buttons: linegraphButtons
            },
            yAxis: [{
                labels: {
                    formatter: function () {
                        return this.value + ' ' + t('html.unit.players')
                    }
                },
                softMin: 0,
                softMax: 2
            }, {
                labels: {
                    formatter: function () {
                        return this.value + ' %'
                    }
                },
                softMin: 0,
                softMax: 100
            }, {
                labels: {
                    formatter: function () {
                        return this.value + ' MB'
                    }
                },
                softMin: 0
            }],
            title: {text: ''},
            plotOptions: {
                areaspline: {
                    fillOpacity: 0.4
                }
            },
            legend: {
                enabled: true
            },
            series: [series.playersOnline, series.cpu, series.ram]
        });
    }, [data, dataSeries, graphTheming, nightModeEnabled, id, t])

    return (
        <div className="chart-area" style={{height: "450px"}} id={id}>
            <span className="loader"/>
        </div>
    )
};

export default CpuRamPerformanceGraph