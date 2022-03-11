import './style/main.sass';
import './style/sb-admin-2.css'
import './style/style.css';

import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import React from "react";
import PlayerPage from "./views/PlayerPage";
import PlayerOverview from "./views/PlayerOverview";
import player from "./mockdata/player.json";
import PlayerSessions from "./views/PlayerSessions";
import PlayerPvpPve from "./views/PlayerPvpPve";
import PlayerServers from "./views/PlayerServers";
import PlayerPluginData from "./views/PlayerPluginData";
import {ThemeContextProvider} from "./hooks/themeHook";

const PlayerRedirect = () => {
    return (<Navigate to={"overview"} replace={true}/>)
}

function App() {
    return (
        <div className="App">
            <ThemeContextProvider>
                <div id="wrapper">
                    <BrowserRouter>
                        <Routes>
                            <Route path="/" element={<Navigate to="/player/AuroraLS3" replace={true}/>}/>
                            <Route path="/player/:identifier" element={<PlayerPage/>}>
                                <Route path="" element={<PlayerRedirect/>}/>
                                <Route path="overview" element={<PlayerOverview player={player}/>}/>
                                <Route path="sessions" element={<PlayerSessions player={player}/>}/>
                                <Route path="pvppve" element={<PlayerPvpPve player={player}/>}/>
                                <Route path="servers" element={<PlayerServers player={player}/>}/>
                                <Route path="plugins/:serverName" element={<PlayerPluginData player={player}/>}/>
                            </Route>
                        </Routes>
                    </BrowserRouter>
                </div>
            </ThemeContextProvider>
        </div>
    );
}

export default App;
