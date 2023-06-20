import React, { useEffect, useState } from "react";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Accordion, AccordionTab } from "primereact/accordion";

function FormTask() {
  const user = userStore((state) => state.user);
  const [activeId, setActiveId] = useState(null);

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

  function convertWord(word) {
    // Convert the word to lowercase first
    var lowercaseWord = word.toLowerCase();

    // Capitalize the first letter and concatenate with the lowercase remainder of the word
    var convertedWord =
      lowercaseWord.charAt(0).toUpperCase() + lowercaseWord.slice(1);

    return convertedWord;
  }

  return (
    <div className="container-fluid mt-5 vh-75">
      <div>
        <form
          className="row d-flex justify-content-around bg-secondary 
          rounded-5 pt-3
        "
        >
          <div className="col-lg-4">
            <div className="row ">
              <div className="col-lg-12 ">
                <div className="row mb-3">
                  <div className="col-lg-6">
                    <InputComponent placeholder={"Titulo"} />
                  </div>
                  <div className="col-lg-6">
                    <SelectComponent local={"Membro Responsavel"} />
                  </div>
                </div>
                <div className="row mb-3">
                  <div className="col-lg-6">
                    <label className="text-white">Data de Inicio:</label>
                    <InputComponent placeholder={"Data Inicio"} type="date" />
                  </div>
                  <div className="col-lg-6">
                    <label className="text-white">Data de Fim:</label>

                    <InputComponent placeholder={"Data Fim"} type="date" />
                  </div>
                </div>
                <div className="row mb-3">
                  <div className="col-lg-6">
                    <InputComponent placeholder={"Executores Adicionais"} />
                  </div>
                  <div className="col-lg-6">
                    <SelectComponent
                      placeholder={"Tarefas Precedentes"}
                      local={"Tarefas Precedentes"}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12 col-sm-12 col-md-12 col-lg-6 d-flex align-items-center ">
            <textarea
              class="text-dark bg-white rounded-2 w-100 h-75 "
              placeholder="Descrição da Tarefa"
              name="bio"
              type="text"
            ></textarea>
          </div>{" "}
          <div className="col-lg-1 d-flex align-items-center">
            <ButtonComponent name={"Adicionar"} />
          </div>
        </form>
        <div className="row mt-4">
          <div className="col-lg-5 bg-secondary rounded-5 p-3">
            <div className="">
              <h3 className="bg-white rounded-5 text-center">
                Lista de Tarefas
              </h3>
              <div>
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
                            <h4 className="row mb-3">
                              {" "}
                              Data de Inicio: {formatDate(task.startDate)}
                            </h4>
                            <h4 className="row mb-3">
                              {" "}
                              Data de Fim: {formatDate(task.finishDate)}
                              <hr />
                            </h4>
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
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default FormTask;
