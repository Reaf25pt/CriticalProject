import { Col, Container, Form, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";
import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
import ButtonComponent from "../Components/ButtonComponent";
import TextAreaComponent from "../Components/TextAreaComponent";
import { BsSearch, BsArrowDown } from "react-icons/bs";

function ProjectsCreate() {
  const handleSubmit = "";

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
            Criar Projeto
          </button>
        </li>
        <li className="nav-item" role="presentation">
          <button
            className="nav-link"
            id="profile-tab"
            data-bs-toggle="tab"
            data-bs-target="#profile"
            type="button"
            role="tab"
            aria-controls="profile"
            aria-selected="false"
            style={{ background: "#C01722", color: "white" }}
          >
            Convidar Membros
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
            <form className="mt-5 p-5 bg-secondary  ">
              <div className="row mb-3 ">
                <div className="col ">
                  <div className="form-outline">
                    <InputComponent type="text" placeholder="Nome do Projeto" />
                  </div>
                </div>
                <div class="form-group mt-3">
                  <div class="input-group rounded">
                    <input
                      type="search"
                      class="form-control rounded"
                      placeholder="Search"
                      aria-label="Search"
                      aria-describedby="search-addon"
                    />
                    <span class="input-group-text border-0">
                      <BsSearch />
                    </span>
                  </div>
                </div>
                <div className="form-outline mt-3">
                  <div className="bg-white d-flex ">
                    <p>Keywords</p>
                    <p>Keywords</p>
                  </div>
                </div>
              </div>

              <div className="form-outline mb-4">
                <InputComponent type="text" placeholder="Nº Max. membros" />

                <div class="form-group mt-3">
                  <div class="input-group rounded">
                    <SelectComponent placeholder={"Local"} />
                    <span class="input-group-text border-0" id="search-addon">
                      <BsArrowDown />
                    </span>
                  </div>
                </div>
              </div>

              <div class="form-outline mb-4">
                <TextAreaComponent
                  placeholder={"Recursos Necessarios(separar por virgula)"}
                />
              </div>
              <div class="form-outline mb-4">
                <TextAreaComponent placeholder={"Descrição do Projeto"} />
              </div>
              <ButtonComponent name={"Criar"} />
            </form>{" "}
          </div>
        </div>
        <div className="tab-content" id="myTabContent">
          <div
            className="tab-pane fade"
            id="profile"
            role="tabpanel"
            aria-labelledby="profile-tab"
          >
            <div className="row mx-auto col-10 col-md-8 col-lg-6">
              <form className="mt-5 p-5 bg-secondary  ">
                <div className="row mb-3 ">
                  <div className="col ">
                    <div className="form-outline">
                      <InputComponent type="text" placeholder="Email" />
                    </div>
                  </div>
                </div>

                <ButtonComponent name={"Convidar"} />
              </form>
              <div className=" col-2 col-md-4 col-lg-6">
                <div>
                  <h3>Membros</h3>
                  <div>
                    <p>Primeiro Nome</p>
                    <p>Ultimo Nome</p>
                  </div>
                </div>
              </div>
            </div>{" "}
          </div>
        </div>
      </div>
    </div>

    // <Container fluid className="ms-5">
    //   <Row className="mt-5 justify-content-md-center">
    //     <Col>
    //       <LinkButton name={"Criar Projeto"} />{" "}
    //       <LinkButton name={"Adicionar Membros"} to={"/home/addmembers"} />{" "}
    //       <LinkButton name={"Outros Dados"} to={"/home/otherinformations"} />
    //     </Col>
    //   </Row>
    //   <Row className="mt-5 ">
    //     <Form onSubmit={handleSubmit}>
    //       <Row>
    //         <Col md={4}>
    //           <Row className="mt-3">
    //             <InputComponent placeholder={"Nome do Projeto"} />
    //           </Row>
    //           <Row className="mt-3">
    //             <SelectComponent placeholder={"Selecione as palavras-chaves"} />
    //           </Row>
    //           <Row className="mt-3">
    //             <InputComponent placeholder={"Nº Maximo de Membros"} />
    //           </Row>
    //           <Row className="mt-3">
    //             <SelectComponent placeholder={"Selecione o local"} />
    //           </Row>
    //           <Row className="mt-5">
    //             <ButtonComponent name={"Criar"} />
    //           </Row>
    //         </Col>
    //         <Col md={8}>
    //           <TextAreaComponent name={"Descrição"} />
    //         </Col>
    //       </Row>
    //     </Form>
    //   </Row>
    // </Container>
  );
}

export default ProjectsCreate;
