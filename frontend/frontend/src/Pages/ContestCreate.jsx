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
              <TextAreaComponent name={"Descrição"} />
            </Row>
            <Row className={style.boxform}>
              {" "}
              <TextAreaComponent name={"Regras"} />
            </Row>
            <Row className="mb-5 w-25 p-3 ">
              <Col>
                <ButtonComponent name={"Adicionar"} />
              </Col>
            </Row>
          </Form>
        </Row>
      </Container>
    </div>
  );
}

export default ContestCreate;
