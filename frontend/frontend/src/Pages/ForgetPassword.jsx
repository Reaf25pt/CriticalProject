import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import logo from "../images/logo-criticalsoftware.png";

import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import LinkButton from "../Components/LinkButton";
import { toast, Toaster } from "react-hot-toast";

function ForgetPassword() {
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

    fetch("http://localhost:8080/projetofinal/rest/user/recoverpassword", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",

        email: credentials.email,
      },
    }).then((response) => {
      if (response.status === 200) {
        toast.success(
          "Pedido efectuado com sucesso. Altere a password através do link que receberá no email inserido"
        );
        navigate("/", { replace: true });
        /*  } else if (response.status === 404) {
        alert("Não existe nenhuma conta associada ao email inserido"); */
      } else {
        toast.error("Algo correu mal. Tente novamente");
      }
      document.getElementById("emailInput").value = "";
    });
  };

  return (
    <div className="container-fluid vh-100 position-relative">
      <Toaster position="top-right" />

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
            <div className="row d-flex justify-content-around h-100 mb-3 ">
              <div className="col-lg-10 text-dark d-flex align-items-center">
                <h4 className="p-0 text-justify ">
                  Escreva o seu email e irá receber um link para alterar a sua
                  password
                </h4>
              </div>
              <div className="col-lg-2">
                <img className="p-0" src={logo} width={100} height={100} />
              </div>
            </div>
            <div className="mb-3 form-outline">
              <InputComponent
                placeholder={"Email *"}
                id="emailInput"
                required
                name="email"
                type="email"
                onChange={handleChange}
              />
            </div>

            <div className="row mb-2">
              <div className="row mb-2">
                <ButtonComponent name={"Enviar email"} type={"submit"} />
              </div>
              <div className="row">
                <LinkButton name={"Voltar"} to={"/"} />
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default ForgetPassword;
