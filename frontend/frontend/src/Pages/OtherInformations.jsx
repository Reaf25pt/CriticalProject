import { Col, Container, Form, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";
import SelectComponent from "../Components/SelectComponent";
import Button from "../Components/Button";
import InputComponent from "../Components/InputComponent";

function OtherInformations() {
  return (
    <Container fluid>
      <Row className="mt-5">
        <Col>
          <LinkButton name={"Criar Projeto"} to={"/home/projectscreate"} />{" "}
          <LinkButton name={"Adicionar Membros"} to={"/home/addmembers"} />{" "}
          <LinkButton name={"Outros Dados"} />
        </Col>
      </Row>
      <Row className="mt-5">
        <Col md={3}>
          <Form>
            <InputComponent placeholder={"Nome da Equipa"} />

            <InputComponent placeholder={"Recursos Necessarios"} />
            <SelectComponent placeholder={"Skills Necessarias"} />

            <Button name={"Gravar"} type={"submit"} />
          </Form>
        </Col>
      </Row>
    </Container>
  );
}

export default OtherInformations;
