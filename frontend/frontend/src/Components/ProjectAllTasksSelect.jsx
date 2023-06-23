import { useState, useEffect } from "react";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";

function ProjectAllTasksSelect(props) {
  const user = userStore((state) => state.user);
  const [projTasks, setProjTasks] = useState([]);

  const { id } = useParams();

  useEffect(() => {
    console.log(props.listMembers);
    console.log(props.projId);

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
        setProjTasks(data);
      })
      .catch((err) => console.log(err));
  }, []);

  return (
    <div className="arrow-select-container">
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
        placeholder={props.placeholder}
        className="form-control"
        taskList={props.taskList}
        /*  listMembers={props.listMembers}
        projId={props.projId} */
      >
        <option value="-1">{props.placeholder} </option>
        {/*  {Object.entries(props.listMembers).map(([key, member]) => ( */}
        {projTasks.map((task) => (
          <option key={task.id} value={task}>
            {task.title}
          </option>
        ))}
      </select>
    </div>
  );
}

export default ProjectAllTasksSelect;
