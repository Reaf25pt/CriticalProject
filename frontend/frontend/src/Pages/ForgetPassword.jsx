import Container from "react-bootstrap/esm/Container";
import MainTitle from "../Components/MainTitle";
import Row from "react-bootstrap/esm/Row";
import SecondTitle from "../Components/SecondTitle";
import Form from "react-bootstrap/Form";
import Col from "react-bootstrap/esm/Col";
import InputComponent from "../Components/InputComponent";
import Button from "../Components/Button";
import Footer from "../Components/Footer";
import LinkImageComponent from "../Components/LinkImageComponent";
import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

function ForgetPassword() {
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

    fetch("http://localhost:8080/projetofinal/rest/user/recoverpassword", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",

        email: credentials.email,
      },
    }).then((response) => {
      if (response.status === 200) {
        alert(
          "Pedido efectuado com sucesso. Altere a password através do link que receberá no email inserido"
        );
        navigate("/", { replace: true });
        /*  } else if (response.status === 404) {
        alert("Não existe nenhuma conta associada ao email inserido"); */
      } else {
        alert("Algo correu mal. Tente novamente");
      }
      document.getElementById("emailInput").value = "";
    });
  };

  return (
    <Container fluid>
      <Row>
        <MainTitle />
      </Row>
      <Row className="mb-5">
        <SecondTitle name={"Esqueceu a password"} />
      </Row>
      <Row>
        <Col md={11} className=" d-flex justify-content-around ">
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col>
                <InputComponent
                  placeholder={"Email *"}
                  id="emailInput"
                  required
                  name="email"
                  type="text"
                  onChange={handleChange}
                />
              </Col>
            </Row>
            <Button name={"Enviar email"} type="submit" />
          </Form>
        </Col>
        <Col>
          <LinkImageComponent to={"/"} />{" "}
        </Col>
      </Row>
      <Row>
        <Footer />
      </Row>
    </Container>
  );
}

export default ForgetPassword;
