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
import { classNames } from "primereact/utils";
import { FaSearch } from "react-icons/fa";

function Projects() {
  const user = userStore((state) => state.user);
  const ownProj = userStore((state) => state.ownProj);

  const [showAllProjects, setAllShowProjects] = useState([]);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [globalFilterValue, setGlobalFilterValue] = useState("");
  // const [title, setTitle] = useState("");

  const [queryWinner, setQueryWinner] = useState(false);

  const [filters, setFilters] = useState({
    // global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    // title: { value: null, matchMode: FilterMatchMode.CONTAINS },
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
      //  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      // title: { value: null, matchMode: FilterMatchMode.CONTAINS },
      status: { value: null, matchMode: FilterMatchMode.EQUALS },
    });
    setQueryWinner(false);
    setGlobalFilterValue("");
    //setTitle("");
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
          <FaSearch />
          <InputText
            filterField="global"
            value={globalFilterValue}
            onChange={onGlobalFilterChange}
            placeholder="nome / palavra-chave / skill "
            title="Filtrar: nome / palavra-chave / skill "
          />
        </span>
      </div>
    );
  };

  const onGlobalFilterChange = (e) => {
    const value = e.target.value;
    // let _filters = { ...filters };

    // _filters["global"].value = value;

    // setFilters(_filters);
    setGlobalFilterValue(value);
    console.log(globalFilterValue);
    console.log(filters);
  };

  /*   const onTitleChange = (e) => {
    const value = e.target.value;

    // let _filters = { ...filters };

    // _filters["global"].value = value;

    // setFilters(_filters);
    setTitle(value);
    console.log(title);
    console.log(filters);
  }; */

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
    const handleFetchData = async () => {
      const response = await fetch(
        `http://localhost:8080/projetofinal/rest/project/?queryWinner=${queryWinner}&global=${globalFilterValue}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            token: user.token,
          },
        }
      );
      const data = await response.json();
      //  .then((resp) => resp.json())
      //  .then((data) => {
      setAllShowProjects(data);
      setLoading(false);

      console.log(data);
    };

    handleFetchData();
    // })
    // .catch((err) => console.log(err));
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
          {" "}
          {!user.contestManager && (ownProj === null || ownProj.id === 0) ? (
            <div className="row">
              <div className="col mt-5">
                //{" "}
                <LinkButton
                  name={"Adicionar Projeto"}
                  to={"/home/projectscreate"}
                />
              </div>
            </div>
          ) : null}
          <div className="row mx-auto mt-5">
            <div className="col-lg-10 bg-secondary p-3 rounded-4 mx-auto ">
              <DataTable
                value={showAllProjects}
                sortMode="multiple"
                tableStyle={{ minWidth: "50rem" }}
                paginator
                rows={10}
                emptyMessage="Nenhum projecto encontrado"
                removableSort
                header={header}
                // filters={filters}
                filterDisplay="menu"
                loading={loading}
                // globalFilterFields={["title", "status", "global"]}
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
                  // filter
                  // filterField="title"
                  // filterPlaceholder="Filtrar: nome"
                  // value={title}
                  // onChange={onTitleChange}
                  //  style={{ width: "17rem" /* , maxWidth: "9rem"  */ }}
                ></Column>
                <Column
                  field="status"
                  header="Estado"
                  sortable
                  style={{ width: "25%" }}
                  //showFilterMenu={true}
                  filterMenuStyle={{ width: "14rem" }}
                  // style={{ minWidth: "12rem" }}
                  filter
                  filterElement={statusRowFilterTemplate}
                ></Column>
                <Column
                  field={"membersNumber"}
                  header="Membros participantes"
                  sortable
                  style={{ width: "15%" }}
                  body={(showAllProjects) => {
                    const calculateMembers =
                      showAllProjects.membersNumber -
                      showAllProjects.availableSpots;
                    return ` ${calculateMembers} / ${showAllProjects.membersNumber}`;

                    /* (showAllProjects) =>
                                         ` ${showAllProjects.availableSpots} / ${showAllProjects.membersNumber}` / (${showAllProjects.membersNumber}-${showAllProjects.availableSpots})
                     
                  */
                  }}
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
