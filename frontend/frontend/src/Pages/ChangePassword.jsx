import logo from "../images/logo-criticalsoftware.png";

import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useState } from "react";
import { toast, Toaster } from "react-hot-toast";

function ChangePassword() {
  const { token } = useParams();
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
      fetch("http://localhost:8080/projetofinal/rest/user/newpasswordvialink", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",

          tokenRecoverPassword: token,
          password: credentials.password,
        },
      }).then((response) => {
        if (response.status === 200) {
          alert("Password alterada com sucesso");
          navigate("/", { replace: true });
        } else if (response.status === 400) {
          toast.error("O link expirou. Receberá um novo link no email");
        } else {
          toast.error("Pedido não satisfeito");
        }
      });
    } else {
      toast.error(
        "As passwords inseridas não são iguais. Escreva a mesma password nos 2 campos"
      );
    }
  };

  return (
    <div className="container-fluid vh-100 position-relative">
      <Toaster />

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
                  Escreva e confirme a nova password. Depois de concluir poderá
                  aceder de novo à sua conta
                </h4>
              </div>
              <div className="col-lg-2">
                <img className="p-0" src={logo} width={100} height={100} />
              </div>
            </div>
            <div className="row mb-3">
              {" "}
              <InputComponent
                placeholder={"Password *"}
                id="passwordInput"
                required
                name="password"
                type="password"
                minLength={8}
                pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
                onChange={handleChange}
              />
            </div>
            <div>
              {" "}
              <InputComponent
                placeholder={"Confirmar Password *"}
                id="passwordInput2"
                required
                name="password2"
                type="password"
                minLength={8}
                pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
                onChange={handleChange}
              />
            </div>

            <div className="row mb-2">
              {/*  <span className="form-text">
                A senha deve ter entre 6 a 16 caracteres //{" "}
              </span> */}
              <div className="row mt-3">
                <ButtonComponent name={"Alterar"} type={"submit"} />
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>

    // <Container fluid>
    //   <Row>
    //     <MainTitle />
    //   </Row>
    //   <Row>
    //     <SecondTitle name={"Alterar a Password"} />
    //   </Row>
    //   <Row className="mt-5">
    //     <Col className="d-flex justify-content-around">
    //       <Form onSubmit={handleSubmit}>
    //         <Row>
    //           <Col>
    //             <InputComponent
    //               placeholder={"Password *"}
    //               id="passwordInput"
    //               required
    //               name="password"
    //               type="password"
    //               minLength={8}
    //               pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
    //               onChange={handleChange}
    //             />
    //           </Col>
    //           <Col>
    //             {" "}
    //             <InputComponent
    //               placeholder={"Confirmar Password *"}
    //               id="passwordInput2"
    //               required
    //               name="password2"
    //               type="password"
    //               minLength={8}
    //               pattern="^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$"
    //               onChange={handleChange}
    //             />
    //           </Col>
    //         </Row>
    //         <span className="form-text">
    //           {" "}
    //           A senha deve ter entre 6 a 16 caracteres
    //         </span>
    //         <ButtonComponent type="submit" name={"Alterar password"} />
    //       </Form>
    //     </Col>
    //   </Row>
    //   <Row>
    //     <Footer />
    //   </Row>
    // </Container>
  );
}

export default ChangePassword;
