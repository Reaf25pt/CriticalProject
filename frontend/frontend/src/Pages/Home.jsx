import { Col, Container, Row } from "react-bootstrap";
import MainTitle from "../Components/MainTitle";
import HeaderComponent from "../Components/HeaderComponent";
import Sidebar from "../Components/Sidebar";
import { Outlet } from "react-router-dom";

function Home() {
  return (
    <Container fluid>
      <Row>
        <Sidebar />
        <Col>
          <Row>
            <HeaderComponent />
          </Row>
          <Row>
            <MainTitle />
          </Row>
        </Col>
      </Row>
    </Container>
  );
}

export default Home;
