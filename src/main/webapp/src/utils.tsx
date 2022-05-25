export const range = (length: number) => Array.from({length}, (_, i) => i);

export const rangeBounds = (a: number, b: number) => range(b - a).map(v => v + a);

export const forJSX = (
    iter: number[],
    func: (v: number, i: number) => any
) => iter.map((a, b) => func(a, b));