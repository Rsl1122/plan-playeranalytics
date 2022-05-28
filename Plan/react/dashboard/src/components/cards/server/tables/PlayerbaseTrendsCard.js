import {useTranslation} from "react-i18next";
import {Card} from "react-bootstrap-v5";
import {FontAwesomeIcon as Fa} from "@fortawesome/react-fontawesome";
import {faExchangeAlt, faUsers} from "@fortawesome/free-solid-svg-icons";
import ComparisonTable from "../../../table/ComparisonTable";
import BigTrend from "../../../trend/BigTrend";
import React from "react";
import {faClock} from "@fortawesome/free-regular-svg-icons";
import {TableRow} from "../../../table/TableRow";

const PlayerbaseTrendsCard = ({data}) => {
    const {t} = useTranslation();
    if (!data) return <></>;
    return (
        <Card>
            <Card.Header>
                <h6 className="col-black">
                    <Fa icon={faExchangeAlt} className="col-amber"/> {t('html.label.trends30days')}
                </h6>
            </Card.Header>
            <ComparisonTable comparisonHeader={t('html.text.comparing30daysAgo')}
                             headers={[t('html.label.thirtyDaysAgo'), t('html.label.now'), t('html.label.trend')]}>
                <TableRow icon={faUsers} color="black" text={t('html.label.totalPlayers')}
                          values={[data.total_players_now, data.total_players_then,
                              <BigTrend trend={data.total_players_trend}/>]}/>
                <TableRow icon={faUsers} color="lime" text={t('html.label.regularPlayers')}
                          values={[data.regular_players_now, data.regular_players_then,
                              <BigTrend trend={data.regular_players_trend}/>]}/>
                <TableRow icon={faClock} color="green"
                          text={t('html.label.averagePlaytime') + ' ' + t('html.label.perPlayer')}
                          values={[data.playtime_avg_now, data.playtime_avg_then,
                              <BigTrend trend={data.playtime_avg_trend}/>]}/>
                <TableRow icon={faClock} color="gray" text={t('html.label.afk') + ' ' + t('html.label.perPlayer')}
                          values={[data.afk_now, data.afk_then, <BigTrend trend={data.afk_trend}/>]}/>
                <TableRow icon={faClock} color="green"
                          text={t('html.label.averagePlaytime') + ' ' + t('html.label.perRegularPlayer')}
                          values={[data.regular_playtime_avg_now, data.regular_playtime_avg_then,
                              <BigTrend trend={data.regular_playtime_avg_trend}/>]}/>
                <TableRow icon={faClock} color="teal"
                          text={t('html.label.averageSessionLength') + ' ' + t('html.label.perRegularPlayer')}
                          values={[data.regular_session_avg_now, data.regular_session_avg_then,
                              <BigTrend trend={data.regular_session_avg_trend}/>]}/>
                <TableRow icon={faClock} color="gray"
                          text={t('html.label.afk') + ' ' + t('html.label.perRegularPlayer')}
                          values={[data.regular_afk_avg_now, data.regular_afk_avg_then,
                              <BigTrend trend={data.regular_afk_avg_trend}/>]}/>
            </ComparisonTable>
        </Card>
    )
}

export default PlayerbaseTrendsCard