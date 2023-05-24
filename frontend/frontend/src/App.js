import React from "react";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Image from "react-bootstrap/Image";
import logo from "./images/logo-criticalsoftware.png";
import Form from "react-bootstrap/Form";

import MainTitle from "./Components/MainTitle";
import Button from "./Components/Button";
import LinkButton from "./Components/LinkButton";
import InputComponent from "./Components/InputComponent";
import { Link } from "react-router-dom";
import Footer from "./Components/Footer";

function App() {
  return (
    <Container fluid>
      <Row className="mb-5">
        <Col>
          <MainTitle />
        </Col>
      </Row>
      <Row>
        <Col className="border-end border-dark">
          {" "}
          <Image src={logo} width={500} height={500} />
        </Col>

        <Col className=" d-flex justify-content-around">
          <Form className=" m-auto d-flex flex-column ">
            <InputComponent placeholder={"Email"} type={"email"} />
            <InputComponent placeholder={"Password"} type={"password"} />
            <div className="form-text">
              {" "}
              A senha deve ter entre 6 a 16 caracteres
            </div>
            <Link className="text-dark" to="forgetpassword">
              Esqueceu a password
            </Link>
            <Button name={"Entrar"} />
            <LinkButton name={"Registar"} to={"/register"} />
          </Form>
        </Col>
      </Row>
      <Row>
        <Footer />
      </Row>
    </Container>
  );
}

export default App;
