import { ReactNode } from "react";
import { Route } from "react-router-dom";

const range = (length: number) => Array.from({length}, (_, i) => i);

const rangeBounds = (a: number, b: number) => range(b - a).map(v => v + a);

const forJSX = <T, S>(
    iter: T[],
    func: (val: T, idx: number) => S
) => iter.map((a, b) => func(a, b));

const ManyToOneRoute = (
    paths: string[],
    element: ReactNode,
    children?: ReactNode
) => forJSX(paths, path => <Route key={path} path={path} element={element}>{children}</Route>);

export {
    range,
    rangeBounds,
    forJSX,
    ManyToOneRoute
};
