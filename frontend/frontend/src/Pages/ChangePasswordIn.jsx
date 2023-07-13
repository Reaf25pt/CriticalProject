import ButtonComponent from "../Components/ButtonComponent";
import InputComponent from "../Components/InputComponent";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import { toast, Toaster } from "react-hot-toast";

function ChangePasswordIn() {
  const [credentials, setCredentials] = useState({});
  const navigate = useNavigate();
  const user = userStore((state) => state.user);
  const clearLoggedUser = userStore((state) => state.clearLoggedUser);

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
      fetch("http://localhost:8080/projetofinal/rest/user/newpassword", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",

          token: user.token,
          oldPassword: credentials.oldPassword,
          newPassword: credentials.password,
        },
      }).then((response) => {
        if (response.status === 200) {
          alert("Password alterada com sucesso");
          /*  localStorage.clear();
          sessionStorage.clear();
          clearLoggedUser();
          navigate("/", { replace: true }); */
        } else if (response.status === 400) {
          toast.error(
            "Atenção, a password antiga que inseriu não está correcta"
          );
        } else {
          toast.error("Pedido não satisfeito");
        }
      });
    } else {
      toast.error(
        "As novas passwords inseridas não são iguais. Escreva a mesma password nos 2 campos"
      );
      document.getElementById("oldPasswordInput").value = "";
      document.getElementById("newPasswordInput").value = "";
      document.getElementById("newPasswordInput2").value = "";
    }
  };

  return (
    <div className="container">
      <Toaster position="top-right" />

      <div className="row justify-content-center">
        <div className="col-lg-5">
          <form
            className="mt-5 p-5 bg-secondary rounded-5 vh-50 d-flex-column   "
            onSubmit={handleSubmit}
          >
            <div className="row mb-3">
              <div className="form-outline">
                <InputComponent
                  placeholder={"Password antiga*"}
                  id="oldPasswordInput"
                  required
                  name="oldPassword"
                  type="password"
                  minLength={8}
                  title="Password válida deve ter no mínimo 8 caracteres e conter 1 letra maiúscula, 1 letra minúscula, 1 número e 1 símbolo !@#$&*"
                  pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="row mb-3">
              <div className="form-outline">
                <InputComponent
                  placeholder={"Nova password *"}
                  id="newPasswordInput"
                  required
                  name="password"
                  type="password"
                  minLength={8}
                  title="Password válida deve ter no mínimo 8 caracteres e conter 1 letra maiúscula, 1 letra minúscula, 1 número e 1 símbolo !@#$&*"
                  pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="row mb-3">
              <div className="form-outline">
                <InputComponent
                  placeholder={"Confirmar nova password *"}
                  id="newPasswordInput2"
                  required
                  name="password2"
                  type="password"
                  minLength={8}
                  title="Password válida deve ter no mínimo 8 caracteres e conter 1 letra maiúscula, 1 letra minúscula, 1 número e 1 símbolo !@#$&*"
                  pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="row">
              <ButtonComponent name={"Alterar password"} type="submit" />
            </div>
          </form>{" "}
        </div>
      </div>
    </div>
  );
}

export default ChangePasswordIn;
