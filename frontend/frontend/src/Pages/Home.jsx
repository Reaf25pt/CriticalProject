import { Col, Container, Row } from "react-bootstrap";
import MainTitle from "../Components/MainTitle";
import HeaderComponent from "../Components/HeaderComponent";
import Sidebar from "../Components/Sidebar";
import { Outlet } from "react-router-dom";
import Footer from "../Components/Footer";
import style from "./home.module.css";

function Home() {
  return (
    <Container fluid>
      <Row>
        <Col md={1}>
          <Sidebar />
        </Col>
        <Col md={11}>
          <Row>
            <HeaderComponent />
          </Row>
          <Row>
            <Outlet />
          </Row>
        </Col>
      </Row>
    </Container>
  );
}

export default Home;
