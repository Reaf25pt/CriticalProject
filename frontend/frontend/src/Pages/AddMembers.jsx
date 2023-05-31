import { Card, Col, Container, Form, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";
import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
import ButtonComponent from "../Components/ButtonComponent";
import TextAreaComponent from "../Components/TextAreaComponent";
const handleSubmit = "";

function AddMembers() {
  return (
    <Container fluid className="mt-5 justify-content-md-center">
      <Row>
        <Col>
          <LinkButton name={"Criar Projeto"} to={"/home/projectscreate"} />{" "}
          <LinkButton name={"Adicionar Membros"} />{" "}
          <LinkButton name={"Outros Dados"} to={"/home/otherinformations"} />
        </Col>
      </Row>
      <Row className="mt-5">
        <Col md={12}>
          <Form onSubmit={handleSubmit} className="mt-5">
            <Row>
              <Col>
                <Row className="mt-3">
                  <SelectComponent placeholder={"Nome do Membro"} />
                  <Row className="mt-5">
                    <ButtonComponent name={"Convidar"} />
                  </Row>
                </Row>
              </Col>
            </Row>
          </Form>
        </Col>
      </Row>
    </Container>
  );
}

export default AddMembers;
