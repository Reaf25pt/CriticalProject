import { useEffect, useState } from "react";
import ContestComponent from "../Components/ContestComponent";
import TextAreaComponent from "../Components/TextAreaComponent";
import EditContestComponent from "../Components/EditContestComponent";
import { Link, useParams } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import { contestOpenStore } from "../stores/ContestOpenStore";
import { BsEyeFill, BsCheck2, BsXLg } from "react-icons/bs";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";

function ContestOpen() {
  const [showComponentA, setShowComponentA] = useState(true);
  const user = userStore((state) => state.user);
  const setContestOpen = contestOpenStore((state) => state.setContestOpen);
  const contest = contestOpenStore((state) => state.contest);
  const [projects, setProjects] = useState([]);

  const toggleComponent = () => {
    setShowComponentA(!showComponentA);
  };

  const { id } = useParams();
  console.log(id);

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
        // console.log(data);
        // setShowProjects(data);
        // console.log(showProjects);
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
        setProjects(data);
        console.log("Projetos");
        console.log(data);
      })
      .catch((err) => console.log(err));
  }, []);

  if (!contest) {
    return <div>Loading...</div>;
  }

  const renderLink = (rowData) => {
    /*   console.log(rowData.id);
    console.log(typeof rowData.id); */
    return (
      <Link to={`/home/projects/${rowData.id}`}>
        <BsEyeFill />
      </Link>
    );
  };

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
                <ContestComponent toggleComponent={toggleComponent} />
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
                <div className="row ">
                  <div className="col-lg-4 bg-secondary rounded-5 p-4">
                    <h3 className="bg-white p-1 rounded-5 text-center mb-4">
                      Lista de projetos pendentes
                    </h3>
                    {projects.map((project) =>
                      project.answered === false ? (
                        <div className="row bg-white w-50 p-1 rounded-5 mx-auto mb-2 ">
                          <div
                            key={project.id}
                            className="d-flex justify-content-between"
                          >
                            <div>
                              <h4>{project.projectTitle}</h4>
                            </div>
                            <div>
                              <Link to={`/home/projects/${project.id}`}>
                                <BsEyeFill color="red" size={30} />
                              </Link>
                              <BsCheck2 size={30} color="green" />
                              <BsXLg size={30} />
                            </div>
                          </div>
                        </div>
                      ) : null
                    )}
                  </div>{" "}
                  <div className="col-lg-4">
                    <DataTable value={projects} selectionMode="single ">
                      <Column field="projectTitle" header="Nome do Projeto" />
                      <Column body={renderLink} header="#" />
                    </DataTable>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>{" "}
    </div>
  );
}
export default ContestOpen;
