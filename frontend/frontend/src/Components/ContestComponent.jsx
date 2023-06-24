import TextAreaComponent from "../Components/TextAreaComponent";
import ButtonComponent from "./ButtonComponent";

function ContestComponent({ toggleComponent }) {
  return (
    <div class="container-fluid">
      <div className="row mt-5">
        <div className="col-lg-5 mx-auto bg-secondary rounded-3 p-5 mx-auto">
          <div className="row mb-5">
            <h2 className="text-center text-white">Titulo do concurso</h2>
            <hr className="text-white" />
          </div>
          <div className="row">
            <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white">
                Data de inicio de Candidatura
              </h5>
              <h3 className="text-center text-white">dd-mm-yyy</h3>
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">
                Data de fim de Candidatura
              </h5>
              <h3 className="text-center text-white">dd-mm-yyy</h3>
            </div>
          </div>
          <div className="row mt-5">
            <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white">
                Data de inicio do Concurso
              </h5>
              <h3 className="text-center text-white">dd-mm-yyy</h3>
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">
                Data de fim do Concurso
              </h5>
              <h3 className="text-center text-white">dd-mm-yyy</h3>
            </div>
          </div>
          <div className="row mt-5">
            <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white mb-3">
                Nº Maximo de Projetos
              </h5>
              <h3 className="text-center text-white bg-warning rounded-3 p-2 mt-3">
                100/200
              </h3>
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">Estado </h5>
              <h3 className="text-center text-white bg-danger rounded-3 p-2 mt-3">
                Planning
              </h3>
            </div>
            <div className="row mx-auto justify-content-around mt-5">
              <div className="col-lg-12">
                <ButtonComponent
                  type="button"
                  name="Editar Projeto"
                  onClick={toggleComponent}
                />
              </div>
            </div>{" "}
          </div>
        </div>
        <div className="col-lg-3 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Descrição</h4>
              <hr />
              <h5>descrição</h5>
            </div>
          </div>
        </div>
        <div className="col-lg-3 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Regras</h4>
              <hr />
              <h5>Regras</h5>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ContestComponent;
