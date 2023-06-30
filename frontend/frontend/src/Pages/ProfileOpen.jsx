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
import { useParams } from "react-router-dom";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

function ProfileOpen() {
  const user = userStore((state) => state.user);

  const [userProfile, setUserProfile] = useState([]);

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
  };
  const { id } = useParams();

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/user/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
      })
      .then((user) => {
        setUserProfile(user);
        // navigate("/home", { replace: true });
      });
  }, []);

  const fullName = userProfile.firstName + " " + userProfile.lastName;
  const renderLink = (rowData) => {
    /*   console.log(rowData.id);
    console.log(typeof rowData.id); */
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
              <div className="container-fluid">
                <div className="row d-flex">
                  <div class="col-12 col-sm-12 col-md-12 col-lg-4 mt-3 ">
                    <div class="p-5 mb-4 bg-secondary h-100 rounded-5 ">
                      <div class="text-center">
                        {userProfile.photo === null ? (
                          <img
                            src="https://t3.ftcdn.net/jpg/00/36/94/26/360_F_36942622_9SUXpSuE5JlfxLFKB1jHu5Z07eVIWQ2W.jpg"
                            alt="avatar"
                            class="rounded-circle img-responsive"
                            width={"200px"}
                            height={"200px"}
                          />
                        ) : userProfile.photo === "" ? (
                          <img
                            src="https://t3.ftcdn.net/jpg/00/36/94/26/360_F_36942622_9SUXpSuE5JlfxLFKB1jHu5Z07eVIWQ2W.jpg"
                            alt="avatar"
                            class="rounded-circle img-responsive"
                            width={"200px"}
                            height={"200px"}
                          />
                        ) : (
                          <img
                            src={userProfile.photo}
                            class="rounded-circle img-responsive"
                            width={"200px"}
                            height={"200px"}
                            alt=""
                          />
                        )}
                        <h5 class="my-3 text-white">{userProfile.email}</h5>
                        <p class="text-white mb-1">
                          {fullName}{" "}
                          {userProfile.nickname && `(${userProfile.nickname})`}
                        </p>
                        <p class="text-white mb-4">{userProfile.office}</p>
                        {userProfile.openProfile ? (
                          <p class="text-white mb-4">Público</p>
                        ) : (
                          <p class="text-white mb-4">Privado</p>
                        )}
                      </div>
                    </div>
                  </div>
                  <div class="col-12 col-sm-12 col-md-12 col-lg-8 mt-3 ">
                    <div class="p-5  bg-secondary h-100 rounded-5">
                      <div className="bg-white h-100 ">
                        {userProfile.bio ? (
                          <textarea class="text-dark bg-white h-100 w-100 rounded-2">
                            {userProfile.bio}
                          </textarea>
                        ) : (
                          <textarea
                            class="text-dark bg-white h-100 w-100 rounded-2"
                            disabled
                          >
                            Biografia não disponibilizada
                          </textarea>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="row mt-5 mb-5 d-flex justify-content-start">
              <div class="col-12 col-sm-12 col-md-12 col-lg-6 mb-5 ">
                <div className="row">
                  <h3 className="bg-white text-center rounded-5 p-0">
                    Skills:
                  </h3>{" "}
                </div>
                <div className="row">
                  {userProfile.skills && userProfile.skills.length !== 0 ? (
                    <div className="row">
                      {userProfile.skills.map((skill) =>
                        skill.skillType === 0 ? (
                          <div
                            key={skill.id}
                            className="col-7 col-lg-7 mx-auto   bg-danger m-0 rounded-2 d-flex align-items-center text-white border border-white mb-1"
                          >
                            <div className="col-lg-10 ">
                              <h4>{skill.title} </h4>{" "}
                            </div>
                          </div>
                        ) : skill.skillType === 1 ? (
                          <div
                            key={skill.id}
                            className="col-7 col-lg-7  mx-auto  bg-success m-0 rounded-2 d-flex align-items-center text-white border border-white mb-1"
                          >
                            <div className="col-lg-10  ">
                              {" "}
                              <h4>{skill.title} </h4>{" "}
                            </div>
                          </div>
                        ) : skill.skillType === 2 ? (
                          <div
                            key={skill.id}
                            className=" col-7  col-lg-7 bg-primary d-flex mx-auto  m-0 rounded-2 d-flex align-items-center text-white border border-white mb-1"
                          >
                            <div className="col-lg-10 overflow-auto">
                              {" "}
                              <h4>{skill.title} </h4>{" "}
                            </div>
                          </div>
                        ) : (
                          <div
                            key={skill.id}
                            className="col-7 col-lg-7 mx-auto  bg-warning d-flex mx-auto   m-0 rounded-2 d-flex align-items-center text-white border border-white mb-1 "
                          >
                            <div className="col-lg-10">
                              {" "}
                              <h4>{skill.title} </h4>{" "}
                            </div>
                          </div>
                        )
                      )}
                    </div>
                  ) : (
                    <p class="bg-white">
                      {fullName} não tem skills associadas ao seu perfil
                    </p>
                  )}
                </div>
                <div class="col-12 col-sm-12 col-md-12 col-lg-6 ">
                  <div className="row">
                    <h3 className="bg-white text-center rounded-5 p-0">
                      Interesses:
                    </h3>{" "}
                  </div>
                  <div className="row d-flex  ">
                    {userProfile.hobbies && userProfile.hobbies !== 0 ? (
                      <div className="row d-flex  ">
                        {userProfile.hobbies.map((hobby) => (
                          <div
                            key={hobby.id}
                            className="col-6 bg-dark mx-auto m-1 p-2 rounded-2 d-flex align-items-center text-white border border-white mb-1"
                          >
                            <div className="col-lg-10">{hobby.title} </div>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p class="bg-white">
                        {fullName} não tem interesses associados ao seu perfil
                      </p>
                    )}
                  </div>
                </div>
              </div>
              <div class="row mt-5">
                <div class="col-auto col-sm-auto col-md-12 col-lg-8 p-4 bg-secondary rounded-3 mx-auto">
                  <div className="">
                    <h3 className="bg-white text-center text-nowrap rounded-5 p-0  ">
                      Projectos:
                    </h3>
                    <DataTable
                      value={userProfile.projects}
                      selectionMode="single "
                    >
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
        </div>
      </div>
    </div>
  );
}

export default ProfileOpen;
