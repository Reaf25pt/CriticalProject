import { Col, Container, Form, Row } from "react-bootstrap";
import SecondTitle from "../Components/SecondTitle";
import InputComponent from "../Components/InputComponent";
import style from "./contestcreate.module.css";
import TextAreaComponent from "../Components/TextAreaComponent";
import ButtonComponent from "../Components/ButtonComponent";
import { userStore } from "../stores/UserStore";
import { useState } from "react";

function ContestCreate() {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    console.log(credentials);

    if (
      credentials.startOpenCall >= credentials.finishOpenCall ||
      credentials.finishOpenCall >= credentials.startDate ||
      credentials.startDate >= credentials.finishDate
    ) {
      alert(
        "Reveja as datas inseridas: a fase de candidaturas tem de ser anterior ao início da execução"
      );
    } else {
      var newContest = credentials;
    }
  };

  return (
    <div>
      <ul className="nav nav-tabs" id="myTab" role="tablist">
        <li className="nav-item" role="presentation">
          <button
            className="nav-link active"
            id="home-tab"
            data-bs-toggle="tab"
            data-bs-target="#home"
            type="button"
            role="tab"
            aria-controls="home"
            aria-selected="true"
            style={{ background: "#C01722", color: "white" }}
          >
            Criar Concurso
          </button>
        </li>
      </ul>
      <div className="tab-content" id="myTabContent">
        <div
          className="tab-pane fade show active"
          id="home"
          role="tabpanel"
          aria-labelledby="home-tab"
        >
          <div className="row mx-auto col-10 col-md-8 col-lg-6">
            <form
              className="mt-5 p-5 bg-secondary rounded-5  "
              onSubmit={handleSubmit}
            >
              <div className="row mb-3">
                <div className="col ">
                  <div className="form-outline">
                    <InputComponent
                      placeholder={"Título *"}
                      id="title"
                      required
                      name="title"
                      type="text"
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-lg-6">
                  <div className="form-outline">
                    <label className="text-white mb-2">
                      {" "}
                      Data de início de candidatura *
                    </label>

                    <InputComponent
                      placeholder={"*"}
                      id="startOpenCall"
                      required
                      name="startOpenCall"
                      type="date"
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="col-lg-6">
                  <div className="form-outline">
                    <label className="text-white mb-2">
                      {" "}
                      Data de fim de candidatura *
                    </label>
                    <InputComponent
                      placeholder={" *"}
                      id="finishOpenCall"
                      required
                      name="finishOpenCall"
                      type="date"
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>

              <div className="row mb-3">
                <div className="col-lg-6">
                  <div className="form-outline">
                    <label className="text-white mb-2">
                      Data de início de execução *
                    </label>

                    <InputComponent
                      placeholder={"*"}
                      id="startDate"
                      required
                      name="startDate"
                      type="date"
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="col-lg-6">
                  <div className="form-outline">
                    <label className="text-white mb-2">
                      Data de fim de execução *
                    </label>
                    <InputComponent
                      placeholder={"*"}
                      id="finishDate"
                      required
                      name="finishDate"
                      type="date"
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>
              <div className="form-outline mb-4">
                <InputComponent
                  placeholder={"Número máximo de projectos participantes *"}
                  id="maxNumberProjects"
                  name="maxNumberProjects"
                  required
                  type="number"
                  min="1"
                  onChange={handleChange}
                />
              </div>

              <div class="form-outline mb-4">
                <TextAreaComponent
                  placeholder={"Descrição do concurso *"}
                  id="details"
                  name="details"
                  type="text"
                  onChange={handleChange}
                />
              </div>
              <div class="form-outline mb-4">
                <TextAreaComponent
                  placeholder={"Regras do concurso *"}
                  id="rules"
                  name="rules"
                  required
                  type="text"
                  onChange={handleChange}
                />
              </div>
              <div className="row">
                <ButtonComponent name={"Criar"} type="submit" />
              </div>
            </form>{" "}
          </div>
        </div>
      </div>
    </div>
    // <Container fluid>
    //   <Container fluid className="mb-5">
    //     <SecondTitle name={"Criar concurso"} />
    //   </Container>{" "}
    //   <Container className="d-flex justify-content-center">
    //     <Form>
    //       <Row className={style.boxform}>
    //         <InputComponent placeholder={"Titulo*"} />
    //         <Col>
    //           <InputComponent placeholder={"Data Inicio*"} />
    //         </Col>
    //         <Col>
    //           <InputComponent placeholder={"Data Fim*"} />
    //         </Col>
    //         <InputComponent placeholder={"Nº Max de Projetos*"} />
    //       </Row>
    //       <Row className={style.boxform}>
    //         <Col md={8}>
    //           <TextAreaComponent name={"Descrição"} />
    //         </Col>
    //         <Col md={4}>
    //           <TextAreaComponent name={"Regras"} />
    //         </Col>
    //       </Row>
    //       <Container fluid className="mb-5 w-25 p-3 d-flex ">
    //         <Col>
    //           <ButtonComponent name={"Adicionar"} />
    //         </Col>
    //       </Container>
    //     </Form>
    //   </Container>
    // </Container>
  );
}

export default ContestCreate;
