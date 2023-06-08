import { Link } from "react-router-dom";
import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import logo from "../images/logo-criticalsoftware.png";
import SelectComponent from "../Components/SelectComponent";
import { BsArrowDown } from "react-icons/bs";

function RegisterIn() {
  const handleSubmit = "";
  const handleChange = "";

  return (
    <div className="container-fluid vh-100 position-relative">
      <div className="row h-50">
        <div className="col-12" style={{ background: "#C01722" }}></div>
      </div>
      <div className="row h-50">
        <div className="col-12 " style={{ background: "#404040" }}></div>
      </div>
      <div class="row position-absolute top-50 start-50 translate-middle vh-100 vw-100 d-flex align-items-center justify-content-center">
        <div className="col-lg-4 mx-auto">
          <form
            className=" d-flex flex-column bg-white p-5 rounded-5 "
            onSubmit={handleSubmit}
          >
            <div className="row d-flex justify-content-around h-100 ">
              <div className="col-lg-10 text-dark d-flex align-items-center">
                <h3 className="p-0 text-justify ">
                  Antes de entrar deve concluir o seu registo. Obrigado
                </h3>
              </div>
              <div className="col-lg-2">
                <img className="p-0" src={logo} width={100} height={100} />
              </div>
            </div>
            <div className="mb-3 form-outline">
              <InputComponent
                placeholder={"Primeiro Nome*"}
                id="firstNameInput"
                required
                name="firstName"
                type="text"
                onChange={handleChange}
              />
            </div>
            <div className="mb-3 form-outline">
              <InputComponent
                placeholder={"Ultimo Nome*"}
                id="firstNameInput"
                required
                name="firstName"
                type="text"
                onChange={handleChange}
              />
            </div>
            <div class="form-group mt-3 mb-3">
              <div class="input-group rounded">
                <SelectComponent placeholder={"Local"} />
                <span class="input-group-text border-0" id="search-addon">
                  <BsArrowDown />
                </span>
              </div>
            </div>
            <div className="row">
              <label class="custom-file-label mb-2" for="inputGroupFile01">
                {" "}
                Selecione Imagem de perfil:
              </label>
              <div className="col-lg-12">
                <div class="input-group mb-3">
                  <div class="custom-file">
                    <InputComponent
                      type="file"
                      class="custom-file-input"
                      id="inputGroupFile01"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div className="row mb-3">
              <ButtonComponent name={"Entrar"} type="submit" />
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default RegisterIn;
