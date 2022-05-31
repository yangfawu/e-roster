import React, {FC} from 'react';
import {Outlet} from "react-router-dom";
import {Container, Nav, Navbar} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";
import axios from "axios";

interface HomeProps {}

// @ts-ignore
window["axios"] = axios;
const Home: FC<HomeProps> = () => {
    return (
        <>
            <Navbar collapseOnSelect expand="sm" bg="dark" variant="dark" sticky="top">
                <Container>
                    <LinkContainer to="/"><Navbar.Brand>E-Roster</Navbar.Brand></LinkContainer>
                    <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                    <Navbar.Collapse id="responsive-navbar-nav" className="justify-content-end">
                        <Nav>
                            <LinkContainer to="/"><Nav.Link>Home</Nav.Link></LinkContainer>
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
