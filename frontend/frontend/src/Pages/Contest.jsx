import { Col, Container, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";
import { useState } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { BsEyeFill } from "react-icons/bs";
import { Link } from "react-router-dom";
import { userStore } from "../stores/UserStore";

function Contest() {
  const user = userStore((state) => state.user);

  const [data, setData] = useState([
    {
      title: "Task 1",
      status: "In Progress",
      startDate: "2023-06-20",
      endDate: "2023-06-25",
    },
    {
      title: "Task 2",
      status: "Completed",
      startDate: "2023-06-15",
      endDate: "2023-06-18",
    },
    {
      title: "Task 3",
      status: "Pending",
      startDate: "2023-06-22",
      endDate: "2023-06-24",
    },
    // Add more data as needed
  ]);

  const renderLink = (rowData) => {
    return (
      <Link to={`/home/projects/${rowData.id}`}>
        <BsEyeFill />
      </Link>
    );
  };

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
            Concursos
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
          {user.contestManager ? (
            <div className="row">
              <div className="col mt-5">
                //{" "}
                <LinkButton
                  name={"Adicionar Concurso"}
                  to={"/home/contestcreate"}
                />
              </div>
            </div>
          ) : null}

          <div className="row mx-auto mt-5">
            <div>
              <DataTable value={data}>
                <Column field="title" header="Nome do Projeto" sortable />
                <Column field="status" header="Status" sortable />
                <Column field="startDate" header="Data de Inicio" sortable />
                <Column field="endDate" header="Data do Fim" sortable />
                <Column body={renderLink} header="#" />
              </DataTable>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Contest;
