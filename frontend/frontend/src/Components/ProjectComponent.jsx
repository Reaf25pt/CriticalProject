import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";
import ButtonComponent from "./ButtonComponent";

function ProjectComponent({ toggleComponent }) {
  const user = userStore((state) => state.user);
  const [showProjects, setShowProjects] = useState([]);
  const [projects, setProjects] = useState([]);

  const { id } = useParams();

  const local = {
    0: "Lisboa",
    1: "Coimbra",
    2: "Porto",
    3: "Tomar",
    4: "Viseu",
    5: "Vila Real",
  };

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
        console.log(data);
        setShowProjects(data);
        console.log(showProjects);
      })
      .catch((err) => console.log(err));
  }, [projects]);

  return (
    <div className="container-fluid">
      <div className="row mt-5 justify-content-around">
        <div className="col-lg-3">
          <div className="row bg-secondary rounded-5 p-4 mb-3">
            <div className="row p-3 mx-auto">
              <div className="col-lg-12 bg-white rounded-3 p-2">
                {showProjects.title}
              </div>
            </div>
            <div className="row d-flex justify-content-around ">
              <div className="col-lg-5 bg-white rounded-3 p-2">
                {local[showProjects.office]}
              </div>
              <div className="col-lg-5 bg-white rounded-3 p-2">
                {showProjects.status}
              </div>
            </div>
            <div className="row mt-3">
              Falta um GET para os membros do projeto
              <select className="col-lg-6 mx-auto">
                Select com membros do projeto
              </select>
            </div>
            <div className="row mx-auto justify-content-around mt-5">
              <div className="col-lg-6">
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
              <h5>{showProjects.details}</h5>
            </div>
          </div>
        </div>
        <div className="col-lg-4 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Recursos</h4>
              <hr />
              <h5>{showProjects.resources}</h5>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProjectComponent;
