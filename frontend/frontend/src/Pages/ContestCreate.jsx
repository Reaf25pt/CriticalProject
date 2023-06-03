import { Col, Container, Form, Row } from "react-bootstrap";
import SecondTitle from "../Components/SecondTitle";
import InputComponent from "../Components/InputComponent";
import style from "./contestcreate.module.css";
import TextAreaComponent from "../Components/TextAreaComponent";
import ButtonComponent from "../Components/ButtonComponent";

function ContestCreate() {
  return (
    <Container fluid className="ms-5">
      {" "}
      <div>
        <SecondTitle name={"Criar concurso"} />
      </div>
      <Form className="mt-5">
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
          <TextAreaComponent name={"Descrição"} />
        </Row>
        <Row className={style.boxform}>
          {" "}
          <TextAreaComponent name={"Regras"} />
        </Row>
        <Row>
          <ButtonComponent name={"Adicionar"} />
        </Row>
      </Form>
    </Container>
  );
}

export default ContestCreate;
