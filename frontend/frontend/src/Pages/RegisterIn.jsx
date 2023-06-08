import { Link } from "react-router-dom";
import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import logo from "../images/logo-criticalsoftware.png";
import SelectComponent from "../Components/SelectComponent";
import { BsArrowDown } from "react-icons/bs";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { userStore } from "../stores/UserStore";

function RegisterIn() {
  const [credentials, setCredentials] = useState({});
  const navigate = useNavigate();
  const user = userStore((state) => state.user);
  const userUpdate = userStore((state) => state.setUser);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const newInfo = {
      firstName: credentials.firstName,
      lastName: credentials.lastName,
      officeInfo: credentials.office,
    };

    fetch("http://localhost:8080/projetofinal/rest/user/ownprofile", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
      body: JSON.stringify(newInfo),
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
      })
      .then((loggedUser) => {
        userUpdate(loggedUser);
        // navigate("/home", { replace: true });
      });
  };

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
                placeholder={"Último Nome *"}
                id="lastNameInput"
                required
                name="lastName"
                type="text"
                onChange={handleChange}
              />
            </div>
            <div class="form-group mt-3 mb-3">
              <div class="input-group rounded">
                <SelectComponent
                  name="office"
                  id="officeInput"
                  required
                  onChange={handleChange}
                  placeholder={"Local de trabalho *"}
                />
                <span class="input-group-text border-0" id="search-addon">
                  <BsArrowDown />
                </span>
              </div>
            </div>
            <div className="row">
              <label class="custom-file-label mb-2" for="inputGroupFile01">
                {" "}
                Selecione imagem de perfil:
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
