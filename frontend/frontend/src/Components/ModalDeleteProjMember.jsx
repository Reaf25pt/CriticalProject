import React from "react";

import { useState } from "react";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { BsArrowDown, BsSearch, BsXLg } from "react-icons/bs";
import { projOpenStore } from "../stores/projOpenStore";
import { toast, Toaster } from "react-hot-toast";

import { userStore } from "../stores/UserStore";
import Modal from "react-bootstrap/Modal";

function ModalDeleteProjMember({ member }) {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const project = projOpenStore((state) => state.project);
  const setMembers = projOpenStore((state) => state.setMembers);

  const handleSubmit = (event) => {
    event.preventDefault();

    /*   const id = hobby.id; */

    fetch("http://localhost:8080/projetofinal/rest/project/member", {
      method: "PATCH",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
        userId: member.userInvitedId,
        projId: project.id,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          toast.success("Membro removido");

          return response.json();
        } else {
          throw new Error("Pedido não satisfeito");
        }
      })
      .then((data) => {
        setMembers(data);
      })
      .catch((error) => {
        toast.error(error.message);
      });
    handleClose();
  };

  return (
    <>
      <OverlayTrigger
        placement="top"
        overlay={<Tooltip>Remover membro</Tooltip>}
      >
        <span data-bs-toggle="tooltip" data-bs-placement="top">
          {" "}
          <BsXLg onClick={handleShow} size={25} color="red" />
        </span>
      </OverlayTrigger>

      <Modal
        show={show}
        onHide={handleClose}
        backdrop="static"
        keyboard={false}
      >
        <Modal.Header closeButton>
          <Toaster position="top-right" />

          <Modal.Title>
            Remover membro do projecto
            {/* <FormattedMessage
              id="deleteTaskDetailModalTitle.tooltip"
              defaultMessage="Apagar actividade"
            /> */}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            {" "}
            Ao confirmar, está a remover o membro{" "}
            {/* <FormattedMessage
              id="deleteTaskModalText1.tooltip"
              defaultMessage="Ao confirmar, está a apagar a actividade"
            />{" "} */}
            <span className="name">
              {member.userInvitedFirstName} {member.userInvitedLastName}
            </span>
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
              Remover
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

export default ModalDeleteProjMember;
