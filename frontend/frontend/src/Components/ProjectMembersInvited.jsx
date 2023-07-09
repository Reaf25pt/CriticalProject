import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "../Components/ModalDeleteProjMember";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "../Components/InputComponent";
import { useNavigate } from "react-router-dom";
import { BsStarFill } from "react-icons/bs";
import { BsEyeFill, BsCheck2, BsXLg } from "react-icons/bs";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { projOpenStore } from "../stores/projOpenStore";
import { toast, Toaster } from "react-hot-toast";

function ProjectMembersInvited() {
  const user = userStore((state) => state.user);
  const navigate = useNavigate();
  const updateUser = userStore((state) => state.updateUser);
  const project = projOpenStore((state) => state.project);
  const setMembers = projOpenStore((state) => state.setMembers);

  const id = project.id;
  const pendingInvites = projOpenStore((state) => state.pendingInvites);
  const setPendingInvites = projOpenStore((state) => state.setPendingInvites);

  useEffect(() => {
    fetch(
      `http://localhost:8080/projetofinal/rest/project/${id}/potentialmembers`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
        },
      }
    )
      .then((resp) => resp.json())
      .then((data) => {
        setPendingInvites(data);
      })
      .catch((err) => console.log(err));
  }, []);

  function handleResponse(projMemberId, answer) {
    // event.preventDefault();

    fetch(`http://localhost:8080/projetofinal/rest/project/selfinvitation`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
        answer: answer,
        projMemberId: projMemberId,
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
        setPendingInvites(data);
        fetchMembers();
      })
      .catch((error) => {
        toast.error(error.message);
      });
  }

  function fetchMembers() {
    fetch(`http://localhost:8080/projetofinal/rest/project/${id}/members`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setMembers(data);
      })
      .catch((err) => console.log(err));
  }

  if (pendingInvites.filter((invite) => invite.selfinvitation).length === 0) {
    return (
      <div className="row mt-5">
        <h5 className="text-white" style={{ fontWeight: "bolder" }}>
          Não há pedidos à espera de resposta
        </h5>
      </div>
    );
  }
  return (
    <div className="container-fluid">
      <div className="row  mx-auto">
        <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
          Convites pendentes
        </h3>
      </div>
      <div className="row overflow-auto" style={{ maxHeight: "50vh" }}>
        {pendingInvites
          .filter((invite) => invite.selfinvitation)
          .map((member, index) => (
            <div
              key={index}
              className="row bg-white text-black mb-3 p-2 rounded-3  
          w-75 mx-auto d-flex justify-content-around d-flex align-items-center"
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
              <div className="col-lg-6 text-center">
                {member.userInvitedFirstName} {member.userInvitedLastName}
              </div>
              <div className="col-lg-2 ">
                <OverlayTrigger
                  placement="top"
                  overlay={<Tooltip>Aceitar pedido</Tooltip>}
                >
                  <span data-bs-toggle="tooltip" data-bs-placement="top">
                    {" "}
                    <BsCheck2
                      size={30}
                      color="green"
                      onClick={() => handleResponse(member.id, 1)}
                    />{" "}
                  </span>
                </OverlayTrigger>
              </div>
              <div className="col-lg-2 ">
                <OverlayTrigger
                  placement="top"
                  overlay={<Tooltip>Recusar pedido</Tooltip>}
                >
                  <span data-bs-toggle="tooltip" data-bs-placement="top">
                    {" "}
                    <BsXLg
                      size={30}
                      color="red"
                      onClick={() => handleResponse(member.id, 0)}
                    />
                  </span>
                </OverlayTrigger>
              </div>
            </div>
          ))}
      </div>
    </div>
  );
}

export default ProjectMembersInvited;
