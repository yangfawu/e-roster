import React, {Component} from 'react';
import styles from './Board.module.scss';
import {forJSX, range} from "../../utils";
import Square, {SquareValue} from "../Square/Square";

interface BoardProps {
    handleSquareClick: (i: number) => any
    squares: SquareValue[]
}

interface BoardState {}

class Board extends Component<BoardProps, BoardState> {

    static ITER = range(3);

    renderSquare(i: number) {
        return (
            <Square
                value={this.props.squares[i]}
                onClick={() => this.props.handleSquareClick(i)}
                key={i}
            />
        );
    }

    render() {
        return (
            <div>
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
