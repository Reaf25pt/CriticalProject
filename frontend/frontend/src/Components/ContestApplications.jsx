import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "./ModalDeleteProjMember";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import { useNavigate } from "react-router-dom";
import { BsStarFill } from "react-icons/bs";
import { BsEyeFill, BsCheck2, BsXLg } from "react-icons/bs";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { Link, useParams } from "react-router-dom";
import { contestOpenStore } from "../stores/ContestOpenStore";
import Modal from "react-bootstrap/Modal";

import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";

function ContestApplications() {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const navigate = useNavigate();
  const updateUser = userStore((state) => state.updateUser);
  const { id } = useParams();
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const setProjList = contestOpenStore((state) => state.setProjectList);
  const projList = contestOpenStore((state) => state.projectList);
  const pendingApplications = projList.filter((item) => !item.answered);

  function handleApplication(status, applicationId) {
    // var status;

    fetch("http://localhost:8080/projetofinal/rest/contest/application", {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
        answer: status,
        applicationId: applicationId,
        contestId: id,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          alert("candidatura respondida");
          return response.json();

          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
          throw new Error("Request failed");
        }
      })
      .then((data) => {
        setProjList(data);
      })
      .catch((err) => console.log(err));
  }

  if (pendingApplications.length === 0 && user.contestManager) {
    return (
      <div className="row mt-5">
        <h5 className="text-white">Não há candidaturas à espera de resposta</h5>
      </div>
    );
  } else if (user.contestManager) {
    return (
      <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
        <div>
          <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
            Candidaturas pendentes
          </h3>
          {pendingApplications.map((application, index) => (
            <div
              key={index}
              className="row bg-white text-black mb-3 rounded-3 w-50 mx-auto align-items-center"
            >
              <div className="col-lg-6 ">{application.projectTitle}</div>
              <div className="col-lg-6 ">
                <OverlayTrigger
                  placement="top"
                  overlay={<Tooltip>Aceitar candidatura</Tooltip>}
                >
                  <span data-bs-toggle="tooltip" data-bs-placement="top">
                    {" "}
                    <BsCheck2
                      size={30}
                      color="green"
                      onClick={handleShow}
                    />{" "}
                  </span>
                </OverlayTrigger>
                <Modal
                  show={show}
                  onHide={handleClose}
                  backdrop="static"
                  keyboard={false}
                  size="lg"
                >
                  <Modal.Header closeButton>
                    <Modal.Title>
                      Aceitar candidatura do projecto {application.projectTitle}
                    </Modal.Title>
                  </Modal.Header>
                  <Modal.Body>
                    <p>
                      Tem a certeza que quer aceitar este projecto a concurso?
                      Uma vez confirmada esta operação não a poderá reverter.
                      Clique no botão Confirmar para aceitar candidatura
                    </p>
                  </Modal.Body>
                  <Modal.Footer id="modalFooter">
                    <Col xs={4} className="closeBtnSeeTask">
                      <Button variant="secondary" onClick={handleClose}>
                        Fechar
                      </Button>
                    </Col>
                    <Col xs={4}>
                      <Button
                        onClick={() => handleApplication(1, application.id)}
                        className="button"
                        type="submit"
                        variant="outline-primary"
                      >
                        {" "}
                        Confirmar
                      </Button>
                    </Col>
                  </Modal.Footer>
                </Modal>
              </div>

              <div className="col-lg-6 ">
                <OverlayTrigger
                  placement="top"
                  overlay={<Tooltip>Recusar candidatura</Tooltip>}
                >
                  <span data-bs-toggle="tooltip" data-bs-placement="top">
                    {" "}
                    <BsXLg
                      size={30}
                      color="red"
                      onClick={handleShow}
                      //  onClick={() => handleApplication(0, application.id)}
                    />
                  </span>
                </OverlayTrigger>
                <Modal
                  show={show}
                  onHide={handleClose}
                  backdrop="static"
                  keyboard={false}
                  size="lg"
                >
                  <Modal.Header closeButton>
                    <Modal.Title>
                      Recusar candidatura do projecto {application.projectTitle}
                    </Modal.Title>
                  </Modal.Header>
                  <Modal.Body>
                    <p>
                      Tem a certeza que quer recusar este projecto a concurso?
                      Uma vez confirmada esta operação não a poderá reverter.
                      Clique no botão Confirmar para recusar candidatura
                    </p>
                  </Modal.Body>
                  <Modal.Footer id="modalFooter">
                    <Col xs={4} className="closeBtnSeeTask">
                      <Button variant="secondary" onClick={handleClose}>
                        Fechar
                      </Button>
                    </Col>
                    <Col xs={4}>
                      <Button
                        onClick={() => handleApplication(0, application.id)}
                        className="button"
                        type="submit"
                        variant="outline-primary"
                      >
                        {" "}
                        Confirmar
                      </Button>
                    </Col>
                  </Modal.Footer>
                </Modal>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }
}

export default ContestApplications;
