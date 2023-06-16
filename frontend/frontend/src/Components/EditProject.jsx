function EditProject() {
  return (
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
  );
}

export default EditProject;
