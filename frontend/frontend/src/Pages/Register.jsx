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
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Register() {
  const [credentials, setCredentials] = useState({});
  const navigate = useNavigate();

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (
      credentials.password === credentials.password2 &&
      credentials.office !== "8"
    ) {
      const user = {
        firstName: credentials.firstName,
        lastName: credentials.lastName,
        email: credentials.email,
        office: credentials.office,
        nickname: credentials.nickname,
        photo: credentials.photo,
      };

      fetch("http://localhost:8080/projetofinal/rest/user/newaccount", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",

          password: credentials.password,
        },
        body: JSON.stringify(user),
      }).then((response) => {
        if (response.status === 200) {
          alert(
            "Conta criada com sucesso. Active a conta através do email enviado para a sua conte de email"
          );
          navigate("/", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
        document.getElementById("emailInput").value = "";
        document.getElementById("passwordInput").value = "";
        document.getElementById("passwordInput2").value = "";
        document.getElementById("firstNameInput").value = "";
        document.getElementById("lastNameInput").value = "";
        document.getElementById("nicknameInput").value = "";
        document.getElementById("photoInput").value = "";
        document.getElementById("officeInput").value = "8";
      });
    } else {
      alert(
        "Verifique os dados inseridos"
        //"As passwords inseridas não são iguais. Escreva a mesma password nos 2 campos"
      );
    }
  };

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
          <Form className="d-flex flex-column mb-5" onSubmit={handleSubmit}>
            <Row>
              <InputComponent
                placeholder={"Email *"}
                id="emailInput"
                required
                name="email"
                type="text"
                onChange={handleChange}
              />
            </Row>
            <Row>
              <Col>
                {" "}
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
              </Col>
              <Col>
                {" "}
                <InputComponent
                  placeholder={"Confirmar Password *"}
                  id="passwordInput2"
                  required
                  name="password2"
                  type="password"
                  minLength={8}
                  pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
                  onChange={handleChange}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <InputComponent
                  placeholder={"Primeiro Nome *"}
                  id="firstNameInput"
                  required
                  name="firstName"
                  type="text"
                  onChange={handleChange}
                />
              </Col>
              <Col>
                {" "}
                <InputComponent
                  placeholder={"Último Nome *"}
                  id="lastNameInput"
                  required
                  name="lastName"
                  type="text"
                  onChange={handleChange}
                />
              </Col>
            </Row>

            <Row>
              <Col>
                {" "}
                <InputComponent
                  placeholder={"Alcunha"}
                  id="nicknameInput"
                  name="nickname"
                  type="text"
                  onChange={handleChange}
                />
              </Col>
              <Col>
                {" "}
                <InputComponent
                  placeholder={"Foto"}
                  id="photoInput"
                  name="photo"
                  type="url"
                  onChange={handleChange}
                />
              </Col>
            </Row>
            <Row>
              <SelectComponent
                name="office"
                id="officeInput"
                required
                onChange={handleChange}
              />
            </Row>
            <Button name={"Registar"} type={"submit"} />
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
