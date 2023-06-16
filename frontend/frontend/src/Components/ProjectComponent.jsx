import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";
import { useParams } from "react-router-dom";

function ProjectComponent() {
  const user = userStore((state) => state.user);
  const [projects, setProjects] = useState([]);
  const [showProjects, setShowProjects] = useState([]);

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
      })
      .catch((err) => console.log(err));
  }, [projects]);
  return (
    <div className="container-fluid">
      <div className="row mt-5">
        <div className="col-lg-4">
          <div className="row bg-secondary rounded-5 p-4">
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
                {showProjects.status}Status
              </div>
            </div>
            <div className="row mt-3">
              <select className="col-lg-6 mx-auto">
                Select com membros do projeto
              </select>
            </div>
          </div>
          <div className="row"></div>
          <div className="row"></div>
        </div>
        <div className="col-8"></div>
      </div>
    </div>
  );
}

export default ProjectComponent;
