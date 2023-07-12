import { useEffect, useState } from "react";
import ContestComponent from "../Components/ContestComponent";
import TextAreaComponent from "../Components/TextAreaComponent";
import EditContestComponent from "../Components/EditContestComponent";
import { Link, useParams } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import { contestOpenStore } from "../stores/ContestOpenStore";
import { BsEyeFill, BsCheck2, BsXLg, BsTrophyFill } from "react-icons/bs";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Tag } from "primereact/tag";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import ContestApplications from "../Components/ContestApplications";
import { toast, Toaster } from "react-hot-toast";

function ContestOpen() {
  const [showComponentA, setShowComponentA] = useState(true);
  const user = userStore((state) => state.user);
  const setContestOpen = contestOpenStore((state) => state.setContestOpen);
  const contest = contestOpenStore((state) => state.contest);
  const setProjList = contestOpenStore((state) => state.setProjectList);
  const projList = contestOpenStore((state) => state.projectList);
  const showProjList = contestOpenStore((state) => state.projectList);

  const toggleComponent = () => {
    setShowComponentA(!showComponentA);
  };

  const { id } = useParams();

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/contest/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setContestOpen(data);
      })
      .catch((err) => console.log(err));
  }, []);

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/contest/projects/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setProjList(data);
      })
      .catch((err) => console.log(err));
  }, []);

  const answeredProjects = projList.filter((item) => item.answered);
  const acceptedProjects = projList.filter((item) => item.accepted);

  // const pendingApplications = projList.filter((item) => !item.answered);

  if (!contest) {
    return <div>Loading...</div>;
  }

  const renderLink = (rowData) => {
    return (
      <Link to={`/home/projects/${rowData.projectId}`}>
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip>Ver projecto</Tooltip>}
        >
          <span data-bs-toggle="tooltip" data-bs-placement="top">
            {" "}
            <BsEyeFill size={30} color="black" />
          </span>
        </OverlayTrigger>
      </Link>
    );
  };

  const winner = (rowData) => {
    if (rowData.projectId === contest.winnerProjectId)
      return (
        <OverlayTrigger placement="top" overlay={<Tooltip>Vencedor</Tooltip>}>
          <span data-bs-toggle="tooltip" data-bs-placement="top">
            {" "}
            <BsTrophyFill size={30} color="black" />
          </span>
        </OverlayTrigger>
      );
  };

  const answer = (rowData) => {
    if (!rowData.accepted && rowData.answered) {
      return (
        <div className="bg-danger text-white text-center rounded-4">
          Recusada
        </div>
      );
    } else if (rowData.accepted && rowData.answered) {
      return (
        <div className="bg-success text-white text-center rounded-4">
          Aprovada
        </div>
      );
    } else {
      return <p></p>;
    }
  };

  return (
    <div>
      <Toaster position="top-right" />
      <div>
        <ul className="nav nav-tabs" id="myTab" role="tablist">
          <li className="nav-item" role="presentation">
            <button
              className="nav-link active"
              id="home-tab"
              data-bs-toggle="tab"
              data-bs-target="#home"
              type="button"
              role="tab"
              aria-controls="home"
              aria-selected="true"
              style={{ background: "#C01722", color: "white" }}
            >
              Dados
            </button>
          </li>
          <li className="nav-item" role="presentation">
            <button
              className="nav-link"
              id="profile-tab"
              data-bs-toggle="tab"
              data-bs-target="#profile"
              type="button"
              role="tab"
              aria-controls="profile"
              aria-selected="false"
              style={{ background: "#C01722", color: "white" }}
            >
              Projetos
            </button>
          </li>
        </ul>
        <div className="tab-content" id="myTabContent">
          <div
            className="tab-pane fade show active"
            id="home"
            role="tabpanel"
            aria-labelledby="home-tab"
          >
            <div>
              {showComponentA ? (
                <ContestComponent
                  toggleComponent={toggleComponent}
                  // answeredProjects={answeredProjects}
                  // projects={projList}
                />
              ) : (
                <EditContestComponent toggleComponent={toggleComponent} />
              )}
            </div>{" "}
          </div>
          <div className="tab-content" id="myTabContent">
            <div
              className="tab-pane fade"
              id="profile"
              role="tabpanel"
              aria-labelledby="profile-tab"
            >
              {user.contestManager ? (
                <div>
                  <div className="row mx-auto">
                    <div className="col-lg-6 mx-auto mt-5 bg-secondary p-3 rounded-4">
                      <DataTable
                        value={answeredProjects}
                        selectionMode="single  "
                        removableSort
                        paginator
                        rows={5}
                        rowsPerPageOptions={[5, 10, 25, 50]}
                        emptyMessage="Nenhum projecto encontrado"
                      >
                        <Column
                          field="projectTitle"
                          header="Nome do Projeto"
                          style={{
                            textAlign: "center",
                            fontSize: "20px",
                            fontWeight: "bolder",
                          }}
                          sortable
                        />

                        <Column
                          field="accepted"
                          body={answer}
                          header="Candidatura"
                          sortable
                        />
                        <Column body={renderLink} header="" />
                        <Column body={winner} header="" />
                      </DataTable>
                    </div>
                    {contest.statusInt === 1 ? (
                      <div className="col-lg-5">
                        {" "}
                        <ContestApplications
                        // pendingApplications={pendingApplications}
                        // setProjects={setProjects}
                        />
                      </div>
                    ) : null}
                  </div>
                </div>
              ) : (
                <div>
                  <div className="row mx-auto">
                    <div className="col-lg-6 mx-auto mt-5 bg-secondary p-3 rounded-4">
                      <DataTable
                        value={acceptedProjects}
                        selectionMode="single  "
                        removableSort
                        paginator
                        rows={5}
                        rowsPerPageOptions={[5, 10, 25, 50]}
                        emptyMessage="Nenhum projecto encontrado"
                      >
                        <Column
                          field="projectTitle"
                          header="Nome do Projeto"
                          style={{
                            textAlign: "center",
                            fontSize: "20px",
                            fontWeight: "bolder",
                          }}
                          sortable
                        />

                        <Column
                          field="accepted"
                          body={answer}
                          header="Candidatura"
                          sortable
                        />
                        <Column body={renderLink} header="" />
                        <Column body={winner} header="" />
                      </DataTable>
                    </div>
                  </div>
                </div>
              )}

              <div></div>
            </div>
          </div>
        </div>
      </div>{" "}
    </div>
  );
}
export default ContestOpen;
