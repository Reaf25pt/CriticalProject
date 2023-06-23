import React, { useEffect, useState } from "react";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import {
  Gantt,
  Task,
  EventOption,
  StylingOption,
  ViewMode,
  DisplayOption,
  TaskListTable,
} from "gantt-task-react";
import "gantt-task-react/dist/index.css";
import ProjectMembersSelect from "./ProjectMembersSelect";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";

function FormTask(listMembers) {
  const user = userStore((state) => state.user);
  const [activeId, setActiveId] = useState(null);
  const [credentials, setCredentials] = useState([]);

  const [showTasks, setShowTasks] = useState([]);
  const [task, setTask] = useState([]);
  const { id } = useParams();

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

  function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const formattedDate = `${year}-${month}-${day}`;
    return formattedDate;
  }

  const mappedTasks = showTasks.map((task) => ({
    id: task.id,
    name: task.title,
    start: new Date(formatTimestamp(task.startDate)),
    end: new Date(formatTimestamp(task.finishDate)),
  }));

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
    document.getElementById("title").value = " ";
    document.getElementById("startDate").value = " ";
    document.getElementById("finishDate").value = " ";
    document.getElementById("details").value = " ";
    document.getElementById("taskOwnerId").value = "-1";
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
      alert("Insira os dados assinalados como obrigatórios");
    } else if (credentials.startDate >= credentials.finishDate) {
      alert("Insira uma data de fim posterior à data de início indicada");
    } else {
      var newTask = credentials;
      newTask.preRequiredTasks = [];

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
            setCredentials([]);
            return response.json();
            //navigate("/home", { replace: true });
          } else {
            alert("Algo correu mal. Tente novamente");
          }
        })
        .catch((err) => console.log(err));
    }
  };

  return (
    <div className="container-fluid mt-5">
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
                <div className="col-lg-6">
                  <InputComponent
                    placeholder={"Executores adicionais "}
                    id="additionalExecutors"
                    name="additionalExecutors"
                    type="text"
                    onChange={handleChange}
                  />
                </div>
                <div className="col-lg-6">
                  <ProjectAllTasksSelect
                    name="preRequiredTasks"
                    id="preRequiredTasks"
                    onChange={handleChange}
                    placeholder={"Tarefas precedentes "}
                    local={"Tarefas precedentes "}
                    taskList={task}
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
      <div className="row mt-4">
        <div className="col-lg-2 bg-secondary rounded-5 p-3">
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
                          <div className="row d-flex ">
                            <div className="row">
                              <button>Eliminar</button>
                              <button>Editar</button>
                              <select name="" id=""></select>
                            </div>
                            <div className="row">
                              <h5 className="row mb-3">
                                {" "}
                                Data de Inicio: {formatDate(task.startDate)}
                              </h5>
                              <h5 className="row mb-3">
                                {" "}
                                Data de Fim: {formatDate(task.finishDate)}
                                <hr />
                              </h5>
                            </div>

                            <div className="row">
                              {" "}
                              <h4 className="p-0">Descrição:</h4>
                              {task.details}
                              <hr />
                            </div>
                            <div className="row">
                              <h4 className="p-0">Executores Adicionais:</h4>{" "}
                              {task.additionalExecutors}
                              <hr />
                            </div>
                            <h4 className="row d-flex justify-content-end">
                              Responsavel: {task.taskOwnerFirstName}{" "}
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
        {/* <div className="col-lg-12 bg-white mx-auto w-50 ">
          {" "}
          {showTasks && showTasks.length > 0 && (
            <>
              <Gantt
                tasks={mappedTasks}
                startDate="2023-01-01"
                endDate="2023-12-30"
                viewMode="Day"
                barBackgroundColor="red"
              />
            </>
          )}
        </div> */}
      </div>
    </div>
  );
}

export default FormTask;
