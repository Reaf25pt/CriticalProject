import Container from "react-bootstrap/esm/Container";
import Row from "react-bootstrap/esm/Row";
import MainTitle from "../Components/MainTitle";
import SecondTitle from "../Components/SecondTitle";
import Form from "react-bootstrap/esm/Form";
import Col from "react-bootstrap/esm/Col";
import InputComponent from "../Components/InputComponent";
import Button from "../Components/Button";
import Footer from "../Components/Footer";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useState } from "react";

function ChangePassword() {
  const { token } = useParams();
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

    /*  console.log(credentials.password);
    console.log(credentials.password2); */

    if (credentials.password === credentials.password2) {
      fetch("http://localhost:8080/projetofinal/rest/user/newpasswordvialink", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",

          tokenRecoverPassword: token,
          password: credentials.password,
        },
      }).then((response) => {
        if (response.status === 200) {
          alert("Password alterada com sucesso");
        } else if (response.status === 400) {
          alert("O link expirou. Receberá um novo link no email");
        } else {
          alert("Algo correu mal. Contacte os nossos serviços");
        }
        navigate("/", { replace: true });
      });
    } else {
      alert(
        "As passwords inseridas não são iguais. Escreva a mesma password nos 2 campos"
      );
    }
  };

  return (
    <Container fluid>
      <Row>
        <MainTitle />
      </Row>
      <Row>
        <SecondTitle name={"Alterar a Password"} />
      </Row>
      <Row className="mt-5">
        <Col className="d-flex justify-content-around">
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col>
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
            <span className="form-text">
              {" "}
              A senha deve ter entre 6 a 16 caracteres
            </span>
            <Button type="submit" name={"Alterar password"} />
          </Form>
        </Col>
      </Row>
      <Row>
        <Footer />
      </Row>
    </Container>
  );
}

export default ChangePassword;
