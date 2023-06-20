import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import ButtonComponent from "./ButtonComponent";

function ProjectComponent({ toggleComponent, project /* , members */ }) {
  return (
    <>
      <div className="container-fluid">
        <div className="row mt-5 justify-content-around">
          <div className="col-lg-3">
            <div className="row bg-secondary rounded-5 p-4 mb-3 d-flex">
              <div className="row mb-3">
                <h2 className="text-center text-white">{project.title}</h2>
              </div>
              <div class="row mt-3 mb-3  rounded-4 p-2 ext-white ">
                <h4 className="text-center text-white">{project.officeInfo}</h4>
              </div>
              <div className="row mt-3 mb-3 rounded-4 p-2 bg-danger  text-white  ">
                <h5 className="text-center">{project.status}</h5>
              </div>
              <div class=" text-center text-white mb-1">
                <h2>
                  {project.membersNumber}/{project.membersNumber}
                </h2>
              </div>

              {project.manager ? (
                <div className="row mx-auto justify-content-around mt-5">
                  <div className="col-lg-12">
                    <ButtonComponent
                      type="button"
                      name="Editar Projeto"
                      onClick={toggleComponent}
                    />
                  </div>
                </div>
              ) : null}
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
          {project.member ? (
            <div className="col-lg-4 ">
              <div className="bg-secondary p-3 rounded-5 h-100">
                <div className="bg-white rounded-5 h-100 p-3">
                  <h4>Recursos</h4>
                  <hr />
                  <h5>{project.resources}</h5>
                </div>
              </div>
            </div>
          ) : null}
        </div>
      </div>
      <div className="row mx-auto justify-content-around mt-5">
        <div className="col-lg-4">
          <div className="row bg-secondary rounded-5 p-4 mb-4">
            <div className="col-lg-12 bg-white rounded-5">
              <h4 className="text-center">Palavras-Chave</h4>
            </div>
            <div className="row mt-3 mx-auto">
              {project.keywords.length > 0 ? (
                <div className="row bg-white  p-2 mx-auto rounded-2 mt-3 mb-3 ">
                  <div className="form-outline  ">
                    <div className="d-flex ">
                      {project.keywords &&
                        project.keywords.map((item) => (
                          <>
                            <div className="bg-secondary text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                              {item.title}{" "}
                            </div>
                          </>
                        ))}
                    </div>
                  </div>
                </div>
              ) : (
                "O projecto não tem palavras-chave associadas"
              )}
            </div>
          </div>
        </div>
        <div className="col-lg-4">
          <div className="row bg-secondary rounded-5 p-4">
            <div className="col-lg-12 bg-white rounded-5">
              <h4 className="text-center">Skills</h4>
            </div>
            <div className="row mt-3 mx-auto">
              {project.skills.length > 0 ? (
                <div className="row bg-white  p-2 mx-auto rounded-2 mt-3 mb-3 ">
                  <div className="form-outline  ">
                    <div className="d-flex ">
                      {project.skills &&
                        project.skills.map((item) =>
                          item.skillType === 0 ? (
                            <div className="bg-danger text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                              {item.title}{" "}
                            </div>
                          ) : item.skillType === 1 ? (
                            <div className="bg-success text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                              {item.title}{" "}
                            </div>
                          ) : item.skillType === 2 ? (
                            <div className="bg-primary text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                              {item.title}{" "}
                            </div>
                          ) : (
                            <div className="bg-warning text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                              {item.title}{" "}
                            </div>
                          )
                        )}
                    </div>
                  </div>
                </div>
              ) : (
                "O projecto não tem skills associadas"
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default ProjectComponent;
