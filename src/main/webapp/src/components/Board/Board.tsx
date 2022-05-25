import React, {Component} from 'react';
import styles from './Board.module.scss';
import {forJSX, range} from "../../utils";
import Square from "../Square/Square";

interface BoardProps {}

interface BoardState {
    squares: (string | null)[]
    xIsNext: boolean
}

class Board extends Component<BoardProps, BoardState> {

    static ITER = range(3);
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

    constructor(props: Readonly<BoardProps> | BoardProps) {
        super(props);
        this.state = {
            squares: range(9).map(_ => null),
            xIsNext: true
        }
    }

    get currentMark(): [boolean, string] {
        const xIsNext = this.state.xIsNext;
        return [xIsNext, xIsNext ? 'X' : 'O'];
    }

    get gameHasWinner() {
        const squares = this.state.squares.slice();
        for (const [i, j, k] of Board.CHECKS) {
            if (squares[i] == null || squares[j] == null || squares[k] == null)
                continue;
            if (squares[i] == squares[j] && squares[j] == squares[k])
                return true;
        }
        return false;
    }

    handleSquareClick(i: number) {
        const nextSquares = this.state.squares.slice();
        if (nextSquares[i] != null || this.gameHasWinner)
            return;

        const [xIsNext, mark] = this.currentMark;
        nextSquares[i] = mark;
        this.setState({
            squares: nextSquares,
            xIsNext: !xIsNext
        });
    }

    renderSquare(i: number) {
        return (
            <Square
                value={this.state.squares[i]}
                onClick={() => this.handleSquareClick(i)}
                key={i}
            />
        );
    }

    render() {
        return (
            <div>
                <div className={styles.status}>Turn: {this.currentMark[1]}</div>
                {forJSX(Board.ITER, i => (
                    <div className={styles.boardRow} key={i}>
                        {forJSX(Board.ITER, j => this.renderSquare(3*i + j))}
                    </div>
                ))}
            </div>
        );
    }
}

export default Board;
