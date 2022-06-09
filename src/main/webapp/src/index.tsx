import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.scss';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Home from "./pages/Home/Home";
import { ManyToOneRoute } from "./misc/utils";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <React.StrictMode>
        <BrowserRouter><Routes>
            <Route path="/" element={<Home/>}>
                <Route path="auth" element={<p>LOGIN PAGE</p>}/>
            </Route>
            <Route path="/account" element={<p>ACCOUNT PAGE</p>}>
                <Route path="courses" element={<p>ACCOUNT COURSES PAGE</p>}/>
                <Route path="connections" element={<p>ACCOUNT CONNECTIONS PAGE</p>}/>
                <Route path="*" element={<Navigate to="" replace/>}/>
            </Route>
            <Route path="/course">
                <Route path="student/:courseId" element={<p>SPECIFIC STUDENT COURSE PAGE</p>}/>
                <Route path="teacher/:courseId" element={<p>SPECIFIC TEACHER COURSE PAGE</p>}/>
                {ManyToOneRoute(["", "*"], <Navigate to="/account/courses" replace/>)}
            </Route>
            <Route path="*" element={<Navigate to="/" replace/>}/>
        </Routes></BrowserRouter>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
