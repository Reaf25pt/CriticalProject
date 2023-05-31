import { Col, Container, Row } from "react-bootstrap";
import style from "./sidebar.module.css";
import { Link, Outlet } from "react-router-dom";

function Sidebar() {
  return (
    <div className="me-5">
      <div>
        <div className={style.sidebar}>
          <div className={style.box}>
            <Link className={style.linksidebar} to={"/home"}>
              Inicio
            </Link>
          </div>
          <div className={style.box}>
            <Link className={style.linksidebar} to={"/home/projects"}>
              Projetos
            </Link>
          </div>
          <div className={style.box}>
            <Link className={style.linksidebar} to={"/home/contests"}>
              Concursos
            </Link>
          </div>
          <div className={style.box}>
            <Link className={style.linksidebar} to={"/home/profile"}>
              Perfil
            </Link>
          </div>
        </div>{" "}
      </div>
    </div>
  );
}

export default Sidebar;
