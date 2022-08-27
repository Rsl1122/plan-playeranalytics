import React, {useEffect} from "react";
import Highcharts from 'highcharts';
import {useTheme} from "../../hooks/themeHook";
import {withReducedSaturation} from "../../util/colors";
import {useTranslation} from "react-i18next";
import Accessibility from "highcharts/modules/accessibility";

const PlayerbasePie = ({series}) => {
    const {t} = useTranslation();
    const {nightModeEnabled, graphTheming} = useTheme();

    useEffect(() => {
        const reduceColors = (slices) => slices.map(slice => {
            return {...slice, color: withReducedSaturation(slice.color)}
        });

        const pieSeries = {
            name: t('html.label.players'),
            colorByPoint: true,
            data: nightModeEnabled ? reduceColors(series) : series
        };

        Accessibility(Highcharts);
        Highcharts.setOptions(graphTheming);
        Highcharts.chart('playerbase-pie', {
            chart: {
                backgroundColor: 'transparent',
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie'
            },
            title: {text: ''},
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    showInLegend: true
                }
            },
            series: [pieSeries]
        });
    }, [series, graphTheming, nightModeEnabled, t]);

    return (<div className="chart-area" id="playerbase-pie"/>);
}

export default PlayerbasePie;