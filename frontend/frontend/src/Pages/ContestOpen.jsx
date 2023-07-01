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

function ContestOpen() {
  const [showComponentA, setShowComponentA] = useState(true);
  const user = userStore((state) => state.user);
  const setContestOpen = contestOpenStore((state) => state.setContestOpen);
  const contest = contestOpenStore((state) => state.contest);
  const [projects, setProjects] = useState([]);
  const [showProjects, setShowProjects] = useState([]);

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
        setShowProjects(data);
        console.log(data);
      })
      .catch((err) => console.log(err));
  }, [projects]);

  const answeredProjects = showProjects.filter((item) => item.answered);

  const pendingApplications = showProjects.filter((item) => !item.answered);

  if (!contest) {
    return <div>Loading...</div>;
  }

  const renderLink = (rowData) => {
    return (
      <Link to={`/home/projects/${rowData.id}`}>
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip>Ver detalhes</Tooltip>}
        >
          <span data-bs-toggle="tooltip" data-bs-placement="top">
            {" "}
            <BsEyeFill size={30} color="black" />
          </span>
        </OverlayTrigger>
      </Link>
    );
  };

  const chooseWinner = (rowData) => {
    if (rowData.accepted && contest.statusInt === 2)
      return (
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip>Declarar vencedor</Tooltip>}
        >
          <span data-bs-toggle="tooltip" data-bs-placement="top">
            {" "}
            <BsTrophyFill
              size={30}
              color="black"
              onClick={() => declareWinner(rowData.projectId)}
            />
          </span>
        </OverlayTrigger>
      );
  };

  /* 
  const accept = (rowData) => {
    if (rowData.answered) {
      return <div></div>;
    } else {
      return (
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip>Aceitar projecto</Tooltip>}
        >
          <span data-bs-toggle="tooltip" data-bs-placement="top">
            {" "}
            <BsCheck2
              size={30}
              onClick={() => handleApplication(1, rowData.id)}
              color="green"
            />{" "}
          </span>
        </OverlayTrigger>
      );
    }
  };

  const reject = (rowData) => {
    if (rowData.answered) {
      return <div></div>;
    } else {
      return (
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip>Recusar projecto</Tooltip>}
        >
          <span data-bs-toggle="tooltip" data-bs-placement="top">
            {" "}
            <BsXLg
              size={30}
              onClick={() => handleApplication(0, rowData.id)}
              color="red"
            />
          </span>
        </OverlayTrigger>
      );
    }
  }; */

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

  const declareWinner = (projId) => {
    // event.preventDefault();
    console.log(projId);

    fetch("http://localhost:8080/projetofinal/rest/contest/application", {
      method: "PUT",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
        contestId: contest.id,
        projId: projId,
      },
    }).then((response) => {
      if (response.status === 200) {
        alert("Vencedor declarado");
        //navigate("/home", { replace: true });
      } else {
        alert("Algo correu mal");
      }
    });
  };
  /*  function handleApplication(status, applicationId) {
    var status;

    /* if (event === 0) {
        status = 0;
      } else if (event === 1) {
        status = 1;
      } */

  /*   fetch("http://localhost:8080/projetofinal/rest/contest/application", {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
        answer: status,
        applicationId: applicationId,
        contestId: id,
      },
    }).then((response) => {
      if (response.status === 200) {
        setProjects([]);
        alert("candidatura respondida");

        //navigate("/home", { replace: true });
      } else {
        alert("Algo correu mal. Tente novamente");
      }
    });
  } */

  return (
    <div>
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
                  answeredProjects={answeredProjects}
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
              <div>
                <div className="row mx-auto">
                  <div className="col-lg-6 mx-auto mt-5 bg-secondary p-3 rounded-4">
                    <DataTable
                      value={answeredProjects}
                      selectionMode="single  "
                      paginator
                      rows={5}
                      rowsPerPageOptions={[5, 10, 25, 50]}
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
                      {/*    <Column
                        body={accept}
                         header=""
                      />
                      ;
                      <Column
                        body={reject}
                         header=""
                      />
                      ; */}
                      <Column
                        field="accepted"
                        body={answer}
                        header="Candidatura"
                        sortable
                      />
                      <Column body={renderLink} header="" />
                      <Column body={chooseWinner} header="" />
                    </DataTable>
                  </div>
                </div>
              </div>

              <ContestApplications
                pendingApplications={pendingApplications}
                setProjects={setProjects}
              />
            </div>
          </div>
        </div>
      </div>{" "}
    </div>
  );
}
export default ContestOpen;
