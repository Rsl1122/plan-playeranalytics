import {useTranslation} from "react-i18next";
import {Card} from "react-bootstrap";
import {FontAwesomeIcon as Fa} from "@fortawesome/react-fontawesome";
import React, {useCallback, useEffect, useState} from "react";
import {faCheck, faGlobe, faUser, faUserPlus, faUsers} from "@fortawesome/free-solid-svg-icons";
import DataTablesTable from "../../table/DataTablesTable";
import {CardLoader} from "../../navigation/Loader";
import {Link} from "react-router-dom";
import {faCalendarCheck, faCalendarPlus, faClock} from "@fortawesome/free-regular-svg-icons";
import FormattedDate from "../../text/FormattedDate";
import FormattedTime from "../../text/FormattedTime";
import ExtensionIcon from "../../extensions/ExtensionIcon";
import {ExtensionValueTableCell} from "../../extensions/ExtensionCard";

const getActivityGroup = value => {
    const VERY_ACTIVE = 3.75;
    const ACTIVE = 3.0;
    const REGULAR = 2.0;
    const IRREGULAR = 1.0;
    if (value >= VERY_ACTIVE) {
        return "html.label.veryActive"
    } else if (value >= ACTIVE) {
        return "html.label.active"
    } else if (value >= REGULAR) {
        return "html.label.indexRegular"
    } else if (value >= IRREGULAR) {
        return "html.label.irregular"
    } else {
        return "html.label.indexInactive"
    }
}

const PlayerListCard = ({data, title}) => {
    const {t} = useTranslation();
    const [options, setOptions] = useState(undefined);

    useEffect(() => {
        if (!data) return;

        const columns = [{
            title: <><Fa icon={faUser}/> {t('html.label.name')}</>,
            data: {_: "name", display: "link"}
        }, {
            title: <><Fa icon={faCheck}/> {t('html.label.activityIndex')}</>,
            data: {_: "activityIndex", display: "activityIndexAndGroup"}
        }, {
            title: <><Fa icon={faClock}/> {t('html.label.activePlaytime')}</>,
            data: {_: "activePlaytime", display: "activePlaytimeFormatted"}
        }, {
            title: <><Fa icon={faCalendarPlus}/> {t('html.label.sessions')}</>,
            data: "sessions"
        }, {
            title: <><Fa icon={faUserPlus}/> {t('html.label.registered')}</>,
            data: {_: "registered", display: "registeredFormatted"}
        }, {
            title: <><Fa icon={faCalendarCheck}/> {t('html.label.lastSeen')}</>,
            data: {_: "lastSeen", display: "lastSeenFormatted"}
        }, {
            title: <><Fa icon={faGlobe}/> {t('html.label.country')}</>,
            data: "country"
        }];

        columns.push(...data.extensionDescriptors.map(descriptor => {
            return {
                title: <><ExtensionIcon icon={descriptor.icon}/> {descriptor.text}</>,
                data: {_: descriptor.name + "Value", display: descriptor.name}
            }
        }));

        const rows = data.players.map(player => {
            const row = {
                name: player.playerName,
                uuid: player.playerUUID,
                link: <Link to={"/player/" + player.playerUUID}>{player.playerName}</Link>,
                activityIndex: player.activityIndex,
                activityIndexAndGroup: player.activityIndex + " (" + t(getActivityGroup(player.activityIndex)) + ")",
                activePlaytime: player.playtimeActive,
                activePlaytimeFormatted: <FormattedTime timeMs={player.playtimeActive}/>,
                sessions: player.sessionCount,
                registered: player.registered,
                registeredFormatted: <FormattedDate date={player.registered}/>,
                lastSeen: player.lastSeen,
                lastSeenFormatted: <FormattedDate date={player.lastSeen}/>,
                country: player.country
            };
            data.extensionDescriptors.forEach(descriptor => {
                row[descriptor.name] = <ExtensionValueTableCell data={player.extensionValues[descriptor.name]}/>;
                row[descriptor.name + "Value"] = JSON.stringify(player.extensionValues[descriptor.name]?.value);
            })
            return row;
        });

        setOptions({
            responsive: true,
            deferRender: true,
            columns: columns,
            data: rows,
            order: [[5, "desc"]]
        });
    }, [data, t]);

    const rowKeyFunction = useCallback((row, column) => {
        return row.uuid + "-" + (column ? JSON.stringify(column.data) : '');
    }, []);

    if (!options) return <CardLoader/>

    return (
        <Card>
            <Card.Header>
                <h6 className="col-black">
                    <Fa icon={faUsers} className="col-black"/> {title ? title : t('html.label.playerList')}
                </h6>
            </Card.Header>
            <DataTablesTable id={"players-table"}
                             rowKeyFunction={rowKeyFunction}
                             options={options}/>
        </Card>
    )
}

export default PlayerListCard;