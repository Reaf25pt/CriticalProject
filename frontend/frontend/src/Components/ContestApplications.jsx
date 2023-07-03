import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "./ModalDeleteProjMember";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import { useNavigate } from "react-router-dom";
import { BsStarFill } from "react-icons/bs";
import { BsEyeFill, BsCheck2, BsXLg } from "react-icons/bs";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { Link, useParams } from "react-router-dom";
import { contestOpenStore } from "../stores/ContestOpenStore";

function ContestApplications({ pendingApplications }) {
  const user = userStore((state) => state.user);
  const navigate = useNavigate();
  const updateUser = userStore((state) => state.updateUser);
  const { id } = useParams();

  const setProjList = contestOpenStore((state) => state.setProjectList);
  const projList = contestOpenStore((state) => state.projectList);
  //  const [pendingInvites, setPendingInvites] = useState([]);
  // const [showPendingInvites, setShowPendingInvites] = useState([]);

  /*   useEffect(() => {
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
        setShowPendingInvites(data);
      })
      .catch((err) => console.log(err));
  }, [pendingInvites]); */

  /*   function handleResponse(projMemberId, answer) {
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
    }).then((response) => {
      if (response.status === 200) {
        setPendingInvites([]);
        setMembers([]);
        alert("pedido respondido");

        //navigate("/home", { replace: true });
      } else {
        alert("Algo correu mal. Tente novamente");
      }
    });
  } */

  function handleApplication(status, applicationId) {
    var status;

    /* if (event === 0) {
        status = 0;
      } else if (event === 1) {
        status = 1;
      } */

    fetch("http://localhost:8080/projetofinal/rest/contest/application", {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
        answer: status,
        applicationId: applicationId,
        contestId: id,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
          alert("candidatura respondida");

          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
      })
      .then((data) => {
        setProjList(data);
      })
      .catch((err) => console.log(err));
  }

  if (pendingApplications.length === 0 && user.contestManager) {
    return (
      <div className="row mt-5">
        <h5 className="text-white">Não há candidaturas à espera de resposta</h5>
      </div>
    );
  } else if (user.contestManager) {
    return (
      <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
        <div>
          <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
            Convites pendentes
          </h3>
          {pendingApplications.map((application, index) => (
            <div
              key={index}
              className="row bg-white text-black mb-3 rounded-3 w-50 mx-auto align-items-center"
            >
              <div className="col-lg-6 ">{application.projectTitle}</div>
              <div className="col-lg-6 ">
                <OverlayTrigger
                  placement="top"
                  overlay={<Tooltip>Aceitar pedido</Tooltip>}
                >
                  <span data-bs-toggle="tooltip" data-bs-placement="top">
                    {" "}
                    <BsCheck2
                      size={30}
                      color="green"
                      onClick={() => handleApplication(1, application.id)}
                    />{" "}
                  </span>
                </OverlayTrigger>
              </div>
              <div className="col-lg-6 ">
                <OverlayTrigger
                  placement="top"
                  overlay={<Tooltip>Recusar pedido</Tooltip>}
                >
                  <span data-bs-toggle="tooltip" data-bs-placement="top">
                    {" "}
                    <BsXLg
                      size={30}
                      color="red"
                      onClick={() => handleApplication(0, application.id)}
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
}

export default ContestApplications;
