import LinkButton from "../Components/LinkButton";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { BsEyeFill } from "react-icons/bs";

function Projects() {
  const user = userStore((state) => state.user);

  const [showAllProjects, setAllShowProjects] = useState([]);
  const [projects, setProjects] = useState([]);

  const renderLink = (rowData) => {
    return (
      <Link to={`/home/projects/${rowData.id}`}>
        <BsEyeFill />
      </Link>
    );
  };

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
  };

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/project/allprojects`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        console.log(data);
        setAllShowProjects(data);
      })
      .catch((err) => console.log(err));
  }, [projects]);
  return (
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
          <div className="row mt-5 d-flex justify-content-around">
            {!user.contestManager && user.noActiveProject ? (
              <div className="col-lg-2 ">
                //{" "}
                <LinkButton
                  name={"Adicionar Projeto"}
                  to={"/home/projectscreate"}
                />
              </div>
            ) : null}

            <div className="col-lg-9 bg-secondary p-3 rounded-4">
              <DataTable
                value={showAllProjects}
                sortMode="multiple"
                tableStyle={{ minWidth: "50rem" }}
                paginator
                rows={10}
              >
                <Column
                  field="creationDate"
                  header="Data de Registo"
                  sortable
                  style={{ width: "25%" }}
                  body={(rowData) =>
                    convertTimestampToDate(rowData.creationDate)
                  }
                ></Column>
                <Column
                  field="title"
                  header="Nome do Projeto"
                  sortable
                  style={{ width: "25%" }}
                ></Column>
                <Column
                  field="status"
                  header="Estado"
                  sortable
                  style={{ width: "25%" }}
                ></Column>
                <Column
                  field={"membersNumber"}
                  header="Vagas disponíveis"
                  sortable
                  style={{ width: "15%" }}
                  body={(showAllProjects) =>
                    `${showAllProjects.availableSpots} / ${showAllProjects.membersNumber}`
                  }
                ></Column>
                <Column body={renderLink} header="#" />
              </DataTable>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Projects;
