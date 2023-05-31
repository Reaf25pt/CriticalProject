import { Col, Container, Row } from "react-bootstrap";
import MainTitle from "../Components/MainTitle";
import HeaderComponent from "../Components/HeaderComponent";
import Sidebar from "../Components/Sidebar";
import { Outlet } from "react-router-dom";
import Footer from "../Components/Footer";

function Home() {
  return (
    <Container fluid>
      <Row>
        <Col md={1}>
          <Sidebar />
        </Col>
        <Col md={11}>
          <HeaderComponent />
          <MainTitle />
          <Outlet />
          <Footer />
        </Col>
      </Row>
      {/* <Row>
        <HeaderComponent />
      </Row>
      <Row>
        <MainTitle />
      </Row>
      <Row>
        <Col md={0} className="m-0">
          <Sidebar />
        </Col>
        <Col md={10}>
          <Outlet />
        </Col>
      </Row>
      <Row>
        <Footer />
      </Row> */}
    </Container>
  );
}

export default Home;
