import { Col, Container, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";

function Projects() {
  return (
    <Container fluid>
      <Row>
        <Col md={2} className="m-5">
          <LinkButton name={"Adicionar Projeto"} to={"/home/projectscreate"} />
        </Col>
        <Col md={10}></Col>
      </Row>
    </Container>
  );
}

export default Projects;
