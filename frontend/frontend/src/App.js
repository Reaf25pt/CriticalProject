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
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { userStore } from "./stores/UserStore";

function App() {
  const [credentials, setCredentials] = useState({});
  const navigate = useNavigate();
  const user = userStore((state) => state.setUser);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    fetch("http://localhost:8080/projetofinal/rest/user/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        email: credentials.email,
        password: credentials.password,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
        } else {
          alert("Dados inválidos");
        }
        document.getElementById("emailInput").value = "";
        document.getElementById("passwordInput").value = "";
      })
      .then((loggedUser) => {
        user(loggedUser);
        navigate("/home", { replace: true });
      });
  };

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
          <Form className=" m-auto d-flex flex-column " onSubmit={handleSubmit}>
            <InputComponent
              placeholder={"Email *"}
              id="emailInput"
              required
              name="email"
              type="text"
              onChange={handleChange}
            />
            <InputComponent
              placeholder={"Password *"}
              id="passwordInput"
              required
              name="password"
              type="password"
              minLength={8}
              pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
              onChange={handleChange}
            />
            <div className="form-text">
              {" "}
              A senha deve ter entre 6 a 16 caracteres
            </div>
            <Link className="text-dark" to="forgetpassword">
              Esqueceu a password
            </Link>
            <Button name={"Entrar"} type="submit" />
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
