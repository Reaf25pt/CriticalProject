import { Col, Container, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";
import { useEffect, useState, useRef } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { BsEyeFill } from "react-icons/bs";
import { Link } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { FilterMatchMode, FilterOperator } from "primereact/api";
import { Dropdown } from "primereact/dropdown";
import { Calendar } from "primereact/calendar";

function Contest() {
  const user = userStore((state) => state.user);
  const [contests, setContests] = useState([]);
  const [showList, setShowList] = useState([]);
  const [loading, setLoading] = useState(true);
  const activeContestList = showList.filter((item) => item.statusInt !== 0);
  const [filters, setFilters] = useState({
    title: { value: null, matchMode: FilterMatchMode.CONTAINS },
    status: { value: null, matchMode: FilterMatchMode.EQUALS },
    // startOpenCall: { value: null, matchMode: FilterMatchMode.DATE_AFTER },
    // finishDate: { value: null, matchMode: FilterMatchMode.DATE_BEFORE },
  });
  const [contestStatus] = useState([
    "Planning",
    "Open",
    "Ongoing",
    "Concluded",
  ]);
  const statusRowFilterTemplate = (options) => {
    return (
      <Dropdown
        value={options.value}
        options={contestStatus}
        onChange={(e) => options.filterApplyCallback(e.value)}
        // itemTemplate={statusItemTemplate}
        placeholder="Filtrar: estado"
        className="p-column-filter"
        showClear
        style={{ minWidth: "12rem" }}
      />
    );
  };

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/contest/allcontests`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setShowList(data);
        setLoading(false);
      })
      .catch((err) => console.log(err));
  }, [contests]);

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
  };

  const renderLink = (rowData) => {
    return (
      <Link to={`/home/contests/${rowData.id}`}>
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
            <>
              <div className="row">
                <div className="col mt-5">
                  //{" "}
                  <LinkButton
                    name={"Adicionar Concurso"}
                    to={"/home/contestcreate"}
                  />
                </div>
              </div>
              <div className="row mx-auto mt-5">
                <div className="col-lg-9 bg-secondary p-3 rounded-4 mx-auto ">
                  <DataTable
                    value={showList}
                    rows={10}
                    paginator
                    filters={filters}
                    filterDisplay="row"
                    loading={loading}
                    // globalFilterFields={["title", "status"]}
                    emptyMessage="Nenhum concurso encontrado"
                  >
                    <Column
                      field="title"
                      header="Nome"
                      sortable
                      filter
                      filterPlaceholder="Pesquisar: nome"
                      style={{ width: "17rem" /* , maxWidth: "9rem"  */ }}
                    />
                    <Column
                      field="status"
                      header="Estado"
                      sortable
                      showFilterMenu={false}
                      filterMenuStyle={{ width: "14rem" }}
                      style={{ minWidth: "12rem" }}
                      filter
                      filterElement={statusRowFilterTemplate}
                    />
                    <Column
                      field="startOpenCall"
                      header="Data de Início"
                      sortable
                      body={(rowData) =>
                        convertTimestampToDate(rowData.startOpenCall)
                      }
                    />
                    <Column
                      field="finishDate"
                      header="Data de Fim"
                      sortable
                      body={(rowData) =>
                        convertTimestampToDate(rowData.finishDate)
                      }
                    />
                    <Column body={renderLink} header="#" />
                  </DataTable>
                </div>
              </div>
            </>
          ) : (
            <div className="row mx-auto mt-5">
              <div className="col-lg-9 bg-secondary p-3 rounded-4 mx-auto ">
                <DataTable
                  value={activeContestList}
                  rows={10}
                  paginator
                  filters={filters}
                  filterDisplay="row"
                  loading={loading}
                  // globalFilterFields={["title", "status"]}
                  emptyMessage="Nenhum concurso encontrado"
                >
                  <Column
                    field="title"
                    header="Nome"
                    sortable
                    filter
                    filterPlaceholder="Pesquisar: nome"
                    style={{ width: "17rem" /* , maxWidth: "9rem" */ }}
                  />
                  <Column
                    field="status"
                    header="Estado"
                    sortable
                    showFilterMenu={false}
                    filterMenuStyle={{ width: "14rem" }}
                    style={{ minWidth: "12rem" }}
                    filter
                    filterElement={statusRowFilterTemplate}
                  />
                  <Column
                    field="startOpenCall"
                    header="Data de Início"
                    sortable
                    body={(rowData) =>
                      convertTimestampToDate(rowData.startOpenCall)
                    }
                  />
                  <Column
                    field="finishDate"
                    header="Data de Fim"
                    sortable
                    body={(rowData) =>
                      convertTimestampToDate(rowData.finishDate)
                    }
                  />
                  <Column body={renderLink} header="#" />
                </DataTable>
              </div>
            </div>
          )}

          {/*  <div className="row mx-auto mt-5">
            <div>
              <DataTable value={showList}>
                <Column field="title" header="Nome" sortable />
                <Column field="status" header="Estado" sortable />
                <Column
                  field="startOpenCall"
                  header="Data de Início"
                  sortable
                  body={(rowData) =>
                    convertTimestampToDate(rowData.startOpenCall)
                  }
                />
                <Column
                  field="finishDate"
                  header="Data de Fim"
                  sortable
                  body={(rowData) => convertTimestampToDate(rowData.finishDate)}
                />
                <Column body={renderLink} header="#" />
              </DataTable>
            </div>
          </div> */}
        </div>
      </div>
    </div>
  );
}

export default Contest;
