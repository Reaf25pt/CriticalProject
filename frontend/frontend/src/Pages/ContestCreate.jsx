import { Col, Container, Form, Row } from "react-bootstrap";
import SecondTitle from "../Components/SecondTitle";
import InputComponent from "../Components/InputComponent";
import style from "./contestcreate.module.css";
import TextAreaComponent from "../Components/TextAreaComponent";
import ButtonComponent from "../Components/ButtonComponent";

function ContestCreate() {
  return (
    <div>
      <Row className="mb-5">
        <SecondTitle name={"Criar concurso"} />
      </Row>
      <Container fluid className="d-flex justify-content-center">
        {" "}
        <Row className="mb-5"></Row>
        <Row>
          <Form>
            <Row className={style.boxform}>
              <InputComponent placeholder={"Titulo*"} />
              <Col>
                <InputComponent placeholder={"Data Inicio*"} />
              </Col>
              <Col>
                <InputComponent placeholder={"Data Fim*"} />
              </Col>
              <InputComponent placeholder={"Nº Max de Projetos*"} />
            </Row>
            <Row className={style.boxform}>
              <Col md={8}>
                <TextAreaComponent name={"Descrição"} />
              </Col>
              <Col md={4}>
                <TextAreaComponent name={"Regras"} />
              </Col>
            </Row>
            <Container fluid className="mb-5 w-25 p-3 d-flex ">
              <Col>
                <ButtonComponent name={"Adicionar"} />
              </Col>
            </Container>
          </Form>
        </Row>
      </Container>
    </div>
  );
}

export default ContestCreate;
