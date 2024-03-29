import React, { useEffect, useState } from "react";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import { Chart } from "react-google-charts";
import {
  BsFillArrowRightSquareFill,
  BsPlayBtnFill,
  BsFillPatchCheckFill,
} from "react-icons/bs";

import ProjectMembersSelect from "./ProjectMembersSelect";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";
import ModalEditTask from "./ModalEditTask";
import ModalDeleteTask from "./ModalDeleteTask";
import { projOpenStore } from "../stores/projOpenStore";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

function FormTask() {
  const user = userStore((state) => state.user);
  const [activeId, setActiveId] = useState(null);
  const [credentials, setCredentials] = useState([]);
  const project = projOpenStore((state) => state.project);
  const tasks = projOpenStore((state) => state.tasks);
  const setTasks = projOpenStore((state) => state.setTasks);
  const members = projOpenStore((state) => state.members);

  const { id } = useParams();
  const [preReqTasks, setPreReqTasks] = useState([]); // lista para enviar para backend
  const addPreReqTask = (task) => {
    setPreReqTasks((state) => [...state, task]);
  };

  const [selectedTask, setSelectedTask] = useState(null);

  const toggleAccordion = (id) => {
    setActiveId(id === activeId ? null : id);
  };

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "numeric", day: "numeric" };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  const handleClicked = (taskId) => {
    if (selectedTask === taskId) {
      setSelectedTask(null);
    } else {
      setSelectedTask(taskId);
    }
  };
  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/project/tasks/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setTasks(data);
      })
      .catch((err) => console.log(err));
  }, []);

  function arrayToString(arr) {
    return arr.join(",");
  }

  const columns = [
    { type: "string", label: "Task ID" },
    { type: "string", label: "Task Name" },
    { type: "string", label: "Member" },
    { type: "date", label: "Start Date" },
    { type: "date", label: "End Date" },
    { type: "number", label: "Duration" },
    { type: "number", label: "Percent Complete" },
    { type: "string", label: "Dependencies" },
  ];

  function calculateDurationInDays(start, end) {
    const millisecondsPerDay = 24 * 60 * 60 * 1000; // Number of milliseconds in a day
    const percentage = 0;
    // Convert the timestamps to Date objects
    const date1 = new Date(start);
    const date2 = new Date(end);
    const now = new Date().getTime();

    // Calculate the difference in milliseconds between the two dates
    if (date1 <= now) {
      const differenceInMillisecondsstartend = Math.abs(date2 - date1);
      const differenceInMillisecondsnowend = Math.abs(date2 - now);

      // Calculate the duration in days
      const durationInDaysTasks = Math.floor(
        differenceInMillisecondsstartend / millisecondsPerDay
      );

      const durationInDaysTasksfinshed = Math.floor(
        differenceInMillisecondsnowend / millisecondsPerDay
      );
      const percentage =
        ((durationInDaysTasksfinshed + 1) / durationInDaysTasks) * 100;

      //return durationInDaysTasksfinshed + 1;
      //return durationInDaysTasks;

      return percentage;
    } else {
      return percentage;
    }
  }

  const rows = [
    ...tasks.map((task) => [
      task.id.toString(),
      task.title,
      task.taskOwnerFirstName,
      new Date(
        new Date(task.startDate).getFullYear(),
        new Date(task.startDate).getMonth(),
        new Date(task.startDate).getDate()
      ),
      new Date(
        new Date(task.finishDate).getFullYear(),
        new Date(task.finishDate).getMonth(),
        new Date(task.finishDate).getDate()
      ),
      null, //calculatePercentage(task.startDate, task.finishDate),
      calculateDurationInDays(task.startDate, task.finishDate),

      task.preRequiredTasks.length > 0
        ? arrayToString(task.preRequiredTasks.map((item) => item.id))
        : null,
    ]),
  ];

  const options = {
    height: 400,
    gantt: {
      trackHeight: 30,
      criticalPathEnabled: true,
    },
  };

  var data2 = [columns, ...rows];

  function convertWord(word) {
    // Convert the word to lowercase first
    var lowercaseWord = word.toLowerCase();

    // Capitalize the first letter and concatenate with the lowercase remainder of the word
    var convertedWord =
      lowercaseWord.charAt(0).toUpperCase() + lowercaseWord.slice(1);

    return convertedWord;
  }

  const clearInputFields = () => {
    //document.getElementById("titleInput").value = "";
    document.getElementById("title").value = "";
    document.getElementById("startDate").value = " ";
    document.getElementById("finishDate").value = " ";
    document.getElementById("details").value = "";
    document.getElementById("taskOwnerId").value = "-1";
    document.getElementById("additionalExecutors").value = "";

    // setInput("-1");
  };

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (
      !credentials.title ||
      !credentials.startDate ||
      !credentials.finishDate ||
      !credentials.details ||
      !credentials.taskOwnerId ||
      credentials.taskOwnerId === "-1"
    ) {
      alert("Insira os dados assinalados como obrigatórios");
    } else if (credentials.startDate >= credentials.finishDate) {
      alert("Insira uma data de fim posterior à data de início indicada");
    } else {
      var newTask = credentials;
      newTask.preRequiredTasks = preReqTasks;

      fetch(`http://localhost:8080/projetofinal/rest/project/${id}/task`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
        },
        body: JSON.stringify(newTask),
      })
        .then((response) => {
          if (response.status === 200) {
            return response.json();
          } else {
            throw new Error("Pedido não satisfeito");
          }
        })
        .then((data) => {
          setTasks(data);
        })
        .catch((err) => console.log(err));
      clearInputFields();
      setPreReqTasks([]);
      setCredentials([]);
    }
  };

  function handleClick(taskId, statusInfo) {
    var editTask = { id: taskId, statusInfo: statusInfo };

    console.log(editTask);

    fetch(`http://localhost:8080/projetofinal/rest/project/${id}/task`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
      body: JSON.stringify(editTask),
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
        } else {
          throw new Error("Pedido não satisfeito");
        }
      })
      .then((data) => {
        setTasks(data);
        alert("Estado da tarefa alterado");
      })
      .catch((error) => {
        alert(error.message);
      });
  }

  return (
    <div className="container-fluid mt-5">
      {project.manager &&
      (project.statusInt === 0 || project.statusInt === 4) ? (
        <div
          className="row d-flex justify-content-around bg-secondary 
          rounded-5 p-4"
        >
          <div className="col-lg-6">
            <div className="row ">
              <div className="col-lg-12 ">
                <div className="row mb-3">
                  <div className="col-lg-6">
                    <InputComponent
                      placeholder={"Título *"}
                      id="title"
                      required
                      name="title"
                      type="text"
                      onChange={handleChange}
                      defaultValue={""}
                    />
                  </div>
                  <div className="col-lg-6">
                    <ProjectMembersSelect
                      name="taskOwnerId"
                      id="taskOwnerId"
                      onChange={handleChange}
                      placeholder={"Membro responsável *"}
                      local={"Membro responsável *"}
                      // listMembers={members}
                      // projId={id}
                    />
                  </div>
                </div>
                <div className="row mb-3">
                  <div className="col-lg-6">
                    <label className="text-white">Data de início *</label>
                    <InputComponent
                      placeholder={" *"}
                      id="startDate"
                      required
                      name="startDate"
                      type="date"
                      onChange={handleChange}
                    />
                  </div>
                  <div className="col-lg-6">
                    <label className="text-white">Data de fim *</label>

                    <InputComponent
                      placeholder={" *"}
                      id="finishDate"
                      required
                      name="finishDate"
                      type="date"
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="row mb-3">
                  <InputComponent
                    placeholder={"Executores adicionais "}
                    id="additionalExecutors"
                    name="additionalExecutors"
                    type="text"
                    onChange={handleChange}
                    defaultValue={""}
                  />
                  <div className="row mt-3">
                    <ProjectAllTasksSelect
                      id="tst"
                      preReqTasks={preReqTasks}
                      setPreReqTasks={setPreReqTasks}
                      addPreReqTask={addPreReqTask}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4 d-flex align-items-center ">
            <TextAreaComponent
              placeholder={"Descrição da tarefa *"}
              id="details"
              name="details"
              required
              type="text"
              onChange={handleChange}
              defaultValue={""}
            />
          </div>{" "}
          <div className="col-lg-2 d-flex align-items-center">
            <ButtonComponent name={"Adicionar tarefa"} onClick={handleSubmit} />
          </div>
        </div>
      ) : null}
      <div className="row mt-4 ">
        {tasks.map((task) => (
          <div key={task.id}>
            <div className="row m-0">
              <div className="col-lg-5">
                <div
                  className={
                    task.taskOwnerId === user.userId
                      ? "row bg-white border border-5 border border-danger mb-3  mx-auto rounded-3 p-2 d-flex justify-content-around"
                      : "row bg-white mb-3  mx-auto rounded-3 p-2 d-flex justify-content-around"
                  }
                >
                  <div className="col-lg-7 m-0">
                    <h6> {task.title}</h6>
                  </div>
                  <div className="col-lg-3 p-1 rounded-5 bg-dark">
                    <h6 className="text-white text-center">
                      {convertWord(task.status)}
                    </h6>
                  </div>
                  <div className="col-lg-1">
                    <BsFillArrowRightSquareFill
                      size={30}
                      color="#A50D13"
                      onClick={() => handleClicked(task.id)}
                      cursor={"pointer"}
                    />
                  </div>
                </div>
              </div>
              <div className="col-lg-7 ">
                {selectedTask === task.id && (
                  <div className="bg-secondary rounded-3 p-2 card">
                    <div
                      className="row d-flex justify-content-around mb-2 p-2"
                      style={{ background: "#A50D13" }}
                    >
                      <div className="col-lg-5">
                        <h4 className="text-white">{task.title}</h4>
                      </div>
                      <div className="col-lg-4 d-flex justify-content-around">
                        {project.manager &&
                        (project.statusInt === 0 || project.statusInt === 4) &&
                        task.statusInfo !== 2 ? (
                          <ModalEditTask task={task} formatDate={formatDate} />
                        ) : null}
                        {project.manager &&
                        project.statusInt === 0 &&
                        task.statusInfo !== 2 ? (
                          <ModalDeleteTask task={task} />
                        ) : null}

                        {(project.manager ||
                          task.taskOwnerId === user.userId) &&
                        project.statusInt === 4 &&
                        task.statusInfo === 0 ? (
                          <div>
                            <OverlayTrigger
                              placement="top"
                              overlay={<Tooltip>Iniciar tarefa</Tooltip>}
                            >
                              <span
                                data-bs-toggle="tooltip"
                                data-bs-placement="top"
                              >
                                {" "}
                                <BsPlayBtnFill
                                  name={"statusInProgress"}
                                  size={40}
                                  cursor={"pointer"}
                                  onClick={() => handleClick(task.id, 1)}
                                  color="white"
                                />
                              </span>
                            </OverlayTrigger>
                          </div>
                        ) : task.statusInfo === 1 ? (
                          <div>
                            <OverlayTrigger
                              placement="top"
                              overlay={<Tooltip>Concluir tarefa</Tooltip>}
                            >
                              <span
                                data-bs-toggle="tooltip"
                                data-bs-placement="top"
                              >
                                {" "}
                                <BsFillPatchCheckFill
                                  name={"statusFinished"}
                                  // onClick={handleClick}
                                  size={40}
                                  cursor={"pointer"}
                                  onClick={() => handleClick(task.id, 2)}
                                  color="white"
                                />
                              </span>
                            </OverlayTrigger>
                          </div>
                        ) : null}
                      </div>
                    </div>
                    <div className="row">
                      <div className="col-lg-3 bg-dark">
                        <h4 className="text-white text-center">
                          Data de início:
                        </h4>
                      </div>
                      <div className="col-lg-6">
                        <h5 className="text-white">
                          {formatDate(task.startDate)}
                        </h5>
                      </div>
                      <hr />
                    </div>
                    <div className="row">
                      <div className="col-lg-3 bg-dark">
                        <h4 className="text-white text-center">Data de fim:</h4>
                      </div>
                      <div className="col-lg-6">
                        <h5 className="text-white">
                          {formatDate(task.finishDate)}
                        </h5>
                      </div>

                      <hr />
                    </div>
                    <div className="row">
                      <div className="col-lg-3 bg-dark">
                        <h4 className="text-white text-center">Descritivo:</h4>
                      </div>
                      <div className="col-lg-9 ">
                        <div className="row">
                          <h5 className="text-white ">{task.details}</h5>
                        </div>
                      </div>

                      <hr />
                    </div>
                    <div
                      className="row"
                      style={{ minHeight: "100px", maxHeight: "40px" }}
                    >
                      <div className="col-lg-3 bg-dark">
                        <h4 className="text-white text-center">
                          Executores Adicionais:
                        </h4>
                      </div>
                      <div className="col-lg-9">
                        <h5 className="text-white">
                          {task.additionalExecutors}
                        </h5>
                      </div>

                      <hr />
                    </div>
                    <div className="row">
                      <div className="col-lg-4 bg-dark">
                        <h4 className="text-white text-center">Responsável:</h4>
                      </div>
                      <div className="col-lg-8">
                        <h5 className="text-white">
                          {task.taskOwnerFirstName} {task.taskOwnerLastName}{" "}
                        </h5>
                      </div>

                      <hr />
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="row mt-4   p-3 rounded-4 ">
        {tasks && tasks.length > 0 && (
          <Chart
            chartType="Gantt"
            data={data2}
            options={options}
            width="100%"
            height="50%"
          />
        )}
      </div>
    </div>
  );
}

export default FormTask;
