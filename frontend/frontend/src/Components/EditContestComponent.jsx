import ButtonComponent from "./ButtonComponent";
import TextAreaComponent from "./TextAreaComponent";
import InputComponent from "./InputComponent";

function EditContestComponent({ toggleComponent }) {
  return (
    <div class="container-fluid">
      <div className="row mt-5">
        <div className="col-lg-6 mx-auto bg-secondary rounded-3 p-5 mx-auto">
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
              <InputComponent type="date" />
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">
                Data de fim de Candidatura
              </h5>
              <InputComponent type="date" />
            </div>
          </div>
          <div className="row mt-5">
            <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white">
                Data de inicio do Concurso
              </h5>
              <InputComponent type="date" />
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">
                Data de fim do Concurso
              </h5>
              <InputComponent type="date" />
            </div>
          </div>
          <div className="row mt-5">
            <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white mb-3">
                Nº Maximo de Projetos
              </h5>
              <InputComponent type="text" />
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">Estado </h5>
              <h3 className="text-center text-white bg-danger rounded-3  mt-3">
                Planning
              </h3>
            </div>
            <div className="row mb-3 mt-5">
              <ButtonComponent
                type="button"
                name="Editar"
                onClick={toggleComponent}
              />
            </div>
            <div className="row">
              <ButtonComponent
                type="button"
                name="Cancelar"
                onClick={toggleComponent}
              />
            </div>
          </div>
        </div>
        <div className="col-lg-3 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <TextAreaComponent placeholder={"Descrição"} />
            </div>
          </div>
        </div>
        <div className="col-lg-3 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <TextAreaComponent placeholder={"Regras"} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditContestComponent;
