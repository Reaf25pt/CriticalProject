import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
import ButtonComponent from "../Components/ButtonComponent";
import TextAreaComponent from "../Components/TextAreaComponent";
import { BsSearch, BsArrowDown } from "react-icons/bs";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import Keyword from "../Components/Keyword";

function ProjectsCreate() {
  const [credentials, setCredentials] = useState({});
  const navigate = useNavigate();
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

    const project = {
      // falta office e permitir lista de keywords
      title: credentials.projectName,
      keywords: [credentials.keyword],
      membersNumber: credentials.maxMembers,
      resources: credentials.resources,
      details: credentials.details,
    };

    fetch("http://localhost:8080/projetofinal/rest/project/newproject", {
      method: "POST",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
      },
      body: JSON.stringify(project),
    }).then((response) => {
      if (response.status === 200) {
        alert("Projecto criado com sucesso");
        navigate("/home", { replace: true });
      } else {
        alert("Algo correu mal");
      }
    });
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
            <form
              className="mt-5 p-5 bg-secondary rounded-5  "
              onSubmit={handleSubmit}
            >
              <div className="row mb-3">
                <div className="col ">
                  <div className="form-outline">
                    <InputComponent
                      placeholder={"Nome do projecto *"}
                      id="projectName"
                      required
                      name="projectName"
                      type="text"
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <Keyword />

                <div className="row mt-3 ">
                  <div className="col-lg-6 d-flex ">
                    <InputComponent
                      placeholder={"Palavra-chave *"}
                      id="keyword"
                      required
                      name="keyword"
                      type="search"
                      onChange={handleChange}
                    />
                    <div className="col-lg-2 input-group-text border-0 ">
                      <BsSearch />
                    </div>
                  </div>

                  <div className="col-lg-3">
                    <ButtonComponent name={"+"} />
                  </div>
                </div>
                <div className="form-outline mt-3">
                  <div className="bg-white d-flex ">
                    <p>Keywords</p>
                    <p>Keywords</p>
                  </div>
                </div>
              </div>

              <div class="form-outline mb-4">
                <TextAreaComponent
                  placeholder={"Descrição do projecto *"}
                  id="details"
                  name="details"
                  type="text"
                  onChange={handleChange}
                />
              </div>

              <div class="form-group mt-3">
                <div class="input-group rounded">
                  <SelectComponent
                    name="office"
                    id="officeInput"
                    required={true}
                    /*  onChange={onChange} */
                    placeholder={"Local de trabalho *"}
                    local={"Local de trabalho *"}
                  />
                  {/*  <span class="input-group-text border-0" id="search-addon">
                    <BsArrowDown />
                  </span> */}
                </div>
                {/* 
                  <SelectComponent placeholder={"Local"} />
                  <span class="input-group-text border-0" id="search-addon">
                    <BsArrowDown />
                  </span> */}
              </div>

              <div className="form-outline mb-4">
                <InputComponent
                  placeholder={"Número máximo de participantes"}
                  id="maxMembers"
                  name="maxMembers"
                  type="number"
                  onChange={handleChange}
                />
              </div>

              <div class="form-outline mb-4">
                <TextAreaComponent
                  placeholder={"Recursos (separar por vírgula)"}
                  id="resources"
                  name="resources"
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
        <div className="tab-content" id="myTabContent">
          <div
            className="tab-pane fade"
            id="profile"
            role="tabpanel"
            aria-labelledby="profile-tab"
          >
            <div className="row">
              <div className="mx-auto col-10 col-md-8 col-lg-6">
                <form className="mt-5 p-5 bg-secondary rounded-5 ">
                  <div className="row mb-3 ">
                    <div className="col ">
                      <div className="form-outline">
                        <InputComponent type="text" placeholder="Email" />
                      </div>
                    </div>
                  </div>

                  <ButtonComponent name={"Convidar"} />
                </form>
              </div>
              <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
                <div>
                  <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
                    Membros do Projetos
                  </h3>
                  <div className="bg-black text-white p-1 m-1 rounded-3 w-75 p-3 mx-auto">
                    <p>Rodrigo Ferreira</p>
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
