import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "../Components/ModalDeleteProjMember";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "../Components/InputComponent";
import { useNavigate } from "react-router-dom";

function ProjectMembersList({ showMembers, showProjects, setMembers }) {
  const user = userStore((state) => state.user);
  const navigate = useNavigate();

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

  return (
    <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
      <div>
        <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
          Membros do Projetos
        </h3>
        <div className="bg-white text-black  m-1 rounded-3 w-50  mx-auto  ">
          {showMembers.map((member, index) => (
            <div key={index} className="row d-flex justify-content-center">
              <div className="col-lg-4 ">
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
              <div className="col-lg-8 d-flex align-items-center">
                {member.userInvitedFirstName} {member.userInvitedLastName}
              </div>
              {member.manager ? (
                <div className="col-lg-8 d-flex align-items-center">Gestor</div>
              ) : (
                <div className="col-lg-8 d-flex align-items-center">Membro</div>
              )}
              {showProjects.manager ? (
                <>
                  <div className="col-lg-2">
                    <span class="material-icons-outlined">toggle_on</span>
                  </div>
                  <div className="col-lg-2">
                    <ModalDeleteProjMember
                      member={member}
                      set={setMembers}
                      projId={showProjects.id}
                    />
                  </div>
                </>
              ) : null}
            </div>
          ))}
        </div>

        <div className="row mx-auto justify-content-around mt-5">
          <div className="col-lg-12">
            <ButtonComponent
              type="button"
              name="Sair do projecto"
              onClick={handleRemove}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProjectMembersList;
