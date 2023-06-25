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

import { userStore } from "../stores/UserStore";
import Modal from "react-bootstrap/Modal";

function ModalFinalTask({ setProjects }) {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const [credentials, setCredentials] = useState();
  const { id } = useParams();

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    console.log(credentials);

    if (
      !credentials.startDate ||
      !credentials.details ||
      !credentials.taskOwnerId ||
      credentials.taskOwnerId === "-1"
    ) {
      alert("Insira os dados em falta");
    } else {
      var finalTask = credentials;
      var status = 1;

      fetch("http://localhost:8080/projetofinal/rest/project/status", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
          status: status,
          projId: id,
        },
        body: JSON.stringify(finalTask),
      }).then((response) => {
        if (response.status === 200) {
          console.log(response);
          setProjects([]);
          alert("Status alterado");

          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
      });
    }

    /* fetch(`http://localhost:8080/projetofinal/rest/project/${id}/task`, {
      method: "PATCH",
      headers: {
        Accept: "**",
        "Content-Type": "application/json",
        token: user.token,
      },
      body: JSON.stringify(editedTask),
    }).then((response) => {
      if (response.status === 200) {
        handleClose();
      } else if (response.status === 403) {
        alert("Não tem autorização para efectuar este pedido");
        /*  } else if (response.status === 404) {
        alert("Actividade não encontrada"); */
    /*  } else {
        alert("Algo correu mal");
      }
    });*/
  };

  return (
    <>
      <OverlayTrigger
        placement="top"
        overlay={
          <Tooltip /* defaultMessage="Apagar" */ id="userHobbyDeleteTooltip">
            {/* <FormattedMessage
                id="deleteTaskDetail.tooltip"
                defaultMessage="Apagar"
              /> */}
            {/*    Editar tarefa */}
          </Tooltip>
        }
      >
        {/*  <FontAwesomeIcon onClick={handleShow} icon={faTrash} /> */}

        <div className="row mx-auto justify-content-around mt-5">
          <div className="col-lg-12">
            <ButtonComponent
              onClick={handleShow}
              type="button"
              name="Mudar status: ready"
              // onClick={() => handleProjectStatus(1)}
            />
          </div>
        </div>
        {/*  <BsXLg onClick={handleShow} /> */}
        {/*  <FontAwesomeIcon onClick={handleShow} icon={faToggleOn} /> */}
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
            Alterar estado do projecto para READY
            {/* <FormattedMessage
              id="deleteTaskDetailModalTitle.tooltip"
              defaultMessage="Apagar actividade"
            /> */}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            Para alterar o estado do projecto com sucesso tem de definir uma
            tarefa final com a duração de 1 dia, que diz respeito à apresentação
            final do projecto nos dias finais do concurso. Tenha atenção para a
            data inserida: uma data posterior à conclusão do concurso a que
            pretende concorrer comprometerá a sua candidatura.{" "}
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
                      <InputComponent
                        placeholder={"Apresentação final"}
                        id="title"
                        disabled
                        name="title"
                        type="text"
                        //onChange={handleChange}
                        //defaultValue={task.title}
                      />
                    </div>
                    <div className="col-lg-6">
                      <ProjectMembersSelect
                        name="taskOwnerId"
                        id="taskOwnerId"
                        onChange={handleChange}
                        placeholder={"Membro responsável *"}
                        local={"Membro responsável *"}
                        //  listMembers={listMembers}
                        projId={id}
                      />
                    </div>
                  </div>
                  <div className="row mb-3">
                    <div className="col-lg-6">
                      <label className="text-white">
                        Data de apresentação *
                      </label>
                      <InputComponent
                        placeholder={" *"}
                        id="startDate"
                        required
                        name="startDate"
                        type="date"
                        onChange={handleChange}
                        // defaultValue={formatDate(task.startDate)}
                      />
                    </div>
                    {/*   <div className="col-lg-6">
                      <label className="text-white">Data de fim *</label>

                      <InputComponent
                        placeholder={" *"}
                        id="finishDate"
                        required
                        name="finishDate"
                        type="date"
                        onChange={handleChange}
                      />
                    </div> */}
                  </div>
                </div>
              </div>
            </div>
            <div class="col-lg-6 d-flex align-items-center ">
              <TextAreaComponent
                placeholder={"Descrição da tarefa *"}
                id="details"
                name="details"
                required
                type="text"
                onChange={handleChange}
              />
              {/*    <textarea
              class="text-dark bg-white rounded-2 w-100 h-75 "
              placeholder="Descrição da Tarefa"
              name="bio"
              type="text"
            ></textarea> */}
            </div>{" "}
            {/*  <div className="col-lg-1 d-flex align-items-center">
              <ButtonComponent
                name={"Editae tarefa"}
                onClick={handleSubmit}
              />
            </div> */}
          </div>
        </Modal.Body>
        <Modal.Footer id="modalFooter">
          <Col xs={4} className="closeBtnSeeTask">
            <Button variant="secondary" onClick={handleClose}>
              Fechar
              {/* <FormattedMessage
                id="closeModal.button"
                defaultMessage="Fechar"
              /> */}
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
              {/*  <FormattedMessage
                id="confirmChangeModal.button"
                defaultMessage="Confirmar alteração"
              /> */}
            </Button>
          </Col>
        </Modal.Footer>
      </Modal>
    </>
  );
}

export default ModalFinalTask;
