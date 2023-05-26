import { Col, Container, Form, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";
import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
import Button from "../Components/Button";
import TextAreaComponent from "../Components/TextAreaComponent";

function ProjectsCreate() {
  const handleSubmit = "";

  return (
    <Container>
      <Row className="mt-5">
        <Col md={4}>
          <Row>
            <Col>
              <LinkButton name={"Adicionar Membros"} />
            </Col>
            <Col>
              {" "}
              <LinkButton name={"Outros Dados"} />
            </Col>
            <Row className="mt-5 ">
              <Form onSubmit={handleSubmit}>
                <Row className="mb-3">
                  <InputComponent placeholder={"Nome do Projeto"} />
                </Row>
                <Row className="mb-3">
                  <SelectComponent
                    placeholder={"Selecione as palavras-chaves"}
                  />
                </Row>
                <Row className="mb-3">
                  <InputComponent placeholder={"Nº Maximo de Membros"} />
                </Row>
                <Row className="mb-3">
                  <SelectComponent placeholder={"Selecione o local"} />
                </Row>
                <TextAreaComponent />

                <Button name={"Criar"} />
              </Form>
            </Row>
          </Row>
        </Col>
      </Row>
    </Container>
  );
}

export default ProjectsCreate;
