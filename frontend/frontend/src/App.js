import React from "react";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Image from "react-bootstrap/Image";
import logo from "./images/logo-criticalsoftware.png";
import Form from "react-bootstrap/Form";

import MainTitle from "./Components/MainTitle";
import ButtonComponent from "./Components/ButtonComponent";
import LinkButton from "./Components/LinkButton";
import InputComponent from "./Components/InputComponent";
import { Link } from "react-router-dom";
import Footer from "./Components/Footer";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { userStore } from "./stores/UserStore";

function App() {
  const [credentials, setCredentials] = useState({});
  const navigate = useNavigate();
  const user = userStore((state) => state.setUser);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    fetch("http://localhost:8080/projetofinal/rest/user/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        email: credentials.email,
        password: credentials.password,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
        } else {
          alert("Dados inválidos");
          document.getElementById("emailInput").value = "";
          document.getElementById("passwordInput").value = "";
        }
      })
      .then((loggedUser) => {
        user(loggedUser);
        navigate("/home/start", { replace: true });
      })
      .catch(console.error);
    // TODO confirmar que está certo?
  };

  return (
    <div className="container-fluid bg-dark vh-100">
      <div className="row">
        <MainTitle />
      </div>
      <div className="row">
        <div className="col-xl-6 col-lg-6 d-flex justify-content-around   ">
          {" "}
          <img className="" src={logo} width={500} height={500} />
        </div>

        <div className="col-xl-6 col-lg-6 d-flex justify-content-around">
          <form
            className=" m-auto d-flex flex-column bg-secondary p-5 rounded-5 "
            onSubmit={handleSubmit}
          >
            <div className="mb-3 form-outline">
              <InputComponent
                placeholder={"Email *"}
                id="emailInput"
                required
                name="email"
                type="text"
                onChange={handleChange}
              />
            </div>
            <div className="form-outline">
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
            <div className="row mb-3 mt-1">
              <Link className="text-dark" to="forgetpassword">
                Esqueceu a sua password?
              </Link>
            </div>
            <div className="row mb-3">
              <ButtonComponent name={"Entrar"} type="submit" />
            </div>
            <div className="row">
              <LinkButton name={"Registar"} to={"/register"} />
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

export default App;
