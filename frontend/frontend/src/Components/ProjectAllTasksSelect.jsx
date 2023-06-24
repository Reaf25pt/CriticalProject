import { useState, useEffect } from "react";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import ButtonComponent from "../Components/ButtonComponent";
import { BsXLg, BsSearch } from "react-icons/bs";

function ProjectAllTasksSelect({
  preReqTasks,
  setPreReqTasks,
  addPreReqTask,
  // resetInput,
}) {
  const user = userStore((state) => state.user);
  const [projTasks, setProjTasks] = useState([]);
  const [credentials, setCredentials] = useState({});
  const [task, setTask] = useState({});
  const { id } = useParams();

  const clearInputFields = () => {
    document.getElementById("preRequiredTasks").value = "-1";
  };

  useEffect(() => {
    console.log(task);

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

  const handleChange = (event) => {
    const selectedTask = event.target.options[event.target.selectedIndex];
    const selectedTitle = selectedTask.text;
    const selectedId = selectedTask.value;

    if (selectedId !== "-1") {
      var taskSelected = { id: selectedId, title: selectedTitle };

      setTask(taskSelected);
    }
    console.log("Selected Title:", selectedTitle);
    console.log("Selected Id:", selectedId);
    console.log(taskSelected);

    /*     const name = event.target.name;
    const value = event.target.value;
    console.log(value);
    setCredentials((values) => {
      return { ...values, [name]: value };
    }); */
  };

  const handleClick = (event) => {
    event.preventDefault();

    console.log(task);
    if (Object.keys(task).length === 0) {
      alert("Seleccione uma tarefa, se aplicável");
    } else {
      //var task = { id: credentials.id, title: credentials.title };
      console.log(task);

      addPreReqTask(task);
      console.log(preReqTasks);

      setTask({});
    }
    setTask({});
    clearInputFields();
  };

  const removeTask = (position) => {
    console.log(position);

    setPreReqTasks((prevTasks) => {
      const updateList = [...prevTasks];
      updateList.splice(position, 1);
      console.log(updateList);
      return updateList;
    });
  };

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-lg-6">
          <select
            name="preRequiredTasks"
            id="preRequiredTasks"
            onChange={handleChange}
            // onClick={handleChange}
            placeholder={"Tarefas precedentes "}
            local={"Tarefas precedentes "}
            className="form-control"

            /*  listMembers={props.listMembers}
        projId={props.projId} */
          >
            <option value={"-1"}>{"Tarefas precedentes "} </option>
            {/*   {Object.entries(projTasks).map(([key, task]) => ( */}
            {projTasks.map((task) => (
              <option key={task.id} value={task.id}>
                {task.title}
              </option>
            ))}
          </select>
        </div>

        <div className="col-lg-3">
          <ButtonComponent onClick={handleClick} name={"+"} />
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
