import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";

function FormTask() {
  return (
    <div className="container-fluid mt-5 vh-75">
      <div>
        <form
          className="row d-flex justify-content-around bg-secondary 
          rounded-5 pt-3
        "
        >
          <div className="col-lg-4">
            <div className="row ">
              <div className="col-lg-12 ">
                <div className="row mb-3">
                  <div className="col-lg-6">
                    <InputComponent placeholder={"Titulo"} />
                  </div>
                  <div className="col-lg-6">
                    <SelectComponent local={"Membro Responsavel"} />
                  </div>
                </div>
                <div className="row mb-3">
                  <div className="col-lg-6">
                    <label className="text-white">Data de Inicio:</label>
                    <InputComponent placeholder={"Data Inicio"} type="date" />
                  </div>
                  <div className="col-lg-6">
                    <label className="text-white">Data de Fim:</label>

                    <InputComponent placeholder={"Data Fim"} type="date" />
                  </div>
                </div>
                <div className="row mb-3">
                  <div className="col-lg-6">
                    <InputComponent placeholder={"Executores Adicionais"} />
                  </div>
                  <div className="col-lg-6">
                    <SelectComponent
                      placeholder={"Tarefas Precedentes"}
                      local={"Tarefas Precedentes"}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12 col-sm-12 col-md-12 col-lg-6 d-flex align-items-center ">
            <textarea
              class="text-dark bg-white rounded-2 w-100 h-75 "
              placeholder="Escreva aqui a sua biografia"
              name="bio"
              type="text"
            ></textarea>
          </div>{" "}
          <div className="col-lg-1 d-flex align-items-center">
            <ButtonComponent name={"Adicionar"} />
          </div>
        </form>
      </div>
    </div>
  );
}

export default FormTask;
