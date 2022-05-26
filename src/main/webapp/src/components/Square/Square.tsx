import React, {Component, MouseEventHandler} from 'react';
import styles from './Square.module.scss';

export type SquareValue = 'X' | 'O' | null;
interface SquareProps {
    value: SquareValue,
    onClick?: MouseEventHandler
}

interface SquareState {}

class Square extends Component<SquareProps, SquareState> {

    render() {
        return (
            <button className={styles.Square} onClick={this.props.onClick}>
                {this.props.value}
            </button>
        );
    }
}

export default Square;
