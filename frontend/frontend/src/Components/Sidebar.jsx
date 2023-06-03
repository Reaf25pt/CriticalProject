import { Col, Container, Nav, Row } from "react-bootstrap";
import style from "./sidebar.module.css";
import { Link, Outlet } from "react-router-dom";

function Sidebar() {
  return (
    <div>
      <div>
        <Nav className={style.sidebar}>
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
        </Nav>{" "}
      </div>
    </div>
  );
}

export default Sidebar;
