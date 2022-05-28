import {useTranslation} from "react-i18next";
import {Card} from "react-bootstrap-v5";
import {FontAwesomeIcon as Fa} from "@fortawesome/react-fontawesome";
import {faCrosshairs} from "@fortawesome/free-solid-svg-icons";
import KillsTable from "../../table/KillsTable";
import React from "react";

const PvpKillsTableCard = ({player_kills}) => {
    const {t} = useTranslation();
    return (
        <Card>
            <Card.Header>
                <h6 className="col-black">
                    <Fa icon={faCrosshairs} className="col-red"/> {t('html.label.recentPvpKills')}
                </h6>
            </Card.Header>
            <KillsTable kills={player_kills}/>
        </Card>
    )
}

export default PvpKillsTableCard;