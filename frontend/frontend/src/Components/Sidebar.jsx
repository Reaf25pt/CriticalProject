import { Link, Outlet } from "react-router-dom";
import {
  BsFillHouseFill,
  BsClipboardFill,
  BsTrophyFill,
  BsWechat,
  BsEnvelopeFill,
} from "react-icons/bs";

function Sidebar() {
  return (
    <div className="container-fluid ">
      <div className="row flex-nowrap min-vh-100">
        <div
          className="col-auto col-md-3 col-xl-2 px-sm-2 px-0 "
          style={{ background: "#C01722" }}
        >
          <div className="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2 text-white  ">
            <Link
              href="/"
              className="d-flex align-items-center pb-3 mb-md-0 me-md-auto text-white text-decoration-none"
            >
              <span className="fs-5 d-none d-sm-inline  ">
                Laboratório de Inovação
                <hr className="hr" />
              </span>
            </Link>
            <ul
              className="nav nav-pills flex-column mb-sm-auto mb-0 align-items-center align-items-sm-start"
              id="menu"
            >
              <li className="nav-item ">
                <Link to={"/home"} className="nav-link align-middle px-0">
                  <BsFillHouseFill color="white" />{" "}
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Inicio
                  </span>
                </Link>
              </li>
              <li className="nav-item">
                <Link
                  to={"/home/projects"}
                  className="nav-link align-middle px-0"
                >
                  <BsClipboardFill color="white" />
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Projetos
                  </span>
                </Link>
              </li>
              <li className="nav-item">
                <Link
                  to={"/home/contests"}
                  className="nav-link align-middle px-0"
                >
                  <BsTrophyFill color="white" />
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Concursos
                  </span>
                </Link>
                <Link to={"/home/chat"} className="nav-link align-middle px-0">
                  <BsWechat color="white" />
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Mensagens
                  </span>
                </Link>
                <Link
                  to={"/home/notifications"}
                  className="nav-link align-middle px-0"
                >
                  <BsEnvelopeFill color="white" />{" "}
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Notificações
                  </span>
                </Link>
              </li>
            </ul>
          </div>
          <hr style={{ borderColor: "white" }} />
          <div class="dropdown pb-4">
            <a
              href="#"
              class="d-flex align-items-center text-white text-decoration-none dropdown-toggle"
              id="dropdownUser1"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <img
                src="https://github.com/mdo.png"
                alt="hugenerd"
                width="30"
                height="30"
                class="rounded-circle"
              />
              <span class="d-none d-sm-inline mx-1">Rodrigo Ferreira</span>
            </a>
            <ul
              class="dropdown-menu dropdown-menu-dark text-small shadow"
              aria-labelledby="dropdownUser1"
            >
              <li>
                <Link class="dropdown-item" to={"/home/profile"}>
                  Perfil
                </Link>
              </li>
              <li>
                <hr class="dropdown-divider" />
              </li>
              <li>
                <Link class="dropdown-item" to="/">
                  Logout
                </Link>
              </li>
            </ul>
          </div>
        </div>

        <div className="col py-3 bg-dark">
          <Outlet />{" "}
        </div>
      </div>
    </div>
  );
}

export default Sidebar;
