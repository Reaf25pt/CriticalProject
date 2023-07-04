import SearchUser from "../Components/SearchUser";
import { useEffect, useState, useRef } from "react";
import { userStore } from "../stores/UserStore";
import { BsEyeFill } from "react-icons/bs";
import { Link } from "react-router-dom";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

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
            <div className="row  mt-5">
              <div className="col-lg-4">
                <SearchUser />
              </div>
            </div>
          </div>
          <div class="row mt-5 mb-5 d-flex justify-content-start">
            {contests && contests.length > 0 ? (
              <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
                <div>
                  <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
                    Concursos activos
                  </h3>
                  {contests.map((contest, index) => (
                    <div
                      key={index}
                      className="row bg-white text-black mb-3 rounded-3 w-80 mx-auto align-items-center"
                    >
                      <div className="col-lg-6 ">{contest.title}</div>
                      <div className="col-lg-4 ">{contest.status}</div>
                      <div className="col-lg-2 ">
                        <Link to={`/home/contests/${contest.id}`}>
                          <OverlayTrigger
                            placement="top"
                            overlay={<Tooltip>Ver detalhes</Tooltip>}
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <BsEyeFill />
                            </span>
                          </OverlayTrigger>
                        </Link>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="row mt-5">
                <h5 className="text-white">Não há concursos activos</h5>
              </div>
            )}
          </div>

          <div class="row mt-5 mb-5 d-flex justify-content-start">
            {ownProj !== null && ownProj.id !== 0 ? (
              <div className="col-8 col-sm-10 col-md-7 col-lg-5 mx-auto bg-secondary mt-5 rounded-5 ">
                <div>
                  <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
                    Projecto activo
                  </h3>
                  <div className="row bg-white text-black mb-3 rounded-3 w-80 mx-auto align-items-center">
                    <div className="col-lg-6 ">{ownProj.title}</div>
                    <div className="col-lg-4 ">{ownProj.status}</div>
                    <div className="col-lg-2 ">
                      <Link to={`/home/projects/${ownProj.id}`}>
                        <OverlayTrigger
                          placement="top"
                          overlay={<Tooltip>Ver detalhes</Tooltip>}
                        >
                          <span
                            data-bs-toggle="tooltip"
                            data-bs-placement="top"
                          >
                            {" "}
                            <BsEyeFill />
                          </span>
                        </OverlayTrigger>
                      </Link>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div className="row mt-5 ">
                <h5 className="text-white">Não tem projecto activo</h5>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Start;
