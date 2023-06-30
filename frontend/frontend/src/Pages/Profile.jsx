import ChangePasswordIn from "./ChangePasswordIn";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import SeeProfileComponenent from "../Components/SeeProfileComponent";
import EditProfileComponent from "../Components/EditProfileComponent";
import Hobby from "../Components/Hobby";
import Skill from "../Components/Skill";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Link } from "react-router-dom";
import { BsEyeFill } from "react-icons/bs";
import { Rating } from "primereact/rating";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

function Profile() {
  const user = userStore((state) => state.user);
  const fullName = user.firstName + " " + user.lastName;
  const [isEditing, setIsEditing] = useState(false);
  const [credentials, setCredentials] = useState(user);
  const userUpdate = userStore((state) => state.setUser);
  const [projects, setProjects] = useState([]);
  const [showProjects, setShowProjects] = useState([]);

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
  };

  const handleEdit = (event) => {
    setIsEditing(true);
  };

  const handleClick = (event) => {
    setIsEditing(false);
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

    var editedUser;

    if (user.contestManager || credentials.openProfile === "2") {
      editedUser = {
        firstName: credentials.firstName,
        lastName: credentials.lastName,
        officeInfo: credentials.officeInfo,

        nickname: credentials.nickname,
        bio: credentials.bio,
        photo: credentials.photo,
        openProfile: "false",
      };
    } else if (credentials.openProfile === "1") {
      editedUser = {
        firstName: credentials.firstName,
        lastName: credentials.lastName,
        officeInfo: credentials.officeInfo,

        nickname: credentials.nickname,
        bio: credentials.bio,
        photo: credentials.photo,
        openProfile: "true",
      };
    } else {
      editedUser = credentials;
    }
    /*  if (
      !user.contestManager &&
      (credentials.office === null ||
        credentials.office === "undefined" ||
        credentials.office === 20 ||
        credentials.office === "20" ||
        credentials.office === undefined ||
        credentials.openProfile === "0")
    ) {
      alert("Seleccione o local de trabalho e/ou visibilidade do perfil");
    } else if (
      user.contestManager &&
      (credentials.office === null ||
        credentials.office === "undefined" ||
        credentials.office === 20 ||
        credentials.office === "20" ||
        credentials.office === undefined)
    ) {
      alert("Seleccione o local de trabalho");
    } else { */

    /*  if (credentials.openProfile === "1") {
      // profile to be public
      editedUser = {
        firstName: credentials.firstName,
        lastName: credentials.lastName,
        officeInfo: credentials.office,
        nickname: credentials.nickname,
        bio: credentials.bio,
        photo: credentials.photo,
        openProfile: "true",
      };
    } else if (credentials.openProfile === "2") {
      editedUser = {
        firstName: credentials.firstName,
        lastName: credentials.lastName,
        officeInfo: credentials.office,
        nickname: credentials.nickname,
        bio: credentials.bio,
        photo: credentials.photo,
        openProfile: "false",
      };
    } */

    fetch("http://localhost:8080/projetofinal/rest/user/ownprofile", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
      body: JSON.stringify(editedUser),
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
      })
      .then((loggedUser) => {
        userUpdate(loggedUser);
        // navigate("/home", { replace: true });
        setIsEditing(false);
      });
  };
  useEffect(() => {
    fetch("http://localhost:8080/projetofinal/rest/user/ownprojects", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setShowProjects(data);
      })
      .catch((err) => console.log(err));
  }, [projects]);

  const renderLink = (rowData) => {
    return (
      <Link to={`/home/projects/${rowData.id}`}>
        <OverlayTrigger
          placement="top"
          overlay={<Tooltip>Ver detalhes</Tooltip>}
        >
          <span data-bs-toggle="tooltip" data-bs-placement="top">
            {" "}
            <BsEyeFill />
          </span>
        </OverlayTrigger>
      </Link>
    );
  };

  return (
    <div>
      <ul className="nav nav-tabs" id="myTab" role="tablist">
        <li className="nav-item" role="presentation">
          <button
            className="nav-link active"
            id="home-tab"
            data-bs-toggle="tab"
            data-bs-target="#home"
            type="button"
            role="tab"
            aria-controls="home"
            aria-selected="true"
            style={{ background: "#C01722", color: "white" }}
          >
            Perfil
          </button>
        </li>
        <li className="nav-item" role="presentation">
          <button
            className="nav-link"
            id="profile-tab"
            data-bs-toggle="tab"
            data-bs-target="#profile"
            type="button"
            role="tab"
            aria-controls="profile"
            aria-selected="false"
            style={{ background: "#C01722", color: "white" }}
          >
            Alterar Password
          </button>
        </li>
      </ul>
      <div className="tab-content" id="myTabContent">
        <div
          className="tab-pane fade show active"
          id="home"
          role="tabpanel"
          aria-labelledby="home-tab"
        >
          <div class="container-fluid">
            <div class="row mb-3">
              {!isEditing && <SeeProfileComponenent onEdit={handleEdit} />}
              {isEditing && (
                <EditProfileComponent
                  onChange={handleChange}
                  onSubmit={handleSubmit}
                  onClick={handleClick}
                />
              )}
            </div>
            <div class="row mt-5 mb-5 d-flex justify-content-start">
              <div class="col-12 col-sm-12 col-md-12 col-lg-6 mb-5 ">
                <Skill />
              </div>
              <div class="col-12 col-sm-12 col-md-12 col-lg-6 ">
                <Hobby />
              </div>
            </div>
            <div class="row mt-5">
              <div class="col-auto col-sm-auto col-md-12 col-lg-8 p-4 bg-secondary rounded-3 mx-auto">
                <div className="">
                  <h3 className="bg-white text-center text-nowrap rounded-5 p-0  ">
                    Os meus Projetos:
                  </h3>
                  <DataTable value={showProjects} selectionMode="single ">
                    <Column field="title" header="Nome do Projeto" sortable />
                    <Column field="status" header="Estado" sortable />
                    <Column
                      field="creationDate"
                      header="Data de Registo"
                      sortable
                      body={(rowData) =>
                        convertTimestampToDate(rowData.creationDate)
                      }
                    />

                    <Column body={renderLink} header="#" />
                  </DataTable>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="tab-content" id="myTabContent">
          <div
            className="tab-pane fade"
            id="profile"
            role="tabpanel"
            aria-labelledby="profile-tab"
          >
            <ChangePasswordIn />
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
