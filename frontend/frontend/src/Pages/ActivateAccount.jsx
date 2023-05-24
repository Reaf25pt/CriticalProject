import Container from "react-bootstrap/esm/Container";
import Button from "../Components/Button";
import MainTitle from "../Components/MainTitle";
import Row from "react-bootstrap/esm/Row";
import Footer from "../Components/Footer";

function ActivateAccount() {
  return (
    <Container fluid>
      <Row>
        <MainTitle />
      </Row>
      <Row className="mt-5">
        <Button name={"Ativar conta"} />
      </Row>
      <Row>
        <Footer />
      </Row>
    </Container>
  );
}

export default ActivateAccount;
