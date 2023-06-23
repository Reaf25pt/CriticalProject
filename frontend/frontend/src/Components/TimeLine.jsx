import { useEffect, useState } from "react";
import ButtonComponent from "./ButtonComponent";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";
import { useParams } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import TextAreaComponent from "./TextAreaComponent";
import { Timeline } from "primereact/timeline";

function TimeLine() {
  const user = userStore((state) => state.user);
  const [projTasks, setProjTasks] = useState([]);
  const [showTasks, setShowTasks] = useState([]);
  const [task, setTask] = useState([]);
  const { id } = useParams();

  useEffect(() => {
    console.log(id);

    fetch(`http://localhost:8080/projetofinal/rest/project/tasks/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        console.log("Lista de Tarefas");
        console.log(data);
        setShowTasks(data);
      })
      .catch((err) => console.log(err));
  }, [task]);

  return (
    <div
      class="container-fluid 
  
  "
    >
      <div className="row mt-5">
        <form className="bg-secondary w-25 p-5 rounded-5 ">
          <div className="row mb-4">
            <ProjectAllTasksSelect
              name="preRequiredTasks"
              id="preRequiredTasks"
              placeholder={"Tarefas precedentes "}
              local={"Tarefas precedentes "}
              taskList={showTasks}
            />
          </div>
          <div className="row mb-4">
            <TextAreaComponent />
          </div>
          <div className="row">
            <ButtonComponent name="Adicionar" />
          </div>
        </form>
      </div>
      <div className="row"></div>
    </div>
  );
}

export default TimeLine;
