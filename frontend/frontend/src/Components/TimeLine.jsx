import { useEffect, useState } from "react";
import ButtonComponent from "./ButtonComponent";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";
import { useParams } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import TextAreaComponent from "./TextAreaComponent";
import { Timeline } from "primereact/timeline";
import { projOpenStore } from "../stores/projOpenStore";
import { toast, Toaster } from "react-hot-toast";

function TimeLine() {
  const user = userStore((state) => state.user);
  const [recordList, setRecordList] = useState([]);

  const { id } = useParams();
  const [credentials, setCredentials] = useState({});
  const [newRecord, setNewRecord] = useState([]);
  const project = projOpenStore((state) => state.project);
  const tasks = projOpenStore((state) => state.tasks);

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/project/${id}/record`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setRecordList(data);
      })
      .catch((err) => console.log(err));
  }, [newRecord]);

  // const customizedMarker = (item) => {
  //   return (
  //     <span className="flex w-2rem h-2rem align-items-center justify-content-center text-white border-circle z-1 shadow-1">
  //       <i className={item.icon}></i>
  //     </span>
  //   );
  // };

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "numeric", day: "numeric" };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  const customizedContent = (item) => {
    return (
      <div className="card bg-secondary text-white text-center ">
        <div className="card-body">
          <h5 className="card-title">
            {item.authorFirstName} {item.authorLastName}
          </h5>
          <hr />
          {item.taskId === 0 ? (
            <h8>{formatDate(item.creationTime)}</h8>
          ) : (
            <div>
              <h4>{item.taskTitle}</h4>
              <h8>{formatDate(item.creationTime)} </h8>
            </div>
          )}
          {/*   <h8>{formatDate(item.creationTime)}</h8> */}
          <hr />
          <h8 class="card-text">{item.message}</h8>
        </div>
      </div>
    );
  };

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const addRecord = (event) => {
    event.preventDefault();

    if (!credentials.record || credentials.record === "") {
      toast.error("Tem de inserir texto");
    } else {
      var newRecord = {
        taskId: credentials.task,
        message: credentials.record,
      };

      fetch(`http://localhost:8080/projetofinal/rest/project/${id}/record`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
        },
        body: JSON.stringify(newRecord),
      })
        .then((response) => {
          if (response.status === 200) {
            return response.json();
          }
        })
        .then((response) => {
          setNewRecord(response);
        });
      document.getElementById("record").value = "";
      document.getElementById("task").value = "0";
    }
  };

  return (
    <div
      class="container-fluid"
      /*    style={{
        maxHeight: "700px",
        marginTop: "20px",
        overflowY: "auto",
      }} */
    >
      <Toaster position="top-right" />

      <div className="row mt-5 d-flex justify-content-around">
        {project.statusInt === 4 ? (
          <div className="col-lg-4">
            <div className="bg-secondary p-5 rounded-5">
              <select
                name="task"
                id="task"
                placeholder={"Escolha tarefa "}
                local={"Escolha tarefa "}
                //taskList={showTasks}
                onChange={handleChange}
              >
                <option value="0">Escolha tarefa</option>
                {tasks.map((task) => (
                  <option key={task.id} value={task.id}>
                    {task.title}
                  </option>
                ))}{" "}
              </select>

              <div className="row mt-3 mb-3">
                <TextAreaComponent
                  placeholder={
                    "Registar ocorrência * \nPode associar uma tarefa à qual o registo diz respeito"
                  }
                  id="record"
                  name="record"
                  required
                  type="text"
                  onChange={handleChange}
                />
              </div>
              <div className="row ">
                <div className="col-lg-12 mx-auto">
                  <ButtonComponent name="Registar" onClick={addRecord} />
                </div>
              </div>
            </div>
          </div>
        ) : null}

        {recordList && recordList.length > 0 ? (
          <div
            className="col-lg-6"
            style={{
              maxHeight: "700px",
              marginTop: "20px",
              overflowY: "auto",
            }}
          >
            <Timeline
              value={recordList}
              align="alternate"
              className="customized-timeline"
              // marker={customizedMarker}
              content={customizedContent}
            />
          </div>
        ) : (
          <h5 className="text-white">
            Não há histórico de registos para apresentar
          </h5>
        )}
      </div>
    </div>
  );
}

export default TimeLine;
