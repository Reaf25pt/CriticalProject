import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "../Components/ModalDeleteProjMember";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "../Components/InputComponent";
import { useNavigate } from "react-router-dom";
import { BsStarFill } from "react-icons/bs";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

function ProjectMembersList({ showMembers, showProjects, setMembers }) {
  const user = userStore((state) => state.user);
  const navigate = useNavigate();
  const updateUser = userStore((state) => state.updateUser);

  useEffect(() => {
    console.log(showMembers);
  }, []);

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
        projId: showProjects.id,
      },
    }).then((response) => {
      if (response.status === 200) {
        updateUser("noActiveProject", true);

        navigate("/home", { replace: true });
      } else if (response.status === 403) {
        alert("Não tem autorização para efectuar este pedido");
        /*  } else if (response.status === 404) {
        alert("Actividade não encontrada"); */
      } else {
        alert("Algo correu mal");
      }
    });
  };

  const handleRole = (event, id) => {
    //event.preventDefault();

    console.log(event + " " + id); //true - 1 - membro para gestor , false - 0 - gestor para membro
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
        projId: showProjects.id,
        role: role,
      },
    }).then((response) => {
      if (response.status === 200) {
      } else if (response.status === 403) {
        alert("Não tem autorização para efectuar este pedido");
        /*  } else if (response.status === 404) {
        alert("Actividade não encontrada"); */
      } /*  else {
        alert("Algo correu mal");
      } */
      setMembers([]);
    });
  };

  return (
    <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
      <div>
        <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
          Membros do Projetos
        </h3>
        {showMembers.map((member, index) => (
          <div
            key={index}
            className="row bg-white text-black mb-3 rounded-3 w-50 mx-auto align-items-center"
          >
            <div className="col-lg-2 ">
              {member.userInvitedPhoto === null ? (
                <img
                  src={
                    "https://t3.ftcdn.net/jpg/00/36/94/26/360_F_36942622_9SUXpSuE5JlfxLFKB1jHu5Z07eVIWQ2W.jpg"
                  }
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
            {showProjects.manager &&
            (showProjects.statusInt === 0 || showProjects.statusInt === 4) ? (
              <>
                <div className="col-lg-1">
                  <ModalDeleteProjMember
                    member={member}
                    set={setMembers}
                    projId={showProjects.id}
                  />
                </div>
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
                              //role="switch"
                              id="flexSwitchCheckChecked"
                              //checked
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
                              <BsStarFill color="#C01722" />
                            </span>
                          </OverlayTrigger>
                        </label>
                      </div>
                      {/*    <div class="form-check form-switch">
                        <input
                          class="form-check-input"
                          type="checkbox"
                          id="flexSwitchCheckDefault"
                        />
                        <label
                          class="form-check-label"
                          for="flexSwitchCheckDefault"
                        >
                          Default switch
                        </label>
                      </div> */}
                    </>
                  ) : (
                    <>
                      {/*   <div class="form-check form-switch">
                        <input
                          class="form-check-input"
                          type="checkbox"
                          id="flexSwitchCheckDefault"
                        />
                        <label
                          class="form-check-label"
                          for="flexSwitchCheckDefault"
                        >
                          Default switch
                        </label>
                      </div> */}
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
                              //  role="switch"
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
            {/* {member.manager ? (
              <div className="col-lg-8 d-flex align-items-center">Gestor</div>
            ) : (
              <div className="col-lg-8 d-flex align-items-center">Membro</div>
            )} */}
            {/* {showProjects.manager ? (
              <>
                <div className="col-lg-1">
                  <span class="material-icons-outlined"></span>
                </div>
                <div className="col-lg-2">
                  <ModalDeleteProjMember
                    member={member}
                    set={setMembers}
                    projId={showProjects.id}
                  />
                </div>
              </>
            ) : null} */}
          </div>
        ))}
      </div>

      {showProjects.member &&
      (showProjects.statusInt === 0 || showProjects.statusInt === 4) ? (
        <div className="col-lg-4 mx-auto mb-4">
          <ButtonComponent
            onClick={handleRemove}
            name={"Sair do projecto"}
          ></ButtonComponent>
        </div>
      ) : null}
    </div>
  );
}

export default ProjectMembersList;
