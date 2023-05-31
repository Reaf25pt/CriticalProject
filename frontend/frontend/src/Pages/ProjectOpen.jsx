import { Col, Container, Row } from "react-bootstrap";
import ButtonComponent from "../Components/ButtonComponent";

function ProjectOpen() {
  return (
    <Container>
      <Row>
        <Col md={3}>
          <Row>
            <Col md={6}>
              <label>Nome do Projeto</label>
              <label>Skills Necessarias</label>
              <label>Equipa do Projeto</label>
              <label>Vencedor</label>
            </Col>
            <Col md={6}>
              <label>Local</label>
              <label>Estado</label>
              <label>Palavras-chaves</label>
            </Col>
          </Row>
          <ButtonComponent name="Participar" />
        </Col>
        <Col md={9}>
          <div>
            <p>Descritivo</p>
            <label>
              {" "}
              Lorem ipsum dolor sit amet consectetur adipisicing elit. Omnis
              impedit eveniet tempore nemo dicta magni perspiciatis, molestias,
              quam totam quia temporibus recusandae exercitationem. Explicabo
              nisi quod suscipit quia ut optio!
            </label>
          </div>
        </Col>
      </Row>
    </Container>
  );
}

export default ProjectOpen;
