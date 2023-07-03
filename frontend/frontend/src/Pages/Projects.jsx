import LinkButton from "../Components/LinkButton";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { BsEyeFill } from "react-icons/bs";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { FilterMatchMode, FilterOperator } from "primereact/api";
import { Dropdown } from "primereact/dropdown";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";

function Projects() {
  const user = userStore((state) => state.user);
  const activeProject = userStore((state) => state.activeProject);

  const [showAllProjects, setAllShowProjects] = useState([]);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [globalFilterValue, setGlobalFilterValue] = useState("");
  const [queryWinner, setQueryWinner] = useState(false);

  const [filters, setFilters] = useState({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    title: { value: null, matchMode: FilterMatchMode.CONTAINS },
    status: { value: null, matchMode: FilterMatchMode.EQUALS },
  });
  const [projectStatus] = useState([
    "Planning",
    "Ready",
    "Proposed to Contest",
    "Approved to Contest",
    "In Progress",
    "Cancelled",
    "Finished",
  ]);

  const statusRowFilterTemplate = (options) => {
    return (
      <Dropdown
        value={options.value}
        options={projectStatus}
        onChange={(e) => options.filterApplyCallback(e.value)}
        // itemTemplate={statusItemTemplate}
        placeholder="Filtrar: estado"
        className="p-column-filter"
        showClear
        style={{ minWidth: "12rem" }}
      />
    );
  };

  const clearFilter = () => {
    console.log("clear " + filters);
    setFilters({
      global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      title: { value: null, matchMode: FilterMatchMode.CONTAINS },
      status: { value: null, matchMode: FilterMatchMode.EQUALS },
    });
    setQueryWinner(false);
    setGlobalFilterValue("");
  };

  const renderHeader = () => {
    return (
      <div className="flex justify-content-between">
        {" "}
        <Button
          type="button"
          icon="pi pi-filter-slash"
          label="Limpar filtros"
          outlined
          onClick={clearFilter}
        />
        <span>
          {" "}
          <Button
            type="button"
            icon="pi pi-filter-slash"
            label="Projectos vencedores"
            outlined
            onClick={() => setQueryWinner(true)}
          />
        </span>{" "}
        <span className="p-input-icon-left">
          <i className="pi pi-search" />
          <InputText
            value={globalFilterValue}
            onChange={onGlobalFilterChange}
            placeholder="Filtrar por palavra-chave / skill "
          />
        </span>
      </div>
    );
  };

  const onGlobalFilterChange = (e) => {
    const value = e.target.value;
    let _filters = { ...filters };

    _filters["global"].value = value;

    setFilters(_filters);
    setGlobalFilterValue(value);
    console.log(globalFilterValue);
    console.log(filters);
  };

  const renderLink = (rowData) => {
    return (
      <Link to={`/home/projects/${rowData.id}`}>
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip>Ver detalhes</Tooltip>}
        >
          <span data-bs-toggle="tooltip" data-bs-placement="top">
            {" "}
            <BsEyeFill />
          </span>
        </OverlayTrigger>
      </Link>
    );
  };

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
  };

  useEffect(() => {
    //fetch(`http://localhost:8080/projetofinal/rest/project/allprojects`, {
    fetch(
      `http://localhost:8080/projetofinal/rest/project/?queryWinner=${queryWinner}&global=${globalFilterValue}`,
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
        setAllShowProjects(data);
        setLoading(false);

        console.log(data);
      })
      .catch((err) => console.log(err));
  }, [projects, queryWinner, globalFilterValue]);

  const header = renderHeader();

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
            {!user.contestManager && !activeProject.hasActiveProject ? (
              <div className="col-lg-2 ">
                //{" "}
                <LinkButton
                  name={"Adicionar Projeto"}
                  to={"/home/projectscreate"}
                />
              </div>
            ) : null}

            <div className="col-lg-10 bg-secondary p-3 rounded-4">
              <DataTable
                value={showAllProjects}
                sortMode="multiple"
                tableStyle={{ minWidth: "50rem" }}
                paginator
                rows={10}
                emptyMessage="Nenhum projecto encontrado"
                removableSort
                header={header}
                filters={filters}
                // filterDisplay="menu"
                loading={loading}
                globalFilterFields={["title", "status", "global"]}
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
                  filter
                  filterPlaceholder="Filtrar: nome"
                  //  style={{ width: "17rem" /* , maxWidth: "9rem"  */ }}
                ></Column>
                <Column
                  field="status"
                  header="Estado"
                  sortable
                  style={{ width: "25%" }}
                  showFilterMenu={true}
                  filterMenuStyle={{ width: "14rem" }}
                  // style={{ minWidth: "12rem" }}
                  filter
                  filterElement={statusRowFilterTemplate}
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
