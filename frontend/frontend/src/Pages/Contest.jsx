import { Container } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";

function Contest() {
  return (
    <Container>
      <LinkButton name={"Adicionar Concurso"} to={"/home/contestcreate"} />

      <h3>Contest</h3>
    </Container>
  );
}

export default Contest;
