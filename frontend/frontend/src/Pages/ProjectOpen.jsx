import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
function ProjectOpen() {
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
          <div className="container-fluid mt-5">
            <div className="row d-flex justify-content-around">
              <div className="col-lg-4">
                <div className="row bg-secondary p-3 rounded-4 text-white mb-5">
                  <div className="row mb-3 ">
                    <h4 className="col-lg-6 ">Nome do Projeto</h4>
                    <h4 className="col-lg-6">Local</h4>
                  </div>
                  <div className="row">
                    <h4 className="col-lg-6">Estado</h4>
                    <h4 className="col-lg-6">Membros do Projeto</h4>
                  </div>
                </div>{" "}
                <div className="row bg-secondary p-3 rounded-4 text-white mb-5">
                  <div className="bg-white text-center text-nowrap rounded-5 p-0 text-dark">
                    {" "}
                    <h3>Skills</h3>
                  </div>
                  <div>
                    {" "}
                    <div>Lista de Skill.....</div>
                  </div>
                </div>
                <div className="row bg-secondary p-3 rounded-4 text-white">
                  <div className="bg-white text-center text-nowrap rounded-5 p-0 text-dark">
                    {" "}
                    <h3>Palavras-Chave</h3>
                  </div>
                  <div>
                    {" "}
                    <div>Lista de palavras-chave.....</div>
                  </div>
                </div>
              </div>
              <div className="col-lg-6 bg-secondary rounded-4 p-3 ">
                <div className="bg-white rounded- h-100 w-100 ">Biografia</div>
              </div>
            </div>
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
          <h3>Tab 3 Content</h3>
          <p>This is the content for Tab 3.</p>
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
          <h3>Tab 5 Content</h3>
          <p>This is the content for Tab 5.</p>
        </div>
      </div>
    </div>
  );
}

export default ProjectOpen;
