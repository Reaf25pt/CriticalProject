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

  const { id } = useParams();

  console.log(id);
  console.log(typeof id);

  useEffect(() => {
    console.log("use effect projecto");

    fetch(`http://localhost:8080/projetofinal/rest/project/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        console.log(data);
        setShowProjects(data);
        console.log(showProjects);
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
        //console.log(data);
        setShowMembers(data);
        // console.log(showMembers);
        //console.log(members);
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
        ) : null}
        {/* <li class="nav-item" role="presentation">
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
        </li> */}
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
                /*   local={local} */
                members={showMembers}
              />
            ) : (
              <EditProject
                project={showProjects}
                toggleComponent={toggleComponent}
                set={setProjects}
                /*   local={local} */
              />
            )}
          </div>
          {/*   <div className="row mx-auto justify-content-around mt-5">
            <div className="col-lg-4">
              <div className="row bg-secondary rounded-5 p-4 mb-4">
                <div className="col-lg-12 bg-white rounded-5">
                  <h4 className="text-center">Palavras-Chave</h4>
                </div>
                <div className="row mt-3 mx-auto">
                  <>Falta criar um map para o array das palavras chaves</>
                </div>
              </div>
            </div>
            <div className="col-lg-4">
              <div className="row bg-secondary rounded-5 p-4">
                <div className="col-lg-12 bg-white rounded-5">
                  <h4 className="text-center">Skills</h4>
                </div>
                <div>Falta criar um map para o array das palavras chaves</div>
              </div>
            </div>
          </div> */}
        </div>

        <div
          class="tab-pane fade"
          id="content2"
          role="tabpanel"
          aria-labelledby="tab2"
        >
          <div className="row">
            {showProjects.manager ? (
              <InviteMember projId={showProjects.id} />
            ) : /*  <div className="mx-auto col-10 col-md-8 col-lg-6">
                <form className="mt-5 p-5 bg-secondary rounded-5 ">
                  <div className="row mb-3 ">
                    <div className="col ">
                      <div className="form-outline">
                        <InputComponent type="text" placeholder="Nome" />
                      </div>
                    </div>
                  </div>
                  <div className="col-lg-6 mx-auto">
                    <ButtonComponent name={"Convidar"} />
                  </div>
                </form>
              </div> */
            null}

            <ProjectMembersList
              showMembers={showMembers}
              showProjects={showProjects}
              setMembers={setMembers}
            />

            {/* <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
              <div>
                <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
                  Membros do Projetos
                </h3>
                <div className="bg-white text-black  m-1 rounded-3 w-50  mx-auto  ">
                  {showMembers.map((member, index) => (
                    <div
                      key={index}
                      className="row d-flex justify-content-center"
                    >
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
                        {member.userInvitedFirstName}{" "}
                        {member.userInvitedLastName}
                      </div>
                      {member.manager ? (
                        <div className="col-lg-8 d-flex align-items-center">
                          Gestor
                        </div>
                      ) : (
                        <div className="col-lg-8 d-flex align-items-center">
                          Membro
                        </div>
                      )}
                      {showProjects.manager ? (
                        <div className="col-lg-2">
                          <ModalDeleteProjMember
                            member={member}
                            set={setMembers}
                          />
                        </div>
                      ) : null}
                    </div>
                  ))}
                </div>
              </div>
            </div> */}
          </div>{" "}
        </div>
        <div
          class="tab-pane fade"
          id="content3"
          role="tabpanel"
          aria-labelledby="tab3"
        >
          <FormTask projStatus={showProjects} listMembers={showMembers} />
        </div>
        <div
          class="tab-pane fade"
          id="content4"
          role="tabpanel"
          aria-labelledby="tab4"
        >
          <h3>Tab 4 Content</h3>
          <p>This is the content for Tab 4.</p>
        </div>
        <div
          class="tab-pane fade"
          id="content5"
          role="tabpanel"
          aria-labelledby="tab5"
        >
          <TimeLine tasks={""} />
        </div>
      </div>
    </div>
  );
}

export default ProjectOpen;
