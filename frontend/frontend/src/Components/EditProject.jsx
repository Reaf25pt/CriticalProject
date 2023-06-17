import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import Keyword from "./Keyword";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";

function EditProject() {
  return (
    <div className="row mx-auto col-10 col-md-8 col-lg-6">
      <form className="mt-5 p-5 bg-secondary rounded-5  ">
        <div className="row mb-3">
          <div className="col ">
            <div className="form-outline">
              <InputComponent
                placeholder={"Nome do projecto *"}
                id="projectName"
                required
                name="projectName"
                type="text"
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
              />
              <div className="col-lg-2 input-group-text border-0 "></div>
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
          />
        </div>

        <div class="form-group mt-3">
          <div class="input-group rounded">
            <SelectComponent
              name="office"
              id="officeInput"
              required={true}
              placeholder={"Local de trabalho *"}
              local={"Local de trabalho *"}
            />
          </div>
        </div>

        <div className="form-outline mb-4">
          <InputComponent
            placeholder={"Número máximo de participantes"}
            id="maxMembers"
            name="maxMembers"
            type="number"
          />
        </div>

        <div class="form-outline mb-4">
          <TextAreaComponent
            placeholder={"Recursos (separar por vírgula)"}
            id="resources"
            name="resources"
            type="text"
          />
        </div>

        <div className="row">
          <ButtonComponent name={"Editar"} type="submit" />
        </div>
        <div class="d-flex justify-content-around">
          <ButtonComponent type="click" name="Cancelar" />
        </div>
      </form>{" "}
    </div>
  );
}

export default EditProject;
