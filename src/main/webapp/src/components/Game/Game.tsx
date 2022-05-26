import React, {Component} from 'react';
import styles from './Game.module.scss';
import Board from "../Board/Board";
import {forJSX, HistoryStack, range, rangeBounds} from "../../utils";
import {SquareValue} from "../Square/Square";

interface GameProps {}

interface GameState {
    xMoves: boolean
    squares: SquareValue[],
    count: number
}

class Game extends Component<GameProps, GameState> {

    static CHECKS = [
        [0, 1, 2],
        [3, 4, 5],
        [6, 7, 8],
        [0, 3, 6],
        [1, 4, 7],
        [2, 5, 8],
        [0, 4, 8],
        [2, 4, 6]
    ];

    private history: HistoryStack<GameState>

    constructor(props: Readonly<GameProps> | GameProps) {
        super(props);

        const init: GameState = {
            squares: range(9).map(_ => null),
            count: 0,
            xMoves: true
        };
        this.history = new HistoryStack<GameState>().enqueue(init);
        this.state = init;
    }

    get winner(): SquareValue {
        const squares = this.state.squares.slice();
        for (const [i, j, k] of Game.CHECKS) {
            if (squares[i] == null || squares[j] == null || squares[k] == null)
                continue;
            if (squares[i] === squares[j] && squares[j] === squares[k])
                return squares[i];
        }
        return null;
    }

    get currentMark(): NonNullable<SquareValue> {
        return this.state.xMoves ? 'X' : 'O';
    }

    updateState(state: GameState) {
        this.history.enqueue(state);
        this.setState(state);
    }

    handleSquareClick(i: number, gameOver: boolean) {
        if (gameOver)
            return;

        const { squares, xMoves, count } = this.state;
        const nextSquares = squares.slice();
        if (nextSquares[i] != null)
            return;
        nextSquares[i] = this.currentMark;

        this.updateState({
            squares: nextSquares,
            count: count + 1,
            xMoves: !xMoves
        });
    }

    navigateTo(level: number) {
        try {
            this.history.moveTo(level);
        } catch (e) {
            console.log(e);
            return;
        }
        this.setState(this.history.current);
    }

    renderMoves() {
        const SIZE = this.history.size;
        // level 1 is an empty board
        // level SIZE is the most recent board
        return forJSX(rangeBounds(1, SIZE + 1), i => (
           <li key={i}>
               <button onClick={() => this.navigateTo(i)}>
                   {i > 1 ? i == SIZE ? `Return to most recent move`: `Return to move #${i - 1}` : `Return to start`}
               </button>
           </li>
        ));
    }

    render() {
        const winner = this.winner;
        const hasWinner = winner != null;
        const noTilesLeft = this.state.count == 9;

        const status = hasWinner ? `Winner is ${winner}!`: noTilesLeft ? `Tie!` : `${this.currentMark}'s Turn.`;
        const gameOver = hasWinner || noTilesLeft;

        return (
            <div className={styles.Game}>
                <div className={styles.gameBoard}>
                    <Board
                        handleSquareClick={(i: number) => this.handleSquareClick(i, gameOver)}
                        squares={this.state.squares}
                    />
                </div>
                <div className={styles.gameInfo}>
                    <div>{status}</div>
                    <ol>{this.renderMoves()}</ol>
                </div>
            </div>
        );
    }
}

export default Game;
