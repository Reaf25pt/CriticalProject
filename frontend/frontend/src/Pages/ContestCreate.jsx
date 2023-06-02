import { Form } from "react-bootstrap";
import SecondTitle from "../Components/SecondTitle";
import InputComponent from "../Components/InputComponent";

function ContestCreate() {
  return (
    <>
      <div>
        <SecondTitle name={"Criar concurso"} />
      </div>
      <Form>
        <InputComponent placeholder={"Titulo*"} />
        <InputComponent placeholder={"Data Inicio*"} />
        <InputComponent placeholder={"Data Fim*"} />
        <InputComponent placeholder={"Nº Max de Projetos*"} />
      </Form>
    </>
  );
}

export default ContestCreate;
