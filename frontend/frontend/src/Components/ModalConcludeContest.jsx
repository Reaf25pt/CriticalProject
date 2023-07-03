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

function ModalConcludeContest() {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const [credentials, setCredentials] = useState("-1");
  const { id } = useParams();
  const projList = contestOpenStore((state) => state.projectList);
  const setContestOpen = contestOpenStore((state) => state.setContestOpen);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    console.log(credentials.projectWinner);

    if (
      !credentials ||
      !credentials.projectWinner ||
      credentials.projectWinner === "-1"
    ) {
      alert("Escolha um projecto vencedor");
    } else {
      fetch("http://localhost:8080/projetofinal/rest/contest/application", {
        method: "PUT",
        headers: {
          Accept: "*/*",
          "Content-Type": "application/json",
          token: user.token,
          contestId: id,
          projId: credentials.projectWinner,
        },
      })
        .then((response) => {
          if (response.status === 200) {
            alert("Vencedor declarado");
            return response.json();
            //navigate("/home", { replace: true });
          } else {
            alert("Algo correu mal");
            throw new Error("Request failed");
          }
        })
        .then((data) => {
          setContestOpen(data);
        })
        .catch((err) => console.log(err));
    }
  };

  return (
    <>
      <OverlayTrigger
        placement="top"
        overlay={<Tooltip id="userHobbyDeleteTooltip"></Tooltip>}
      >
        <div className="row mx-auto justify-content-around mt-5">
          <div className="col-lg-12">
            <ButtonComponent
              onClick={handleShow}
              type="button"
              name="Terminar concurso"
            />
          </div>
        </div>
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
            Escolher projecto vencedor e terminar concurso
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            Seleccione o projecto vencedor para dar por concluído o concurso.
            Atenção, uma vez concluída a operação com sucesso não poderá
            reverter nem alterar detalhes do concurso.
          </p>

          <div
            className="row d-flex justify-content-around bg-secondary 
          rounded-5 p-4"
          >
            <div className="col-lg-4">
              <div className="row ">
                <div className="col-lg-12 ">
                  <div className="row mb-3">
                    <div className="col-lg-6">
                      <div className="arrow-select-container">
                        <select
                          name="projectWinner"
                          id="projectWinner"
                          onChange={handleChange}
                          required
                          placeholder="Projectos"
                          className="form-control"
                        >
                          <option value="-1">{"Projectos"} </option>
                          {/*  {Object.entries(props.listMembers).map(([key, member]) => ( */}
                          {projList
                            .filter(
                              (proj) =>
                                proj.accepted /* && proj.projectStatusInt === 6 */
                            )
                            .map((proj) => (
                              <option key={proj.id} value={proj.projectId}>
                                {proj.projectTitle}
                              </option>
                            ))}
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer id="modalFooter">
          <Col xs={4} className="closeBtnSeeTask">
            <Button variant="secondary" onClick={handleClose}>
              Fechar
            </Button>
          </Col>
          <Col xs={4}>
            <Button
              onClick={handleSubmit}
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
    </>
  );
}

export default ModalConcludeContest;
