import React, { useEffect, useState } from "react";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import { Chart } from "react-google-charts";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

import ProjectMembersSelect from "./ProjectMembersSelect";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";
import ModalEditTask from "./ModalEditTask";
import ModalDeleteTask from "./ModalDeleteTask";

function FormTask(listMembers) {
  const user = userStore((state) => state.user);
  const [activeId, setActiveId] = useState(null);
  const [credentials, setCredentials] = useState([]);
  const [projInfo, setProjInfo] = useState([]);
  const [showTasks, setShowTasks] = useState([]);
  const [task, setTask] = useState([]);
  const { id } = useParams();
  const [preReqTasks, setPreReqTasks] = useState([]); // lista para enviar para backend
  const addPreReqTask = (task) => {
    setPreReqTasks((state) => [...state, task]);
  };
  // const [input, setInput] = useState("-1");
  const [triggerList, setTriggerList] = useState("-1");

  const toggleAccordion = (id) => {
    setActiveId(id === activeId ? null : id);
  };

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

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

  console.log("calculatePercentage");
  console.log(calculateDurationInDays(1687741200000, 1688086800000));

  const rows = [
    ...showTasks.map((task) => [
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

  console.log("rows");
  console.log(rows);

  var data2 = [columns, ...rows];

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/project/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setProjInfo(data);
      })
      .catch((err) => console.log(err));
  }, []);

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

    console.log(credentials);

    if (
      !credentials.title ||
      !credentials.startDate ||
      !credentials.finishDate ||
      !credentials.details ||
      !credentials.taskOwnerId ||
      credentials.taskOwnerId === "-1"
    ) {
      console.log(credentials);
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
            setTask([]);
            setPreReqTasks([]);
            setCredentials([]);
            alert(
              "Atenção, a(s) tarefa(s) precedentes só serão admitidas se a data final for anterior à data de início da tarefa adicionada"
            );
            return response.json();
            //navigate("/home", { replace: true });
          } else {
            alert("Algo correu mal. Tente novamente");
          }
        })
        .catch((err) => console.log(err));
      clearInputFields();
      setPreReqTasks([]);
      setCredentials([]);
      setTriggerList("");
    }
  };

  const handleClick = (event) => {
    console.log(activeId);
    if (event.target.name === "statusInProgress") {
      var editTask = {
        id: activeId,
        statusInfo: 1,
      };
    } else if (event.target.name === "statusFinished") {
      var editTask = {
        id: activeId,
        statusInfo: 2,
      };
    }
    console.group(editTask);

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
          setTask([]);
          alert("Status alterado");
          return response.json();
          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
      })
      .catch((err) => console.log(err));
  };

  return (
    <div className="container-fluid mt-5">
      {!user.contestManager ? (
        <div
          className="row d-flex justify-content-around bg-secondary 
          rounded-5 p-4"
        >
          <div className="col-lg-4">
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
                      listMembers={listMembers}
                      projId={id}
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
                      triggerList={triggerList}
                      //  resetInput={input}
                      /* name="preRequiredTasks"
                    id="preRequiredTasks"
                    onChange={handleChange}
                    placeholder={"Tarefas precedentes "}
                    local={"Tarefas precedentes "}
                    taskList={task} */
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-6 d-flex align-items-center ">
            <TextAreaComponent
              placeholder={"Descrição da tarefa *"}
              id="details"
              name="details"
              required
              type="text"
              onChange={handleChange}
              defaultValue={""}
            />
            {/*    <textarea
              class="text-dark bg-white rounded-2 w-100 h-75 "
              placeholder="Descrição da Tarefa"
              name="bio"
              type="text"
            ></textarea> */}
          </div>{" "}
          <div className="col-lg-1 d-flex align-items-center">
            <ButtonComponent name={"Adicionar tarefa"} onClick={handleSubmit} />
          </div>
        </div>
      ) : null}
      <div className="row mt-4">
        <div className="col-lg-6 bg-secondary rounded-5 p-3">
          <div className="">
            <h3 className="bg-white rounded-5 text-center">Lista de Tarefas</h3>
            <div>
              {showTasks && showTasks.length !== 0 ? (
                <div className="accordion ">
                  {showTasks.map((task) => (
                    <div className="accordion-item " key={task.id}>
                      <div className="accordion-header  ">
                        <div className="row d-flex justify-content-between  ">
                          <button
                            style={{ background: "#C01722" }}
                            className={`accordion-button text-white d-flex justify-content-between  ${
                              activeId === task.id ? "active" : ""
                            }`}
                            type="button"
                            onClick={() => toggleAccordion(task.id)}
                          >
                            <div className="col-lg-9"> {task.title}</div>
                            <div className="bg-dark p-1">
                              {convertWord(task.status)}
                            </div>
                          </button>
                        </div>
                      </div>
                      <div
                        className={`accordion-collapse collapse  ${
                          activeId === task.id ? "show" : ""
                        }`}
                      >
                        <div className="accordion-body ">
                          {" "}
                          <div className="row d-flex justify-content-between ">
                            <div className="col-lg-10">
                              <h5 className="row mb-3">
                                {" "}
                                Início: {formatDate(task.startDate)}
                              </h5>
                              <h5 className="row mb-3">
                                {" "}
                                Fim: {formatDate(task.finishDate)}
                              </h5>
                            </div>
                            <div className="col-lg-2 d-flex">
                              {!user.contestManager &&
                              task.statusInfo !== 2 &&
                              (projInfo.statusInt === 0 ||
                                projInfo.statusInt === 4) ? (
                                /*  <button>Editar</button> */

                                <ModalEditTask
                                  task={task}
                                  set={setTask}
                                  formatDate={formatDate}
                                  setTriggerList={setTriggerList}
                                />
                              ) : null}
                              {!user.contestManager &&
                              task.statusInfo !== 2 &&
                              projInfo.statusInt === 0 ? (
                                <ModalDeleteTask
                                  task={task}
                                  set={setTask}
                                  setTriggerList={setTriggerList}
                                />
                              ) : null}

                              {!user.contestManager &&
                              projInfo.statusInt === 4 &&
                              task.statusInfo === 0 ? (
                                <button
                                  name={"statusInProgress"}
                                  onClick={handleClick}
                                >
                                  Iniciar execução
                                </button>
                              ) : task.statusInfo === 1 ? (
                                <button
                                  name={"statusFinished"}
                                  onClick={handleClick}
                                >
                                  Tarefa concluída
                                </button>
                              ) : null}
                            </div>

                            <div className="row">
                              {" "}
                              <h4 className="p-0">Descrição:</h4>
                              {task.details}
                              <hr />
                            </div>

                            {task.additionalExecutors ? (
                              <div className="row">
                                <h4 className="p-0">Executores adicionais:</h4>{" "}
                                {task.additionalExecutors}
                                <hr />
                              </div>
                            ) : null}

                            {task.preRequiredTasks.length > 0 ? (
                              <div className="row">
                                {" "}
                                Tarefas precedentes:
                                {task.preRequiredTasks.map((item) => (
                                  <div key={item.id} className="p-0">
                                    {item.title} {item.status}
                                  </div>
                                ))}
                              </div>
                            ) : null}

                            <h4 className="row d-flex justify-content-end">
                              Responsável: {task.taskOwnerFirstName}{" "}
                              {task.taskOwnerLastName}
                            </h4>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}{" "}
                </div>
              ) : (
                <p>Não há tarefas definidas</p>
              )}
            </div>
          </div>
        </div>
        <div className="row mt-4 w-75 bg-secondary p-3 rounded-4 mx-auto">
          {showTasks && showTasks.length > 0 && (
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
    </div>
  );
}

export default FormTask;
