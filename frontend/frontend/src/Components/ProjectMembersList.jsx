import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "../Components/ModalDeleteProjMember";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "../Components/InputComponent";
import { useNavigate } from "react-router-dom";
import { BsStarFill } from "react-icons/bs";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { projOpenStore } from "../stores/projOpenStore";
import { toast, Toaster } from "react-hot-toast";
import Modal from "react-bootstrap/Modal";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";

function ProjectMembersList() {
  const user = userStore((state) => state.user);
  const navigate = useNavigate();
  const updateUser = userStore((state) => state.updateUser);
  const project = projOpenStore((state) => state.project);
  const clearProject = projOpenStore((state) => state.clearProject);
  const members = projOpenStore((state) => state.members);
  const setMembers = projOpenStore((state) => state.setMembers);
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const handleRemove = (event) => {
    event.preventDefault();

    /*   const id = hobby.id; */

    fetch("http://localhost:8080/projetofinal/rest/project/member", {
      method: "PATCH",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
        userId: user.userId,
        projId: project.id,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          updateUser("noActiveProject", true);
          navigate("/home/start", { replace: true });
          clearProject();
        } else {
          alert("Pedido não satisfeito");
          handleClose();
        }
      })
      .catch((error) => {
        alert(error.message);
      });
  };

  const handleRole = (event, id) => {
    //event.preventDefault();

    var role;
    if (event) {
      role = 1;
    } else {
      role = 0;
    }

    fetch("http://localhost:8080/projetofinal/rest/project/member", {
      method: "PUT",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
        userId: id,
        projId: project.id,
        role: role,
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
        setMembers(data);
        // toast.success("Papel alterado");
      })
      .catch((error) => {
        toast.error(error.message);
      });
  };

  return (
    <div className="container">
      <Toaster position="top-right" />
      <div>
        <h3 className="bg-white mt-5 text-center rounded-5 mb-3 ">
          Membros do Projetos
        </h3>
        <div className="row overflow-auto" style={{ maxHeight: "50vh" }}>
          {members.map((member, index) => (
            <div
              key={index}
              className="row w-75 bg-white text-black mb-3 rounded-3  mx-auto align-items-center p-2"
            >
              <div className="col-lg-2 ">
                {member.userInvitedPhoto === null ? (
                  <img
                    src="https://static-00.iconduck.com/assets.00/user-avatar-icon-512x512-vufpcmdn.png"
                    class="rounded-circle img-responsive"
                    width={"40px"}
                    height={"40px"}
                    alt="avatar"
                  />
                ) : (
                  <img
                    src={member.userInvitedPhoto}
                    class="rounded-circle img-responsive"
                    width={"40px"}
                    height={"40px"}
                    alt=""
                  />
                )}
              </div>
              <div className="col-lg-6 ">
                {member.userInvitedFirstName} {member.userInvitedLastName}
              </div>
              {project.manager &&
              (project.statusInt === 0 || project.statusInt === 4) ? (
                <>
                  {user.userId !== member.userInvitedId ? (
                    <div className="col-lg-1">
                      <ModalDeleteProjMember member={member} />
                    </div>
                  ) : (
                    <div className="col-lg-1"></div>
                  )}
                  <div className="col-lg-1">
                    {member.manager ? (
                      <>
                        <div class="form-check form-switch">
                          <OverlayTrigger
                            placement="top"
                            overlay={
                              <Tooltip>Retirar permissão de gestor</Tooltip>
                            }
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <input
                                class="form-check-input bg-secondary"
                                type="checkbox"
                                id="flexSwitchCheckChecked"
                                defaultChecked
                                onClick={(event) =>
                                  handleRole(
                                    event.target.checked,
                                    member.userInvitedId
                                  )
                                }
                              />{" "}
                            </span>
                          </OverlayTrigger>

                          <label
                            class="form-check-label"
                            for="flexSwitchCheckChecked"
                          >
                            <OverlayTrigger
                              placement="top"
                              overlay={<Tooltip>Gestor</Tooltip>}
                            >
                              <span
                                data-bs-toggle="tooltip"
                                data-bs-placement="top"
                              >
                                {" "}
                                <BsStarFill color="#c09617" size={20} />
                              </span>
                            </OverlayTrigger>
                          </label>
                        </div>
                      </>
                    ) : (
                      <>
                        <div class="form-check form-switch">
                          <OverlayTrigger
                            placement="top"
                            overlay={<Tooltip>Tornar gestor</Tooltip>}
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <input
                                class="form-check-input "
                                type="checkbox"
                                id="flexSwitchCheckDefault"
                                onClick={(event) =>
                                  handleRole(
                                    event.target.checked,
                                    member.userInvitedId
                                  )
                                }
                              />
                            </span>
                          </OverlayTrigger>

                          <label
                            class="form-check-label"
                            for="flexSwitchCheckDefault"
                          ></label>
                        </div>
                      </>
                    )}
                  </div>
                </>
              ) : null}
            </div>
          ))}
        </div>

        <div className="row mt-4 ">
          <div className="row mt-4">
            {project.member &&
            members.length > 1 &&
            (project.statusInt === 0 || project.statusInt === 4) ? (
              <div className="col-lg-6 mx-auto mb-4">
                <ButtonComponent
                  // onClick={handleRemove}
                  onClick={handleShow}
                  name={"Sair do projecto"}
                ></ButtonComponent>
              </div>
            ) : null}
          </div>
          <Modal
            show={show}
            onHide={handleClose}
            backdrop="static"
            keyboard={false}
            size="lg"
          >
            <Modal.Header closeButton>
              <Modal.Title>Sair do projecto</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <p>
                Tem a certeza que quer deixar de participar neste projecto? Uma
                vez confirmada esta operação não a poderá reverter.
              </p>
              <p>Clique no botão Confirmar para prosseguir</p>
            </Modal.Body>
            <Modal.Footer id="modalFooter">
              <Col xs={4} className="closeBtnSeeTask">
                <Button variant="secondary" onClick={handleClose}>
                  Fechar
                </Button>
              </Col>
              <Col xs={4}>
                <Button
                  onClick={handleRemove}
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
    </div>
  );
}

export default ProjectMembersList;
