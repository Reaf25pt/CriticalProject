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

function Profile() {
  const user = userStore((state) => state.user);
  const fullName = user.firstName + " " + user.lastName;
  const [isEditing, setIsEditing] = useState(false);
  const [credentials, setCredentials] = useState({});
  const userUpdate = userStore((state) => state.setUser);
  const [projects, setProjects] = useState([]);
  const [showProjects, setShowProjects] = useState([]);
  // const [selectedItems, setSelectedItems] = useState([]);
  const [selectedRow, setSelectedRow] = useState(null); // State to keep track of the selected row

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
    console.log(credentials.openProfile);
    if (
      credentials.office === null ||
      credentials.office === "undefined" ||
      credentials.office === 20 ||
      credentials.office === "20" ||
      credentials.office === undefined ||
      credentials.openProfile === "0"
    ) {
      alert("Seleccione o local de trabalho e/ou visibilidade do perfil");
    } else {
      var editedUser;
      if (credentials.openProfile === "1") {
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
      }

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
    }
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
        console.log(data);
        setShowProjects(data);
      })
      .catch((err) => console.log(err));
  }, [projects]);

  const renderLink = (rowData) => {
    return (
      <Link to={`/projects/${rowData.id}`}>
        <BsEyeFill />
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
            <div class="row mt-5">
              <div class="col-lg-4 ">
                <div class="p-4 bg-secondary rounded-3">
                  <div>
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
                      />

                      <Column body={renderLink} header="#" />
                    </DataTable>
                  </div>
                </div>
              </div>
              <div class="col-lg-4 ">
                <Skill />
              </div>
              <div class="col-lg-4 ">
                <Hobby />
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
