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
  const [showR, setShowR] = useState(false);
  const [showA, setShowA] = useState(false);
  const user = userStore((state) => state.user);
  const navigate = useNavigate();
  const updateUser = userStore((state) => state.updateUser);
  const { id } = useParams();
  const handleCloseA = () => setShowA(false);
  const handleShowA = () => setShowA(true);
  const handleCloseR = () => setShowR(false);
  const handleShowR = () => setShowR(true);
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
      <div className="container">
        <div className="row ">
          <h3 className="bg-white mt-5 text-center rounded-5 mb-3 ">
            Candidaturas pendentes
          </h3>
          <div className="row overflow-auto " style={{ maxHeight: "50vh" }}>
            {pendingApplications.map((application, index) => (
              <div
                key={index}
                className="row w-75 bg-white text-black mb-2 rounded-3  mx-auto  p-2 "
              >
                <div className="col-lg-6 ">
                  <h5 className="text-center">{application.projectTitle}</h5>
                </div>
                <div className="col-lg-2 ">
                  <OverlayTrigger
                    placement="top"
                    overlay={<Tooltip>Aceitar candidatura</Tooltip>}
                  >
                    <span data-bs-toggle="tooltip" data-bs-placement="top">
                      {" "}
                      <BsCheck2
                        size={30}
                        color="green"
                        onClick={handleShowA}
                      />{" "}
                    </span>
                  </OverlayTrigger>
                  <Modal
                    show={showA}
                    onHide={handleCloseA}
                    backdrop="static"
                    keyboard={false}
                    size="lg"
                  >
                    <Modal.Header closeButton>
                      <Modal.Title>
                        Aceitar candidatura do projecto{" "}
                        {application.projectTitle}
                      </Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                      <p>
                        Tem a certeza que quer aceitar este projecto a concurso?
                        Uma vez confirmada esta operação não a poderá reverter.{" "}
                      </p>
                      <p>Clique no botão Confirmar para aceitar candidatura</p>
                    </Modal.Body>
                    <Modal.Footer id="modalFooter">
                      <Col xs={4} className="closeBtnSeeTask">
                        <Button variant="secondary" onClick={handleCloseA}>
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

                <div className="col-lg-2 ">
                  <OverlayTrigger
                    placement="top"
                    overlay={<Tooltip>Recusar candidatura</Tooltip>}
                  >
                    <span data-bs-toggle="tooltip" data-bs-placement="top">
                      {" "}
                      <BsXLg
                        size={30}
                        color="red"
                        onClick={handleShowR}
                        //  onClick={() => handleApplication(0, application.id)}
                      />
                    </span>
                  </OverlayTrigger>
                  <Modal
                    show={showR}
                    onHide={handleCloseR}
                    backdrop="static"
                    keyboard={false}
                    size="lg"
                  >
                    <Modal.Header closeButton>
                      <Modal.Title>
                        Recusar candidatura do projecto{" "}
                        {application.projectTitle}
                      </Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                      <p>
                        Tem a certeza que quer recusar este projecto a concurso?
                        Uma vez confirmada esta operação não a poderá reverter.
                      </p>
                      <p>Clique no botão Confirmar para recusar candidatura</p>
                    </Modal.Body>
                    <Modal.Footer id="modalFooter">
                      <Col xs={4} className="closeBtnSeeTask">
                        <Button variant="secondary" onClick={handleCloseR}>
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
                <div className="col-lg-2">
                  <Link to={`/home/projects/${application.projectId}`}>
                    <OverlayTrigger
                      placement="top"
                      overlay={<Tooltip>Ver projecto</Tooltip>}
                    >
                      <span data-bs-toggle="tooltip" data-bs-placement="top">
                        {" "}
                        <BsEyeFill
                          size={25}
                          color="black"
                          //onClick={() => handleSeeProfile(item.id)}
                        />
                      </span>
                    </OverlayTrigger>
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }
}

export default ContestApplications;
