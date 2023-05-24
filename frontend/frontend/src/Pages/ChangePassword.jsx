import Container from "react-bootstrap/esm/Container";
import Row from "react-bootstrap/esm/Row";
import MainTitle from "../Components/MainTitle";
import SecondTitle from "../Components/SecondTitle";
import Form from "react-bootstrap/esm/Form";
import Col from "react-bootstrap/esm/Col";
import InputComponent from "../Components/InputComponent";
import Button from "../Components/Button";
import Footer from "../Components/Footer";

function ChangePassword() {
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
          <Form>
            <Row>
              <Col>
                <InputComponent placeholder={"Password"} />
              </Col>
              <Col>
                {" "}
                <InputComponent placeholder={"Confirmar password"} />
              </Col>
            </Row>
            <span className="form-text">
              {" "}
              A senha deve ter entre 6 a 16 caracteres
            </span>
            <Button name={"Alterar password"} />
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
