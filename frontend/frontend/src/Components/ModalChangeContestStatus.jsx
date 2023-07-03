import React from "react";
import { useParams } from "react-router-dom";

import { useState } from "react";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { BsArrowDown, BsSearch, BsXLg } from "react-icons/bs";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";
import ProjectMembersSelect from "./ProjectMembersSelect";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";
import { BsFillPencilFill } from "react-icons/bs";
import { contestOpenStore } from "../stores/ContestOpenStore";

import { userStore } from "../stores/UserStore";
import Modal from "react-bootstrap/Modal";

function ModalChangeContestStatus() {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const { id } = useParams();
  const projList = contestOpenStore((state) => state.projectList);
  const contest = contestOpenStore((state) => state.contest);
  const setContestOpen = contestOpenStore((state) => state.setContestOpen);

  const handleStatus = (event) => {
    // event.preventDefault();
    var status;

    if (event === 1) {
      status = 1;
    } else if (event === 2) {
      status = 2;
    } else if (event === 3) {
      status = 3;
    }

    fetch("http://localhost:8080/projetofinal/rest/contest/status", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
        status: status,
        contestId: contest.id,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          alert("Status alterado");
          return response.json();
          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
          throw new Error("Algo correu mal");
        }
      })
      .then((data) => {
        setContestOpen(data);
      })
      .catch((err) => console.log(err));
  };

  return (
    <>
      {contest.statusInt === 0 ? (
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip id="userHobbyDeleteTooltip"></Tooltip>}
        >
          <div className="row mx-auto justify-content-around mt-5">
            <div className="col-lg-12">
              <ButtonComponent
                onClick={handleShow}
                type="button"
                name="Abrir candidaturas: Open"
              />
            </div>
          </div>
        </OverlayTrigger>
      ) : contest.statusInt === 1 ? (
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip id="userHobbyDeleteTooltip"></Tooltip>}
        >
          <div className="row mx-auto justify-content-around mt-5">
            <div className="col-lg-12">
              <ButtonComponent
                onClick={handleShow}
                type="button"
                name="Fechar candidaturas: Ongoing"
              />
            </div>
          </div>
        </OverlayTrigger>
      ) : null}

      <Modal
        show={show}
        onHide={handleClose}
        backdrop="static"
        keyboard={false}
        size="lg"
      >
        <Modal.Header closeButton>
          {contest.statusInt === 0 ? (
            <Modal.Title>Abrir candidadaturas a concurso</Modal.Title>
          ) : contest.statusInt === 1 ? (
            <Modal.Title>
              Fechar candidaturas a concurso e iniciar a fase Ongoing
            </Modal.Title>
          ) : null}
        </Modal.Header>
        <Modal.Body>
          {contest.statusInt === 0 ? (
            <p>
              Confirme para alterar o estado do concurso para OPEN. Uma vez
              concluída a operação com sucesso não poderá reverter para o estado
              PLANNING nem editar as informações do concurso
            </p>
          ) : contest.statusInt === 1 ? (
            <p>
              Confirme para alterar o estado do concurso para ONGOING. Uma vez
              concluída a operação com sucesso não poderá reverter para o estado
              OPEN nem responder a candidaturas de projectos
            </p>
          ) : null}
        </Modal.Body>
        <Modal.Footer id="modalFooter">
          <Col xs={4} className="closeBtnSeeTask">
            <Button variant="secondary" onClick={handleClose}>
              Fechar
            </Button>
          </Col>
          {contest.statusInt === 0 ? (
            <Col xs={4}>
              <Button
                onClick={() => handleStatus(1)}
                className="button"
                type="submit"
                variant="outline-primary"
              >
                {" "}
                Confirmar
              </Button>
            </Col>
          ) : contest.statusInt === 1 ? (
            <Col xs={4}>
              <Button
                onClick={() => handleStatus(2)}
                className="button"
                type="submit"
                variant="outline-primary"
              >
                {" "}
                Confirmar
              </Button>
            </Col>
          ) : null}
        </Modal.Footer>
      </Modal>
    </>
  );
}

export default ModalChangeContestStatus;
