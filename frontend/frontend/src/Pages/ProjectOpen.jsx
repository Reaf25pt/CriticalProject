import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import Keyword from "../Components/Keyword";
import { BsSearch } from "react-icons/bs";
import TextAreaComponent from "../Components/TextAreaComponent";
import SelectComponent from "../Components/SelectComponent";
import FormTask from "../Components/FormTask";
import TimeLine from "../Components/TimeLine";
import ProjectComponent from "../Components/ProjectComponent";
import { useEffect, useState } from "react";
import EditProject from "../Components/EditProject";
import { useParams } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import ModalDeleteProjMember from "../Components/ModalDeleteProjMember";
import InviteMember from "../Components/InviteMember";
import ProjectMembersList from "../Components/ProjectMembersList";
import ProjectMembersInvited from "../Components/ProjectMembersInvited";
import ProjectChat from "../Components/ProjectChat";

function ProjectOpen() {
  const [showComponentA, setShowComponentA] = useState(true);
  const user = userStore((state) => state.user);
  const [showProjects, setShowProjects] = useState(null);
  const [projects, setProjects] = useState([]);
  const [showMembers, setShowMembers] = useState([]);
  const [members, setMembers] = useState([]);

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
        console.log("projects");
        console.log(data);
        setShowProjects(data);
      })
      .catch((err) => console.log(err));
  }, [projects]);

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
        console.log("members");
        console.log(data);
        setShowMembers(data);
      })
      .catch((err) => console.log(err));
  }, [members]);

  if (!showProjects) {
    return <div>Loading...</div>;
  }

  return (
    <div class="container-fluid">
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
        {showProjects.member ? (
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
              <ProjectComponent
                toggleComponent={toggleComponent}
                project={showProjects}
                members={showMembers}
                setProjects={setProjects}
              />
            ) : (
              <EditProject
                project={showProjects}
                toggleComponent={toggleComponent}
                set={setProjects}
              />
            )}
          </div>
        </div>

        <div
          class="tab-pane fade"
          id="content2"
          role="tabpanel"
          aria-labelledby="tab2"
        >
          <div className="row d-flex justify-content-around">
            <div className="col-lg-5">
              {showProjects.manager ? (
                <InviteMember projId={showProjects.id} />
              ) : null}
            </div>
            <div className="col-lg-4">
              <ProjectMembersList
                showMembers={showMembers}
                showProjects={showProjects}
                setMembers={setMembers}
              />
            </div>
            <div className="row">
              {showProjects.manager ? (
                <ProjectMembersInvited
                  showProjects={showProjects}
                  setMembers={setMembers}
                />
              ) : null}
            </div>
          </div>{" "}
        </div>
        <div
          class="tab-pane fade"
          id="content3"
          role="tabpanel"
          aria-labelledby="tab3"
        >
          <FormTask project={showProjects} listMembers={showMembers} />
        </div>
        <div
          class="tab-pane fade"
          id="content4"
          role="tabpanel"
          aria-labelledby="tab4"
        >
          {/*  <h3>Tab 4 Content</h3> */}
          <ProjectChat project={showProjects} />
          {/*  <p>This is the content for Tab 4.</p> */}
        </div>
        <div
          class="tab-pane fade"
          id="content5"
          role="tabpanel"
          aria-labelledby="tab5"
        >
          <TimeLine project={showProjects} />
        </div>
      </div>
    </div>
  );
}

export default ProjectOpen;
