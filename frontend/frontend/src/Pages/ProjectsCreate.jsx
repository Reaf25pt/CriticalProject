import { Col, Container, Form, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";
import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
import Button from "../Components/Button";
import TextAreaComponent from "../Components/TextAreaComponent";

function ProjectsCreate() {
  const handleSubmit = "";

  return (
    <Container fluid>
      <Row className="mt-5 justify-content-md-center">
        <Col>
          <LinkButton name={"Criar Projeto"} />{" "}
          <LinkButton name={"Adicionar Membros"} to={"/home/addmembers"} />{" "}
          <LinkButton name={"Outros Dados"} to={"/home/otherinformations"} />
        </Col>
      </Row>
      <Row className="mt-5 ">
        <Form onSubmit={handleSubmit}>
          <Row>
            <Col md={4}>
              <Row className="mt-3">
                <InputComponent placeholder={"Nome do Projeto"} />
              </Row>
              <Row className="mt-3">
                <SelectComponent placeholder={"Selecione as palavras-chaves"} />
              </Row>
              <Row className="mt-3">
                <InputComponent placeholder={"Nº Maximo de Membros"} />
              </Row>
              <Row className="mt-3">
                <SelectComponent placeholder={"Selecione o local"} />
              </Row>
              <Row className="mt-5">
                <Button name={"Criar"} />
              </Row>
            </Col>
            <Col md={8}>
              <TextAreaComponent name={"Descrição"} />
            </Col>
          </Row>
        </Form>
      </Row>
    </Container>
  );
}

export default ProjectsCreate;
