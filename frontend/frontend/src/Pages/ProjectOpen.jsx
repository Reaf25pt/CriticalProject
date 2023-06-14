import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import Keyword from "../Components/Keyword";
import { BsSearch } from "react-icons/bs";
import TextAreaComponent from "../Components/TextAreaComponent";
import SelectComponent from "../Components/SelectComponent";
import FormTask from "../Components/FormTask";
import TimeLine from "../Components/TimeLine";
function ProjectOpen() {
  const handleSubmit = "";
  const handleChange = "";

  return (
    <div class="container-fluid">
      <ul class="nav nav-tabs" role="tablist">
        <li class="nav-item" role="presentation">
          <button
            class="nav-link active"
            id="tab1"
            data-bs-toggle="tab"
            data-bs-target="#content1"
            type="button"
            role="tab"
            aria-controls="content1"
            aria-selected="true"
            style={{ background: "#C01722", color: "white" }}
          >
            Dados{" "}
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button
            class="nav-link"
            id="tab2"
            data-bs-toggle="tab"
            data-bs-target="#content2"
            type="button"
            role="tab"
            aria-controls="content2"
            aria-selected="false"
            style={{ background: "#C01722", color: "white" }}
          >
            Convidar membros{" "}
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button
            class="nav-link"
            id="tab3"
            data-bs-toggle="tab"
            data-bs-target="#content3"
            type="button"
            role="tab"
            aria-controls="content3"
            aria-selected="false"
            style={{ background: "#C01722", color: "white" }}
          >
            Plano do Projeto{" "}
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button
            class="nav-link"
            id="tab4"
            data-bs-toggle="tab"
            data-bs-target="#content4"
            type="button"
            role="tab"
            aria-controls="content4"
            aria-selected="false"
            style={{ background: "#C01722", color: "white" }}
          >
            Chat{" "}
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button
            class="nav-link"
            id="tab5"
            data-bs-toggle="tab"
            data-bs-target="#content5"
            type="button"
            role="tab"
            aria-controls="content4"
            aria-selected="false"
            style={{ background: "#C01722", color: "white" }}
          >
            Histórico{" "}
          </button>
        </li>
      </ul>
      <div class="tab-content mt-2">
        <div
          class="tab-pane fade show active"
          id="content1"
          role="tabpanel"
          aria-labelledby="tab1"
        >
          {" "}
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
                <ButtonComponent name={"Editar"} type="submit" />
              </div>
            </form>{" "}
          </div>
        </div>
        <div
          class="tab-pane fade"
          id="content2"
          role="tabpanel"
          aria-labelledby="tab2"
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
        <div
          class="tab-pane fade"
          id="content3"
          role="tabpanel"
          aria-labelledby="tab3"
        >
          <FormTask />
        </div>
        <div
          class="tab-pane fade"
          id="content4"
          role="tabpanel"
          aria-labelledby="tab4"
        >
          <h3>Tab 4 Content</h3>
          <p>This is the content for Tab 4.</p>
        </div>
        <div
          class="tab-pane fade"
          id="content5"
          role="tabpanel"
          aria-labelledby="tab5"
        >
          <TimeLine />
        </div>
      </div>
    </div>
  );
}

export default ProjectOpen;
