import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import Keyword from "../Components/Keyword";
import { BsSearch } from "react-icons/bs";
import TextAreaComponent from "../Components/TextAreaComponent";
import SelectComponent from "../Components/SelectComponent";
import FormTask from "../Components/FormTask";
import TimeLine from "../Components/TimeLine";
import ProjectComponent from "../Components/ProjectComponent";
import { useEffect, useState, useRef } from "react";
import EditProject from "../Components/EditProject";
import { useParams } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "../Components/ModalDeleteProjMember";
import InviteMember from "../Components/InviteMember";
import ProjectMembersList from "../Components/ProjectMembersList";
import ProjectMembersInvited from "../Components/ProjectMembersInvited";
import ProjectChat from "../Components/ProjectChat";
import { projOpenStore } from "../stores/projOpenStore";
import { toast, Toaster } from "react-hot-toast";

function ProjectOpen() {
  const [showComponentA, setShowComponentA] = useState(true);
  const user = userStore((state) => state.user);
  const project = projOpenStore((state) => state.project);
  const setProject = projOpenStore((state) => state.setProjOpen);
  const members = projOpenStore((state) => state.members);
  const setMembers = projOpenStore((state) => state.setMembers);

  const toggleComponent = () => {
    setShowComponentA(!showComponentA);
  };

  const { id } = useParams(); // id do projecto

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/project/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setProject(data);
      })
      .catch((err) => console.log(err));
  }, []);

  useEffect(() => {
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
  }, []);

  if (project === null) {
    return <div>Loading...</div>;
  }

  return (
    <div class="container-fluid">
      <Toaster position="top-right" />

      <ul class="nav nav-tabs" role="tablist">
        <li class="nav-item" role="presentation">
          <button
            class="nav-link active"
            id="tab1"
            data-bs-toggle="tab"
            data-bs-target="#content1"
            type="button"
            role="tab"
            aria-controls="content1"
            aria-selected="true"
            style={{ background: "#C01722", color: "white" }}
          >
            Dados{" "}
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button
            class="nav-link"
            id="tab2"
            data-bs-toggle="tab"
            data-bs-target="#content2"
            type="button"
            role="tab"
            aria-controls="content2"
            aria-selected="false"
            style={{ background: "#C01722", color: "white" }}
          >
            Membros{" "}
          </button>
        </li>
        {project.member ? (
          <>
            <li class="nav-item" role="presentation">
              <button
                class="nav-link"
                id="tab3"
                data-bs-toggle="tab"
                data-bs-target="#content3"
                type="button"
                role="tab"
                aria-controls="content3"
                aria-selected="false"
                style={{ background: "#C01722", color: "white" }}
              >
                Plano do Projeto{" "}
              </button>
            </li>

            <li class="nav-item" role="presentation">
              <button
                class="nav-link"
                id="tab4"
                data-bs-toggle="tab"
                data-bs-target="#content4"
                type="button"
                role="tab"
                aria-controls="content4"
                aria-selected="false"
                style={{ background: "#C01722", color: "white" }}
              >
                Chat{" "}
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button
                class="nav-link"
                id="tab5"
                data-bs-toggle="tab"
                data-bs-target="#content5"
                type="button"
                role="tab"
                aria-controls="content4"
                aria-selected="false"
                style={{ background: "#C01722", color: "white" }}
              >
                Histórico{" "}
              </button>
            </li>
          </>
        ) : user.contestManager ? (
          <li class="nav-item" role="presentation">
            <button
              class="nav-link"
              id="tab3"
              data-bs-toggle="tab"
              data-bs-target="#content3"
              type="button"
              role="tab"
              aria-controls="content3"
              aria-selected="false"
              style={{ background: "#C01722", color: "white" }}
            >
              Plano do Projeto{" "}
            </button>
          </li>
        ) : null}
      </ul>
      <div class="tab-content mt-2">
        <div
          class="tab-pane fade show active"
          id="content1"
          role="tabpanel"
          aria-labelledby="tab1"
        >
          {" "}
          <div>
            {showComponentA ? (
              <ProjectComponent toggleComponent={toggleComponent} />
            ) : (
              <EditProject toggleComponent={toggleComponent} />
            )}
          </div>
        </div>

        <div
          class="tab-pane fade"
          id="content2"
          role="tabpanel"
          aria-labelledby="tab2"
        >
          <div className="row mx-auto d-flex justify-content-around">
            <div className="col-lg-7">
              {project.manager ? <InviteMember /> : null}
            </div>{" "}
          </div>
          <div className="row d-flex justify-content-around">
            <div className="col-lg-5">
              <ProjectMembersList />
            </div>
            <div className="col-lg-5">
              {project.manager ? <ProjectMembersInvited /> : null}
            </div>
          </div>
        </div>
        <div
          class="tab-pane fade"
          id="content3"
          role="tabpanel"
          aria-labelledby="tab3"
        >
          <FormTask />
        </div>
        <div
          class="tab-pane fade"
          id="content4"
          role="tabpanel"
          aria-labelledby="tab4"
        >
          {/*  <h3>Tab 4 Content</h3> */}
          <ProjectChat />
          {/*  <p>This is the content for Tab 4.</p> */}
        </div>
        <div
          class="tab-pane fade"
          id="content5"
          role="tabpanel"
          aria-labelledby="tab5"
        >
          <TimeLine />
        </div>
      </div>
    </div>
  );
}

export default ProjectOpen;
