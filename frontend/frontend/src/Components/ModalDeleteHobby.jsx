import React from "react";

import { useState } from "react";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { BsArrowDown, BsSearch, BsXLg } from "react-icons/bs";

import { userStore } from "../stores/UserStore";
import Modal from "react-bootstrap/Modal";

function ModalDeleteHobby({ hobby, set, setS }) {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const handleSubmit = (event) => {
    event.preventDefault();

    const id = hobby.id;

    fetch("http://localhost:8080/projetofinal/rest/user/hobby", {
      method: "DELETE",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
      },
      body: id,
    }).then((response) => {
      if (response.status === 200) {
        set([]); // reset a lista da pagina de users para actualizar
        setS([]);
        handleClose();
      } else if (response.status === 403) {
        alert("Não tem autorização para efectuar este pedido");
        /*  } else if (response.status === 404) {
        alert("Actividade não encontrada"); */
      } else {
        alert("Algo correu mal");
      }
    });
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
            Apagar
          </Tooltip>
        }
      >
        {/*  <FontAwesomeIcon onClick={handleShow} icon={faTrash} /> */}
        <BsXLg onClick={handleShow} />
        {/*  <FontAwesomeIcon onClick={handleShow} icon={faToggleOn} /> */}
      </OverlayTrigger>

      <Modal
        show={show}
        onHide={handleClose}
        backdrop="static"
        keyboard={false}
      >
        <Modal.Header closeButton>
          <Modal.Title>
            Apagar interesse
            {/* <FormattedMessage
              id="deleteTaskDetailModalTitle.tooltip"
              defaultMessage="Apagar actividade"
            /> */}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            {" "}
            Ao confirmar, está a apagar o interesse{" "}
            {/* <FormattedMessage
              id="deleteTaskModalText1.tooltip"
              defaultMessage="Ao confirmar, está a apagar a actividade"
            />{" "} */}
            <span className="name">{hobby.title}</span>
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

export default ModalDeleteHobby;
