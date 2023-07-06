import SearchUser from "../Components/SearchUser";
import { useEffect, useState, useRef } from "react";
import { userStore } from "../stores/UserStore";
import { BsEyeFill } from "react-icons/bs";
import { Link } from "react-router-dom";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";

function Start() {
  const user = userStore((state) => state.user);
  const [contests, setContests] = useState([]);
  const ownProj = userStore((state) => state.ownProj);
  const setOwnProj = userStore((state) => state.setOwnProj);

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/contest/activecontests`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setContests(data);
        console.log(data);
      })
      .catch((err) => console.log(err));

    fetch(`http://localhost:8080/projetofinal/rest/user/activeproject`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setOwnProj(data);
      })
      .catch((err) => console.log(err));
  }, []);

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

  
  const renderLinkProj = (rowData) => {
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
            Início
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
          <div className="container-fluid">
            <div className="row mt-5 d-flex justify-content-around">
              <div className="col-lg-4">
                <div className="row">
                  <SearchUser />
                </div>
                <div className="row d-flex justify-content-around mt-5">
                  <h1 className="text-center">
                    <span className="text-danger" style={{fontSize:"60px",fontWeight:"bolder"}}>Bem-vindo</span> 
                    <p className="text-white" style={{fontSize:"50px"}}>ao Laboratório de Inovação.</p>
                  </h1>
                </div>
              </div>
              <div class="col-lg-6">
                {contests && contests.length > 0 ? (
                  <div className="row bg-secondary rounded-5 p-3 ">
                    <h3 className="bg-white  text-center text-nowrap rounded-5 mb-3 ">
                      Concursos activos
                    </h3>
                    <DataTable
                      value={contests}
                      selectionMode="single  "
                      emptyMessage="Nenhum projecto encontrado"
                    >
                      <Column field="title" header="Nome do Projeto" />
                      <Column field="status" header="Estado" />
                      <Column
                        field="startOpenCall"
                        header="Data inicial "
                        body={(rowData) =>
                          convertTimestampToDate(rowData.startOpenCall)
                        }
                      />
                      <Column
                        field="finishOpenCall"
                        header="Data final"
                        body={(rowData) =>
                          convertTimestampToDate(rowData.finishOpenCall)
                        }
                      />
                      <Column body={renderLink} header="#" />
                    </DataTable>
                  </div>
                ) : (
                  <div className="row mt-5">
                    <h5 className="text-white">Não há concursos activos</h5>
                  </div>
                )}
                <div class="row mt-5 mb-5 d-flex  rounded-5">
                  {ownProj !== null && ownProj.id !== 0 ? (
                    <div className="row bg-secondary rounded-5 p-3 ">
                      <h3 className="bg-white  text-center text-nowrap rounded-5 mb-3 ">
                        Projecto activo
                      </h3>
                      {/* <DataTable
                        value={ownProj}
                        selectionMode="single  "
                        emptyMessage="Nenhum projecto encontrado"
                      >
                        <Column field="title" header="Nome do Projeto" />
                        <Column field="status" header="Estado" />
                        <Column
                          field="startOpenCall"
                          header="Data inicial "
                          body={(rowData) =>
                            convertTimestampToDate(rowData.startOpenCall)
                          }
                        />
                        <Column
                          field="finishOpenCall"
                          header="Data final"
                          body={(rowData) =>
                            convertTimestampToDate(rowData.finishOpenCall)
                          }
                        />
                        <Column body={renderLinkProj} header="#" />
                      </DataTable> */}
                       
                       
                    </div>
                  ) : (
                      <h5 className="text-white"  style={{fontWeight:"bolder"}}>Não tem projecto activo</h5>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Start;
