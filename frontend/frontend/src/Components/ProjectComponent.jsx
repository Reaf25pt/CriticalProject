import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import ButtonComponent from "./ButtonComponent";

function ProjectComponent({ toggleComponent, project, local, members }) {
  return (
    <div className="container-fluid">
      <div className="row mt-5 justify-content-around">
        <div className="col-lg-3">
          <div className="row bg-secondary rounded-5 p-4 mb-3">
            <div className="row p-3 mx-auto">
              <div className="col-lg-12 bg-white rounded-3 p-2">
                {project.title}
              </div>
            </div>
            <div className="row d-flex justify-content-around ">
              <div className="col-lg-5 bg-white rounded-3 p-2">{"item"}</div>
              <div className="col-lg-5 bg-white rounded-3 p-2">
                {project.status}
              </div>
            </div>
            <div className="row mt-3">
              <select className="col-lg-6 mx-auto">
                <option>Membros</option>
                {members.map((member) => (
                  <option>
                    {member.userInvitedFirstName} {member.userInvitedLastName}
                  </option>
                ))}{" "}
              </select>
            </div>
            <div className="row mx-auto justify-content-around mt-5">
              <div className="col-lg-12">
                <ButtonComponent
                  type="button"
                  name="Editar Projeto"
                  onClick={toggleComponent}
                />
              </div>
            </div>
          </div>
        </div>
        <div className="col-lg-4 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Descrição</h4>
              <hr />
              <h5>{project.details}</h5>
            </div>
          </div>
        </div>
        <div className="col-lg-4 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Recursos</h4>
              <hr />
              <h5>{project.resources}</h5>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProjectComponent;
