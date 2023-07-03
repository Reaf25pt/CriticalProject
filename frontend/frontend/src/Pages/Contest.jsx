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
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { classNames } from "primereact/utils";

function Contest() {
  const user = userStore((state) => state.user);
  const [contests, setContests] = useState([]);
  const [showList, setShowList] = useState([]);
  const [loading, setLoading] = useState(true);
  const activeContestList = showList.filter((item) => item.statusInt !== 0);
  const [filters, setFilters] = useState({
    title: { value: null, matchMode: FilterMatchMode.CONTAINS },
    status: { value: null, matchMode: FilterMatchMode.EQUALS },
    startOpenCall: { value: null, matchMode: FilterMatchMode.DATE_AFTER },
    finishDate: { value: null, matchMode: FilterMatchMode.DATE_BEFORE },
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

  const clearFilter = () => {
    console.log("clear " + filters);
    setFilters({
      title: { value: null, matchMode: FilterMatchMode.CONTAINS },
      status: { value: null, matchMode: FilterMatchMode.EQUALS },
      startOpenCall: { value: null, matchMode: FilterMatchMode.DATE_AFTER },
      finishDate: { value: null, matchMode: FilterMatchMode.DATE_BEFORE },
    });
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
        console.log(data);
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

  const renderHeader = () => {
    return (
      <div className="flex justify-content-between">
        <Button
          type="button"
          icon="pi pi-filter-slash"
          label="Limpar filtros"
          outlined
          onClick={clearFilter}
        />
      </div>
    );
  };

  const filterClearTemplate = (options) => {
    return (
      <Button
        type="button"
        icon="pi pi-times"
        onClick={options.filterClearCallback}
        severity="secondary"
      ></Button>
    );
  };

  const filterApplyTemplate = (options) => {
    return (
      <Button
        type="button"
        icon="pi pi-check"
        onClick={options.filterApplyCallback}
        severity="success"
      ></Button>
    );
  };

  const formatDate = (value) => {
    return new Date(value).toLocaleDateString("pt-PT", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  const dateStartBodyTemplate = (rowData) => {
    return formatDate(rowData.startOpenCall);
  };
  const dateFinishBodyTemplate = (rowData) => {
    return formatDate(rowData.finishDate);
  };

  const startDateFilterTemplate = (options) => {
    return (
      <Calendar
        value={options.value}
        onChange={(e) => options.filterCallback(e.value, options.index)}
        dateFormat="mm/dd/yy"
        placeholder="mm/dd/yyyy"
        mask="99/99/9999"
      />
    );
  };

  const finishDateFilterTemplate = (options) => {
    return (
      <Calendar
        value={options.value}
        onChange={(e) => options.filterCallback(e.value, options.index)}
        dateFormat="mm/dd/yy"
        placeholder="mm/dd/yyyy"
        mask="99/99/9999"
      />
    );
  };
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
                    removableSort
                    value={showList}
                    rows={10}
                    paginator
                    header={header}
                    filters={filters}
                    // filterDisplay="menu"
                    loading={loading}
                    globalFilterFields={[
                      "title",
                      "status",
                      "startOpenCall",
                      "finishDate",
                    ]}
                    emptyMessage="Nenhum concurso encontrado"
                  >
                    <Column
                      field="title"
                      header="Nome"
                      sortable
                      filter
                      // showFilterMenu={true}
                      filterPlaceholder="Filtrar: nome"
                      style={{ width: "17rem" /* , maxWidth: "9rem"  */ }}
                    />
                    <Column
                      field="status"
                      header="Estado"
                      sortable
                      //  showFilterMenu={true}
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
                      filterField="startOpenCall"
                      dataType="date"
                      style={{ minWidth: "12rem" }}
                      // body={dateStartBodyTemplate}
                      filter
                      filterElement={startDateFilterTemplate}
                      filterClear={filterClearTemplate}
                      filterApply={filterApplyTemplate}
                      // showFilterMenu={true}
                    />
                    <Column
                      field="finishDate"
                      header="Data de Fim"
                      sortable
                      body={(rowData) =>
                        convertTimestampToDate(rowData.finishDate)
                      }
                      filterField="finishDate"
                      dataType="date"
                      style={{ minWidth: "12rem" }}
                      // body={dateFinishBodyTemplate}
                      filter
                      filterElement={finishDateFilterTemplate}
                      filterClear={filterClearTemplate}
                      filterApply={filterApplyTemplate}
                      // showFilterMenu={true}
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
                  removableSort
                  value={activeContestList}
                  rows={10}
                  paginator
                  header={header}
                  filters={filters}
                  filterDisplay="row"
                  loading={loading}
                  globalFilterFields={[
                    "title",
                    "status",
                    "startOpenCall",
                    "finishDate",
                  ]}
                  emptyMessage="Nenhum concurso encontrado"
                >
                  <Column
                    field="title"
                    header="Nome"
                    sortable
                    filter
                    filterPlaceholder="Filtrar: nome"
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
                    filterField="startOpenCall"
                    dataType="date"
                    style={{ minWidth: "12rem" }}
                    // body={dateStartBodyTemplate}
                    filter
                    filterElement={startDateFilterTemplate}
                    filterClear={filterClearTemplate}
                    filterApply={filterApplyTemplate}
                  />
                  <Column
                    field="finishDate"
                    header="Data de Fim"
                    sortable
                    body={(rowData) =>
                      convertTimestampToDate(rowData.finishDate)
                    }
                    filterField="finishDate"
                    dataType="date"
                    style={{ minWidth: "12rem" }}
                    // body={dateFinishBodyTemplate}
                    filter
                    filterElement={finishDateFilterTemplate}
                    filterClear={filterClearTemplate}
                    filterApply={filterApplyTemplate}
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
