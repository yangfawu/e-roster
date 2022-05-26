export const range = (length: number) => Array.from({length}, (_, i) => i);

export const rangeBounds = (a: number, b: number) => range(b - a).map(v => v + a);

export const forJSX = (
    iter: number[],
    func: (v: number, i: number) => any
) => iter.map((a, b) => func(a, b));

class ListNode<T> {

    private readonly _data: T | null
    private _next: ListNode<T> | null
    private _prev: ListNode<T> | null

    constructor(data: T | null) {
        this._data = data;
        this._next = null;
        this._prev = null;
    }

    get data(): T | null {
        return this._data;
    }

    get next(): ListNode<T> | null {
        return this._next;
    }

    set next(value: ListNode<T> | null) {
        this._next = value;
    }

    get prev(): ListNode<T> | null {
        return this._prev;
    }

    set prev(value: ListNode<T> | null) {
        this._prev = value;
    }
}

export class HistoryStack<T> {

    private top: ListNode<T> | null
    private cursor: ListNode<T> | null
    private _size: number
    private cursorSize: number

    constructor() {
        this.top = null;
        this.cursor = null;
        this._size = 0;
        this.cursorSize = 0;
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

    get current(): T | null {
        if (this.cursor == null)
            return null;
        return this.cursor.data;
    }

    private move(count: number, child: 'prev' | 'next') {
        while (count-- > 0) {
            if (this.cursor == null)
                throw new Error("Encountered internal transversal error.");
            this.cursor = this.cursor[child];
        }
    }

    moveTo(level = 0): void {
        level = Math.max(0, Math.min(level, this._size));
        if (level > this.cursorSize)
            this.move(level - this.cursorSize, 'next');
        else
            this.move(this.cursorSize - level, 'prev');
        this.cursorSize = level;
    }

    get size(): number {
        return this._size;
    }

}