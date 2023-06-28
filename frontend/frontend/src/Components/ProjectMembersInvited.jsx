import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "../Components/ModalDeleteProjMember";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "../Components/InputComponent";
import { useNavigate } from "react-router-dom";
import { BsStarFill } from "react-icons/bs";
import { BsEyeFill, BsCheck2, BsXLg } from "react-icons/bs";

function ProjectMembersInvited({ showProjects, setMembers }) {
  const user = userStore((state) => state.user);
  const navigate = useNavigate();
  const updateUser = userStore((state) => state.updateUser);
  const id = showProjects.id;
  const [pendingInvites, setPendingInvites] = useState([]);
  const [showPendingInvites, setShowPendingInvites] = useState([]);

  useEffect(() => {
    console.log("pending invites");
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
        console.log(data);
        setShowPendingInvites(data);
        console.log(showPendingInvites);
      })
      .catch((err) => console.log(err));
  }, [pendingInvites]);

  function handleResponse(projMemberId, answer) {
    // event.preventDefault();

    console.log(projMemberId + " " + answer);

    fetch(`http://localhost:8080/projetofinal/rest/project/selfinvitation`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
        answer: answer,
        projMemberId: projMemberId,
        projId: id,
      },
    }).then((response) => {
      if (response.status === 200) {
        console.log(response);
        setPendingInvites([]);
        setMembers([]);
        alert("pedido respondido");

        //navigate("/home", { replace: true });
      } else {
        alert("Algo correu mal. Tente novamente");
      }
    });
  }

  return (
    <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
      <div>
        <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
          Convites pendentes
        </h3>
        {showPendingInvites.map((member, index) => (
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
            <div className="col-lg-6 ">
              <BsCheck2
                size={30}
                color="green"
                onClick={() => handleResponse(member.id, 1)}
              />
            </div>
            <div className="col-lg-6 ">
              <BsXLg
                size={30}
                color="red"
                onClick={() => handleResponse(member.id, 0)}
              />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ProjectMembersInvited;
