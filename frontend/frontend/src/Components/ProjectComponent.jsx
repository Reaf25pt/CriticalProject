import style from "./projectcomponent.module.css";

function ProjectComponent() {
  return (
    <div className={style.boxproject}>
      <p>Data de criação</p>
      <p>"Nome do Projeto"</p>
      <p>Estado</p>
      <p>5</p>
      <p>IDE</p>
    </div>
  );
}

export default ProjectComponent;
