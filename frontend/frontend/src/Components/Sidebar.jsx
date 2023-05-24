import { Col, Container, Row } from "react-bootstrap";
import style from "./sidebar.module.css";
import { Link, Outlet } from "react-router-dom";

function Sidebar() {
  return (
    <Container>
      <Row>
        <Col className={style.sidebar} md={2}>
          <Link to={"/home"}>Inicio</Link>
          <Link to={"/projects"}>Projetos</Link>
          <Link to={"/contests"}>Concursos</Link>
          <Link to={"/profile"}>Perfil</Link>
        </Col>{" "}
        <Col md={10}>
          <Outlet />
        </Col>
      </Row>
    </Container>
  );
}

export default Sidebar;
