import React, {Component} from 'react';
import styles from './Game.module.scss';
import Board from "../Board/Board";

interface GameProps {}

interface GameState {}

class Game extends Component<GameProps, GameState> {
    render() {
        return (
            <div className={styles.Game}>
                <div className={styles.gameBoard}>
                    <Board></Board>
                </div>
                <div className={styles.gameInfo}>
                    <div>{/* status */}</div>
                    <div>{/* TODO */}</div>
                </div>
            </div>
        );
    }
}

export default Game;
