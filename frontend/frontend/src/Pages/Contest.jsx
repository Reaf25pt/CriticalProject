import { Col, Container, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";

function Contest() {
  return (
    <Container fluid>
      <Row md={2} className="m-5">
        <Col>
          <LinkButton name={"Adicionar Concurso"} to={"/home/contestcreate"} />
        </Col>
        <Col md={10}></Col>
      </Row>
    </Container>
  );
}

export default Contest;
