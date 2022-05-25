import React, {Component} from 'react';
import Game from "../Game/Game";

interface AppProps {}

interface AppState {}

class App extends Component<AppProps, AppState> {
    render() {
        return (
            <Game></Game>
        );
    }
}

export default App;
