import ButtonComponent from "../Components/ButtonComponent";

import { Link, useNavigate, useParams } from "react-router-dom";
import logo from "../images/logo-criticalsoftware.png";
import { toast, Toaster } from "react-hot-toast";

function ActivateAccount() {
  const { token } = useParams();
  const navigate = useNavigate();

  const handleClick = (event) => {
    event.preventDefault();

    fetch("http://localhost:8080/projetofinal/rest/user/accountvalidation", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",

        tokenForValidation: token,
      },
    }).then((response) => {
      if (response.status === 200) {
        alert("Conta activada com sucesso");
        navigate("/", { replace: true });
      } else if (response.status === 400) {
        toast.error("O link expirou. Receberá um novo link no email");
      } else {
        toast.error("Pedido não satisfeito");
      }
    });
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
          <form className=" d-flex flex-column bg-white p-5 rounded-5 ">
            <div className="row d-flex justify-content-around h-100 mb-3 ">
              <div className="col-lg-12 text-dark d-flex align-items-center">
                <h1 className="p-0 text-center ">
                  Bem-vindo ao Laboratório da Inovação.
                </h1>
              </div>
              <div className="row mb-4">
                <div className="col-lg-12 d-flex  justify-content-center">
                  <img className="" src={logo} width={200} height={200} />
                </div>
                <h3>Clique no botão para ativar a sua conta.</h3>
              </div>
            </div>

            <div className="row mb-2 d-flex  justify-content-center">
              <div className="col-6 mb-2 ">
                <ButtonComponent
                  name={"Ativar conta"}
                  type="button"
                  onClick={handleClick}
                />{" "}
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
    //     <p style={{ marginTop: "150px", textAlign: "center" }}>
    //       Está a um passo de poder aceder à aplicação Laboratório de Inovação da
    //       Critical Software. Clique no botão para activar a sua conta.
    //     </p>
    //   </Row>
    //   <Row className="mt-5">
    //     <ButtonComponent
    //       name={"Ativar conta"}
    //       type="button"
    //       onClick={handleClick}
    //     />
    //   </Row>
    //   <Row>
    //     <Footer />
    //   </Row>
    // </Container>
  );
}

export default ActivateAccount;
