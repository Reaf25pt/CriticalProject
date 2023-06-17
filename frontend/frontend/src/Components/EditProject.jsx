import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import Keyword from "./Keyword";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";

function EditProject({ toggleComponent }) {
  return (
    <div className="container-fluid">
      <form className="row mt-5 justify-content-around">
        <div className="col-lg-3 ">
          <div className="row bg-secondary justify-content-center rounded-5 p-4 mb-3">
            <InputComponent
              placeholder={"Nome do projecto *"}
              id="projectName"
              required
              name="projectName"
              type="text"
            />
            <div className="row d-flex justify-content-around mb-3 mt-3">
              <div className="col-lg-5 bg-white rounded-3 p-2">
                <select></select>
              </div>
              <div className="col-lg-5 bg-white rounded-3 p-2">
                <select></select>
              </div>
            </div>

            <InputComponent
              placeholder={"Nº de Membros *"}
              id="members"
              required
              name="projectName"
              type="text"
            />

            <div className="row mt-3 mb-3">
              <ButtonComponent name={"Editar"} type="submit" />
            </div>
            <div class="d-flex justify-content-around">
              <ButtonComponent
                type="click"
                name="Cancelar"
                onClick={toggleComponent}
              />
            </div>
          </div>
        </div>
        <div className="col-lg-4 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Descrição</h4>
              <hr />
              <textarea className="h-75 w-100"></textarea>
            </div>
          </div>
        </div>
        <div className="col-lg-4 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Recursos</h4>
              <hr />
              <textarea className="h-75 w-100"></textarea>
            </div>
          </div>
        </div>
      </form>{" "}
    </div>
  );
}

export default EditProject;
