import Container from "react-bootstrap/esm/Container";
import MainTitle from "../Components/MainTitle";
import SecondTitle from "../Components/SecondTitle";
import React from "react";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
import Button from "../Components/Button";
import Footer from "../Components/Footer";
import LinkImageComponent from "../Components/LinkImageComponent";

function Register() {
  return (
    <Container fluid>
      <Row>
        <MainTitle />
      </Row>
      <Row>
        <SecondTitle name={"Registar"} />
      </Row>
      <Row className="mt-5">
        <Col md={11} className=" d-flex justify-content-around">
          <Form className="d-flex flex-column mb-5">
            <Row>
              <InputComponent placeholder={"Email"} />
            </Row>
            <Row>
              <Col>
                {" "}
                <InputComponent placeholder={"Password"} />
              </Col>
              <Col>
                {" "}
                <InputComponent placeholder={"Confirmar Password"} />
              </Col>
            </Row>
            <Row>
              <Col>
                <InputComponent placeholder={"Primeiro Nome"} />
              </Col>
              <Col>
                {" "}
                <InputComponent placeholder={"Ultimo Nome"} />
              </Col>
            </Row>

            <Row>
              <Col>
                {" "}
                <InputComponent placeholder={"Alcunha"} />
              </Col>
              <Col>
                {" "}
                <InputComponent placeholder={"Foto"} />
              </Col>
            </Row>
            <Row>
              <SelectComponent />
            </Row>
            <Button name={"Registar"} />
          </Form>
        </Col>
        <Col>
          <LinkImageComponent to={"/"} />
        </Col>
      </Row>
      <Row>
        <Footer />
      </Row>
    </Container>
  );
}

export default Register;
