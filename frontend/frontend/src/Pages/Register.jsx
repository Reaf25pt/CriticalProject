import MainTitle from "../Components/MainTitle";
import React from "react";

import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import Footer from "../Components/Footer";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import LinkButton from "../Components/LinkButton";

function Register() {
  const [credentials, setCredentials] = useState({});
  const navigate = useNavigate();

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (credentials.password === credentials.password2) {
      fetch("http://localhost:8080/projetofinal/rest/user/newaccount", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          email: credentials.email,
          password: credentials.password,
        },
      }).then((response) => {
        if (response.status === 200) {
          alert(
            "Conta criada com sucesso. Active a conta através do link enviado para o email registado"
          );
          navigate("/", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
        document.getElementById("emailInput").value = "";
        document.getElementById("passwordInput").value = "";
        document.getElementById("passwordInput2").value = "";
      });
    } else {
      alert(
        "Verifique os dados inseridos"
        //"As passwords inseridas não são iguais. Escreva a mesma password nos 2 campos"
      );
    }
  };

  return (
    <div className="container-fluid bg-dark vh-100">
      <div className="row">
        <MainTitle />
      </div>

      <div className="row h-75 d-flex align-items-center justify-content-center">
        <div className="mx-auto col-8 col-md-8 col-lg-4 ">
          <form
            className="mb-5 bg-secondary rounded-5 p-5"
            onSubmit={handleSubmit}
          >
            <h3 className="bg-dange text-white text-center mb-3">Registo</h3>
            <div className="form-outline mb-3">
              <InputComponent
                placeholder={"Email *"}
                id="emailInput"
                required
                name="email"
                type="text"
                onChange={handleChange}
              />
            </div>
            <div className="form-outline mb-3">
              <InputComponent
                placeholder={"Password *"}
                id="passwordInput"
                required
                name="password"
                type="password"
                minLength={8}
                title="Password válida deve ter no mínimo 8 caracteres e conter 1 letra maiúscula, 1 letra minúscula, 1 número e 1 símbolo !@#$&*"
                // pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
                onChange={handleChange}
              />
            </div>
            <div className="form-outline mb-3">
              <InputComponent
                placeholder={"Confirmar Password *"}
                id="passwordInput2"
                required
                name="password2"
                type="password"
                minLength={8}
                title="Password válida deve ter no mínimo 8 caracteres e conter 1 letra maiúscula, 1 letra minúscula, 1 número e 1 símbolo !@#$&*"
                // pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
                onChange={handleChange}
              />
            </div>{" "}
            {/* <div className="row">
              <div className="col">
                <InputComponent
                  placeholder={"Primeiro Nome *"}
                  id="firstNameInput"
                  required
                  name="firstName"
                  type="text"
                  onChange={handleChange}
                />
              </div>
              <div className="col">
                {" "}
                <InputComponent
                  placeholder={"Último Nome *"}
                  id="lastNameInput"
                  required
                  name="lastName"
                  type="text"
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="row">
              <div className="col">
                {" "}
                <InputComponent
                  placeholder={"Alcunha"}
                  id="nicknameInput"
                  name="nickname"
                  type="text"
                  onChange={handleChange}
                />
              </div>
              <div className="col">
                {" "}
                <InputComponent
                  placeholder={"Foto"}
                  id="photoInput"
                  name="photo"
                  type="url"
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="row">
              <SelectComponent
                name="office"
                id="officeInput"
                required
                onChange={handleChange}
                placeholder={"Selecione uma opção *"}
              />
            </div> */}
            <div className="row mb-2">
              <div className="row mb-2">
                <ButtonComponent name={"Registar"} type={"submit"} />
              </div>
              <div className="row">
                <LinkButton name={"Voltar"} to={"/"} />
              </div>
            </div>
          </form>
        </div>
      </div>
      <div className="row">
        <Footer />
      </div>
    </div>
  );
}

export default Register;
