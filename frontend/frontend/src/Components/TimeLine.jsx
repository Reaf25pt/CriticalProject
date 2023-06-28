import { useEffect, useState } from "react";
import ButtonComponent from "./ButtonComponent";
import ProjectAllTasksSelect from "./ProjectAllTasksSelect";
import { useParams } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import TextAreaComponent from "./TextAreaComponent";
import { Timeline } from "primereact/timeline";

function TimeLine() {
  const user = userStore((state) => state.user);
  const [recordList, setRecordList] = useState([]);
  const [showTasks, setShowTasks] = useState([]);
  const [task, setTask] = useState([]);
  const { id } = useParams();
  const [credentials, setCredentials] = useState({});

  /*
  const events = [
    {
      status: "Ordered",
      date: "15/10/2020 10:30",
      icon: "pi pi-shopping-cart",
      color: "#9C27B0",
      image: "game-controller.jpg",
    },
    {
      status: "Processing",
      date: "15/10/2020 14:00",
      icon: "pi pi-cog",
      color: "#673AB7",
    },
    {
      status: "Shipped",
      date: "15/10/2020 16:15",
      icon: "pi pi-shopping-cart",
      color: "#FF9800",
    },
    {
      status: "Delivered",
      date: "16/10/2020 10:00",
      icon: "pi pi-check",
      color: "#607D8B",
    },
    {
      status: "Delivered",
      date: "16/10/2020 10:00",
      icon: "pi pi-check",
      color: "#607D8B",
    },
    {
      status: "Delivered",
      date: "16/10/2020 10:00",
      icon: "pi pi-check",
      color: "#607D8B",
    },
  ];
*/

  useEffect(() => {
    console.log("use effect timeline");
    fetch(`http://localhost:8080/projetofinal/rest/project/${id}/record`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        console.log(data);
        setRecordList(data);
        //console.log(showProjects);
      })
      .catch((err) => console.log(err));
  }, []);

  // const customizedMarker = (item) => {
  //   return (
  //     <span className="flex w-2rem h-2rem align-items-center justify-content-center text-white border-circle z-1 shadow-1">
  //       <i className={item.icon}></i>
  //     </span>
  //   );
  // };

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  const customizedContent = (item) => {
    return (
      <div className="card bg-secondary text-white">
        <div className="card-body">
          <h5 className="card-title">
            {item.authorFirstName} {item.authorLastName}
          </h5>
          <h7>{formatDate(item.creationTime)}</h7>
          <p class="card-text">{item.message}</p>
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

  return (
    <div class="container-fluid">
      <div className="row mt-5 d-flex justify-content-around">
        <div className="col-lg-4">
          <form className="bg-secondary p-5 rounded-5">
            <ProjectAllTasksSelect
              name="preRequiredTasks"
              id="preRequiredTasks"
              placeholder={"Tarefas precedentes "}
              local={"Tarefas precedentes "}
              taskList={showTasks}
            />
            <div className="row mt-3 mb-3">
              <TextAreaComponent
                placeholder={"Registar ocorrência *"}
                id="record"
                name="record"
                required
                type="text"
                onChange={handleChange}
              />
            </div>
            <div className="row">
              <ButtonComponent
                name="Adicionar ocorrência"
                onClick={addRecord}
              />
            </div>
          </form>
        </div>
        {recordList && recordList.length > 0 ? (
          <div className="col-lg-6">
            <Timeline
              value={recordList}
              align="alternate"
              className="customized-timeline"
              // marker={customizedMarker}
              content={customizedContent}
            />
          </div>
        ) : (
          <p className="bg-secondary">
            Não há histórico de registos para apresentar
          </p>
        )}
      </div>
    </div>
  );
}

export default TimeLine;
