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
import { projOpenStore } from "../stores/projOpenStore";

import { userStore } from "../stores/UserStore";
import Modal from "react-bootstrap/Modal";

function ModalCancelProject() {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const [credentials, setCredentials] = useState();
  const { id } = useParams();
  const setProject = projOpenStore((state) => state.setProjOpen);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    var status = 5;

    fetch("http://localhost:8080/projetofinal/rest/project/status", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
        status: status,
        projId: id,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
        } else {
          throw new Error("Pedido não satisfeito");
        }
      })
      .then((data) => {
        setProject(data);
        handleClose();
        // toast.success("Papel alterado");
      })
      .catch((error) => {
        alert(error.message);
      });
  };

  return (
    <>
      <div className="row mx-auto justify-content-around mt-5">
        <div className="col-lg-12">
          <ButtonComponent
            onClick={handleShow}
            type="button"
            name="Cancelar projecto"
          />
        </div>
      </div>

      <Modal
        show={show}
        onHide={handleClose}
        backdrop="static"
        keyboard={false}
        size="lg"
      >
        <Modal.Header closeButton>
          <Modal.Title>
            Cancelar projecto
            {/* <FormattedMessage
              id="deleteTaskDetailModalTitle.tooltip"
              defaultMessage="Apagar actividade"
            /> */}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Ao confirmar, o projecto será cancelado. </p>
          <p>
            Se o projecto foi aprovado para participar num concurso, já não
            poderá ser reactivado.{" "}
          </p>
          <p>
            Caso contrário, o projecto pode voltar a ser activado se, há data da
            reactivação, os seus membros não tiverem nenhum projecto activo nem
            tiverem permissão de gestor de concurso.
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

export default ModalCancelProject;
