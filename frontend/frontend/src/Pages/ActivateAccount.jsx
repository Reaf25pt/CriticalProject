import Container from "react-bootstrap/esm/Container";
import Button from "../Components/Button";
import MainTitle from "../Components/MainTitle";
import Row from "react-bootstrap/esm/Row";
import Footer from "../Components/Footer";
import { Link, useNavigate, useParams } from "react-router-dom";

function ActivateAccount() {
  const { token } = useParams();
  const navigate = useNavigate();

  const handleClick = (event) => {
    console.log("clique ");
    event.preventDefault();

    console.log("clique ");

    fetch("http://localhost:8080/projetofinal/rest/user/accountvalidation", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",

        tokenForValidation: token,
      },
    }).then((response) => {
      if (response.status === 200) {
        alert("Conta activada com sucesso");
      } else if (response.status === 400) {
        alert("O link expirou. Receberá um novo link no email");
      } else {
        alert("Algo correu mal. Contacte os nossos serviços");
      }
      navigate("/", { replace: true });
    });
  };

  return (
    <Container fluid>
      <Row>
        <MainTitle />
      </Row>
      <Row>
        <p style={{ marginTop: "150px", textAlign: "center" }}>
          Está a um passo de poder aceder à aplicação Laboratório de Inovação da
          Critical Software. Clique no botão para activar a sua conta.
        </p>
      </Row>
      <Row className="mt-5">
        <Button name={"Ativar conta"} type="button" onClick={handleClick} />
      </Row>
      <Row>
        <Footer />
      </Row>
    </Container>
  );
}

export default ActivateAccount;
