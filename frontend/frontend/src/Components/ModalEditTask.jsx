import React from "react";
import { useParams } from "react-router-dom";

import { useState } from "react";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { BsArrowDown, BsSearch, BsXLg } from "react-icons/bs";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import SelectComponent from "./SelectComponent";
import TextAreaComponent from "./TextAreaComponent";
import ProjectMembersSelect from "./ProjectMembersSelect";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";
import { BsFillPencilFill } from "react-icons/bs";

import { userStore } from "../stores/UserStore";
import Modal from "react-bootstrap/Modal";

function ModalEditTask({ task, set, formatDate, setTriggerList }) {
  const [show, setShow] = useState(false);
  const user = userStore((state) => state.user);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const [credentials, setCredentials] = useState(task);
  const { id } = useParams();
  const [preReqTasks, setPreReqTasks] = useState(task.preRequiredTasks); // lista para enviar para backend
  const addPreReqTask = (task) => {
    setPreReqTasks((state) => [...state, task]);
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
    console.log(formatDate(credentials.startDate));
    console.log(preReqTasks.length);

    var editedTask = credentials;
    editedTask.preRequiredTasks = preReqTasks;

    fetch(`http://localhost:8080/projetofinal/rest/project/${id}/task`, {
      method: "PATCH",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
      },
      body: JSON.stringify(editedTask),
    }).then((response) => {
      if (response.status === 200) {
        set([]); // reset a lista da pagina de tasks para actualizar
        setTriggerList(""); // para actualizar lista de tarefas a apresentar no adicionar tarefa
        handleClose();
      } else if (response.status === 403) {
        alert("Não tem autorização para efectuar este pedido");
        /*  } else if (response.status === 404) {
        alert("Actividade não encontrada"); */
      } else {
        alert("Algo correu mal");
      }
    });
  };

  return (
    <>
      <OverlayTrigger placement="top" overlay={<Tooltip>Editar</Tooltip>}>
        <span data-bs-toggle="tooltip" data-bs-placement="top">
          {" "}
          <BsFillPencilFill onClick={handleShow} size={40} color="green" />
        </span>
      </OverlayTrigger>

      <Modal
        show={show}
        onHide={handleClose}
        backdrop="static"
        keyboard={false}
        size="xl"
      >
        <Modal.Header closeButton>
          <Modal.Title>
            Editar tarefa
            {/* <FormattedMessage
              id="deleteTaskDetailModalTitle.tooltip"
              defaultMessage="Apagar actividade"
            /> */}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
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
                        defaultValue={task.title}
                      />
                    </div>
                    <div className="col-lg-6">
                      <ProjectMembersSelect
                        name="taskOwnerId"
                        id="taskOwnerId"
                        onChange={handleChange}
                        placeholder={"Membro responsável *"}
                        local={"Membro responsável *"}
                        //  listMembers={listMembers}
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
                        // defaultValue={formatDate(task.startDate)}
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
                        // defaultValue={formatDate(task.finishDate)}
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
                      defaultValue={task.additionalExecutors}
                    />
                    <div className="row mt-3">
                      <ProjectAllTasksSelect
                        id="tst"
                        preReqTasks={preReqTasks}
                        setPreReqTasks={setPreReqTasks}
                        addPreReqTask={addPreReqTask}
                        editTaskId={task.id}
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
                defaultValue={task.details}
              />
              {/*    <textarea
              class="text-dark bg-white rounded-2 w-100 h-75 "
              placeholder="Descrição da Tarefa"
              name="bio"
              type="text"
            ></textarea> */}
            </div>{" "}
            {/*  <div className="col-lg-1 d-flex align-items-center">
              <ButtonComponent
                name={"Editae tarefa"}
                onClick={handleSubmit}
              />
            </div> */}
          </div>
        </Modal.Body>
        <Modal.Footer id="modalFooter">
          <Col xs={4} className="closeBtnSeeTask">
            <Button variant="secondary" onClick={handleClose}>
              Fechar
              {/* <FormattedMessage
                id="closeModal.button"
                defaultMessage="Fechar"
              /> */}
            </Button>
          </Col>
          <Col xs={4}>
            <Button
              onClick={handleSubmit}
              className="button"
              type="submit"
              variant="outline-primary"
            >
              {" "}
              Editar
              {/*  <FormattedMessage
                id="confirmChangeModal.button"
                defaultMessage="Confirmar alteração"
              /> */}
            </Button>
          </Col>
        </Modal.Footer>
      </Modal>
    </>
  );
}

export default ModalEditTask;
