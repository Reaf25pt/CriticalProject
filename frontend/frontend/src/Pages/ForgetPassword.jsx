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

function ForgetPassword() {
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
          <Form>
            <Row>
              <Col>
                <InputComponent placeholder={"Email"} />
              </Col>
            </Row>
            <Button name={"Enviar email"} />
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
