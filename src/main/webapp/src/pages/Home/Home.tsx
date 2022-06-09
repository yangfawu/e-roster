import React, { FC } from 'react';
import { Outlet } from "react-router-dom";
import { Container, Image, Nav, Navbar } from "react-bootstrap";
import { LinkContainer } from "react-router-bootstrap";
import axios, { AxiosResponse } from "axios";
import { auth, signIn, signOut } from "../../misc/firebase";
import { useAuthState } from "react-firebase-hooks/auth";

interface HomeProps {
}

// @ts-ignore
window["axios"] = axios;
// @ts-ignore
window["signIn"] = signIn;
// @ts-ignore
window["signOut"] = signOut;

const LAX = (prom: Promise<AxiosResponse>) => prom
    .then(res => console.log(res.data))
    .catch(err => console.error(`[Fetch Error] ${err.response.data.message || "NO MESSAGE"}`));
const config = (token: string) => ({
    headers: {
        Authorization: `Bearer ${token}`
    }
});
const pubPost = (url: string, data: any) => LAX(axios.post(`api/${url}`, data));
const post = (url: string, token: string, data: any) => LAX(axios.post(`api/${url}`, data, config(token)));
const get = (url: string, token: string) => LAX(axios.get(`api/${url}`, config(token)));

// @ts-ignore
window["Apost"] = post;
// @ts-ignore
window["Aget"] = get;
// @ts-ignore
window["ApostPUB"] = pubPost;

const Home: FC<HomeProps> = () => {
    const [user, loading, error] = useAuthState(auth);
    return (
        <>
            <Navbar collapseOnSelect expand="sm" bg="dark" variant="dark" sticky="top">
                <Container>
                    <LinkContainer to="/"><Navbar.Brand>E-Roster</Navbar.Brand></LinkContainer>
                    <Navbar.Toggle aria-controls="responsive-navbar-nav"/>
                    <Navbar.Collapse id="responsive-navbar-nav" className="justify-content-end">
                        <Nav>
                            <LinkContainer to="/"><Nav.Link>Home</Nav.Link></LinkContainer>
                            <LinkContainer to="/"><Image src={user?.photoURL || ""}/></LinkContainer>
                            <LinkContainer to="/auth"><Nav.Link>Account</Nav.Link></LinkContainer>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
            <Outlet/>
        </>
    );
};

export default Home;
