import { useState, useEffect } from "react";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import ButtonComponent from "../Components/ButtonComponent";
import { BsXLg, BsSearch } from "react-icons/bs";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { projOpenStore } from "../stores/projOpenStore";

function ProjectAllTasksSelect({
  preReqTasks,
  setPreReqTasks,
  addPreReqTask,
  editTaskId,
  // triggerList,
  // resetInput,
}) {
  const user = userStore((state) => state.user);
  const tasks = projOpenStore((state) => state.tasks);
  const setTasks = projOpenStore((state) => state.setTasks);
  const [credentials, setCredentials] = useState({});
  const [task, setTask] = useState({});
  const { id } = useParams();

  const clearInputFields = () => {
    document.getElementById("preRequiredTasks").value = "-1";
  };

  const handleChange = (event) => {
    const selectedTask = event.target.options[event.target.selectedIndex];
    const selectedTitle = selectedTask.text;
    const selectedId = selectedTask.value;

    if (selectedId !== "-1") {
      var taskSelected = { id: selectedId, title: selectedTitle };

      setTask(taskSelected);
    }
  };

  const handleClick = (event) => {
    event.preventDefault();

    if (Object.keys(task).length === 0) {
      alert("Seleccione uma tarefa, se aplicável");
    } else {
      //var task = { id: credentials.id, title: credentials.title };

      addPreReqTask(task);

      setTask({});
    }
    setTask({});
    clearInputFields();
  };

  const removeTask = (position) => {
    setPreReqTasks((prevTasks) => {
      const updateList = [...prevTasks];
      updateList.splice(position, 1);
      return updateList;
    });
  };

  return (
    <div className="container">
      <div className="row">
        <div className="col-lg-8">
          <select
            name="preRequiredTasks"
            id="preRequiredTasks"
            onChange={handleChange}
            // onClick={handleChange}
            placeholder={"Tarefas precedentes "}
            local={"Tarefas precedentes "}
            className="form-control col-lg-8"

            /*  listMembers={props.listMembers}
        projId={props.projId} */
          >
            <option value={"-1"}>{"Tarefas precedentes "} </option>
            {/*   {Object.entries(projTasks).map(([key, task]) => ( */}
            {tasks
              .filter((task) => {
                return (
                  task.id !== editTaskId && task.title !== "Apresentação final"
                );
              })
              .map((task) => (
                <option key={task.id} value={task.id}>
                  {task.title}
                </option>
              ))}
          </select>
        </div>

        <div className="col-lg-3">
          <OverlayTrigger
            placement="top"
            overlay={<Tooltip>Adicionar tarefa precedente</Tooltip>}
          >
            <span data-bs-toggle="tooltip" data-bs-placement="top">
              {" "}
              <ButtonComponent onClick={handleClick} name={"+"} />
            </span>
          </OverlayTrigger>
        </div>
      </div>
      {preReqTasks && preReqTasks.length > 0 ? (
        <div className="row bg-white  p-2 mx-auto rounded-2 mt-3 mb-3 ">
          <div className="form-outline  ">
            <div className="d-flex ">
              {preReqTasks.map((item, position) => (
                <>
                  <div className="bg-secondary text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                    {item.title}{" "}
                    <div className="">
                      <BsXLg onClick={() => removeTask(position)} />
                    </div>
                  </div>
                </>
              ))}
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

export default ProjectAllTasksSelect;
