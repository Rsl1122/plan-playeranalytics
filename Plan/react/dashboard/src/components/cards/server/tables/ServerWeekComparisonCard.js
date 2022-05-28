import {useTranslation} from "react-i18next";
import {Card} from "react-bootstrap-v5";
import {FontAwesomeIcon as Fa} from "@fortawesome/react-fontawesome";
import {faCrosshairs, faExchangeAlt, faSkull, faUsers} from "@fortawesome/free-solid-svg-icons";
import ComparisonTable from "../../../table/ComparisonTable";
import BigTrend from "../../../trend/BigTrend";
import {faCalendarCheck, faClock} from "@fortawesome/free-regular-svg-icons";
import React from "react";
import {TableRow} from "../../../table/TableRow";

const ServerWeekComparisonCard = ({data}) => {
    const {t} = useTranslation();
    if (!data) return <></>;
    return (
        <Card>
            <Card.Header>
                <h6 className="col-black">
                    <Fa icon={faExchangeAlt}/> {t('html.label.weekComparison')}
                </h6>
            </Card.Header>
            <ComparisonTable comparisonHeader={t('html.label.comparing7days')}
                             headers={[data.start + ' - ' + data.midpoint, data.midpoint + ' - ' + data.end, t('html.label.trend')]}>
                <TableRow icon={faUsers} color="blue" text={t('html.label.uniquePlayers')}
                          values={[data.unique_before, data.unique_after, <BigTrend trend={data.unique_trend}/>]}/>
                <TableRow icon={faUsers} color="light-green" text={t('html.label.newPlayers')}
                          values={[data.new_before, data.new_after, <BigTrend trend={data.new_trend}/>]}/>
                <TableRow icon={faUsers} color="lime" text={t('html.label.regularPlayers')}
                          values={[data.regular_before, data.regular_after, <BigTrend trend={data.regular_trend}/>]}/>
                <TableRow icon={faClock} color="green"
                          text={t('html.label.averagePlaytime') + ' ' + t('html.label.perPlayer')}
                          values={[data.average_playtime_before, data.average_playtime_after,
                              <BigTrend trend={data.average_playtime_trend}/>]}/>
                <TableRow icon={faCalendarCheck} color="teal" text={t('html.label.sessions')}
                          values={[data.sessions_before, data.sessions_after,
                              <BigTrend trend={data.sessions_trend}/>]}/>
                <TableRow icon={faCrosshairs} color="red" text={t('html.label.playerKills')}
                          values={[data.player_kills_before, data.player_kills_after,
                              <BigTrend trend={data.player_kills_trend}/>]}/>
                <TableRow icon={faCrosshairs} color="green" text={t('html.label.mobKills')}
                          values={[data.mob_kills_before, data.mob_kills_after,
                              <BigTrend trend={data.mob_kills_trend}/>]}/>
                <TableRow icon={faSkull} color="black" text={t('html.label.deaths')}
                          values={[data.deaths_before, data.deaths_after, <BigTrend trend={data.deaths_trend}/>]}/>
            </ComparisonTable>
        </Card>
    )
}

export default ServerWeekComparisonCard