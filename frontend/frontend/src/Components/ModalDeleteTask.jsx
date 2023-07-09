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
import { BsFillTrashFill } from "react-icons/bs";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";

import { userStore } from "../stores/UserStore";
import Modal from "react-bootstrap/Modal";
import ModalEditTask from "./ModalEditTask";

function ModalDeleteTask({ task, set, setTriggerList }) {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const { id } = useParams();

  const handleSubmit = (event) => {
    event.preventDefault();

    fetch(`http://localhost:8080/projetofinal/rest/project/task`, {
      method: "DELETE",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
        projId: id,
        taskId: task.id,
      },
    }).then((response) => {
      if (response.status === 200) {
        set([]); // reset a lista da pagina de tasks para actualizar
        setTriggerList(""); // para actualizar lista de tarefas a apresentar no adicionar tarefa
        handleClose();
      } else if (response.status === 403) {
        alert("Não tem autorização para efectuar este pedido");
        /*  } else if (response.status === 404) {
        alert("Actividade não encontrada"); */
      } else {
        alert("Algo correu mal");
      }
    });
    handleClose();
  };

  return (
    <>
      <OverlayTrigger placement="top" overlay={<Tooltip>Apagar</Tooltip>}>
        <span data-bs-toggle="tooltip" data-bs-placement="top">
          {" "}
          <BsFillTrashFill onClick={handleShow} size={30} color="white" />
        </span>
      </OverlayTrigger>

      <Modal
        show={show}
        onHide={handleClose}
        backdrop="static"
        keyboard={false}
        size="xl"
      >
        <Modal.Header closeButton>
          <Modal.Title>
            Eliminar tarefa
            {/* <FormattedMessage
              id="deleteTaskDetailModalTitle.tooltip"
              defaultMessage="Apagar actividade"
            /> */}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            {" "}
            Só é permitido eliminar uma tarefa que não tenha associação com
            nenhuma outra tarefa. Por favor, garanta que não há nenhuma tarefa
            precedente ou que a tarefa a eliminar não seja requisito obrigatório
            para que outra possa ser executada. Ao confirmar, está a eliminar a
            tarefa{" "}
            {/* <FormattedMessage
              id="deleteTaskModalText1.tooltip"
              defaultMessage="Ao confirmar, está a apagar a actividade"
            />{" "} */}
            <span className="name">{task.title}</span>
            {/*  <FormattedMessage
              id="deleteTaskModalText2.tooltip"
              defaultMessage=" e todos os seus detalhes. \n\n  Não será possível recuperar esta informação"
            /> */}
          </p>
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
              Apagar
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

export default ModalDeleteTask;
