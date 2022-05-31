import {ReactNode} from "react";
import {Route} from "react-router-dom";

export const range = (length: number) => Array.from({length}, (_, i) => i);

export const rangeBounds = (a: number, b: number) => range(b - a).map(v => v + a);

export const forJSX = <T, S>(
    iter: T[],
    func: (val: T, idx: number) => S
) => iter.map((a, b) => func(a, b));

class ListNode<T> {

    private readonly _data: T | null

    constructor(data: T | null) {
        this._data = data;
        this._next = null;
        this._prev = null;
    }

    private _next: ListNode<T> | null

    get next(): ListNode<T> | null {
        return this._next;
    }

    set next(value: ListNode<T> | null) {
        this._next = value;
    }

    private _prev: ListNode<T> | null

    get prev(): ListNode<T> | null {
        return this._prev;
    }

    set prev(value: ListNode<T> | null) {
        this._prev = value;
    }

    get data(): T | null {
        return this._data;
    }
}

export class HistoryStack<T> {

    private top: ListNode<T> | null
    private cursor: ListNode<T> | null
    private cursorSize: number

    constructor() {
        this.top = null;
        this.cursor = null;
        this._size = 0;
        this.cursorSize = 0;
    }

    private _size: number

    get size(): number {
        return this._size;
    }

    get current(): T | null {
        if (this.cursor == null)
            return null;
        return this.cursor.data;
    }

    enqueue(data: T | null): HistoryStack<T> {
        const node = new ListNode<T>(data);
        node.prev = this.cursor;

        if (this.cursor != null)
            this.cursor.next = node;

        this.top = node;
        this.cursor = node;

        this._size = this.cursorSize + 1;
        this.cursorSize++;

        return this;
    }

    moveTo(level = 0): void {
        level = Math.max(0, Math.min(level, this._size));
        if (level > this.cursorSize)
            this.move(level - this.cursorSize, 'next');
        else
            this.move(this.cursorSize - level, 'prev');
        this.cursorSize = level;
    }

    private move(count: number, child: 'prev' | 'next') {
        while (count-- > 0) {
            if (this.cursor == null)
                throw new Error("Encountered internal transversal error.");
            this.cursor = this.cursor[child];
        }
    }

}

export const ManyToOneRoute = (
    paths: string[],
    element: ReactNode,
    children?: ReactNode
) => forJSX(paths, path => <Route key={path} path={path} element={element}>{children}</Route>);