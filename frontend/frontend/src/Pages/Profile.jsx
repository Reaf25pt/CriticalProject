import { BsArrowDown, BsSearch } from "react-icons/bs";
import ButtonComponent from "../Components/ButtonComponent";
import LinkButton from "../Components/LinkButton";
import TextAreaComponent from "../Components/TextAreaComponent";
import SelectComponent from "../Components/SelectComponent";
import ChangePasswordIn from "./ChangePasswordIn";

function Profile() {
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
              <div class="col-12 col-sm-12 col-md-12 col-lg-4 mt-3 ">
                <div class="p-5 mb-4 bg-secondary h-100 rounded-5 ">
                  <div class="text-center">
                    <img
                      src="https://randomuser.me/api/portraits/women/22.jpg"
                      alt="avatar"
                      class="rounded-circle img-responsive"
                    />
                    <h5 class="my-3 text-white">
                      Rodrigo.a.ferreira@gmail.com
                    </h5>
                    <p class="text-white mb-1">Rodrigo Ferreira(Alcunha)</p>
                    <p class="text-white mb-4">Coimbra</p>
                    <p class="text-white mb-4">Privado</p>
                    <div class="d-flex justify-content-around">
                      <ButtonComponent type="button" name="Editar" />

                      <LinkButton name="Alterar password" />
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-12 col-sm-12 col-md-12 col-lg-8 mt-3 ">
                <div class="p-5 mb-4 bg-secondary h-100 rounded-5">
                  <textarea
                    class="text-dark bg-white h-75 w-100 rounded-2 mb-5"
                    placeholder="Biografia..."
                  ></textarea>
                  <div className="row">
                    <ButtonComponent name={"Editar"} />
                  </div>
                </div>
              </div>
            </div>
            <div class="row mt-5">
              <div class="col-lg-4 ">
                <div class="p-4 bg-secondary rounded-3">
                  <div>
                    <h3 className="bg-white text-center text-nowrap rounded-5  ">
                      Projetos
                    </h3>
                    <div className="text-white p-1 m-1 rounded-3 w-75 p-3 ">
                      <div className="mb-2 bg-black rounded-3">
                        <p>Projeto 1</p>
                      </div>
                      <div className="mb-2 bg-black rounded-3">
                        <p>Projeto 2</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-lg-4 ">
                <div class="bg-secondary rounded-3 p-4 h-100">
                  <div class="input-group rounded mb-3">
                    <input
                      type="search"
                      class="form-control rounded "
                      placeholder="Skills..."
                      aria-label="Search"
                      aria-describedby="search-addon"
                    />
                    <div class="form-group">
                      <div class="input-group rounded">
                        <SelectComponent placeholder={"Tipos"} />
                        <span
                          class="input-group-text border-0"
                          id="search-addon"
                        >
                          <BsArrowDown />
                        </span>
                      </div>
                    </div>
                    <span class="input-group-text border-0">
                      <BsSearch />
                    </span>
                  </div>
                  <div className="row mx-auto">
                    <div className="w-25  bg-white m-1 rounded-3">
                      <p>skills x</p>
                    </div>
                    <div className="w-25 m-1 bg-danger rounded-3">
                      <p>skills x</p>
                    </div>
                    <div className="w-25 m-1 bg-danger rounded-3">
                      <p>skills x</p>
                    </div>
                    <div className="w-25  bg-white m-1 rounded-3">
                      <p>skills x</p>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-lg-4 ">
                <div class=" bg-secondary rounded-3 p-4 h-100">
                  <div class="input-group rounded mb-3">
                    <input
                      type="search"
                      class="form-control rounded "
                      placeholder="Hobbies"
                      aria-label="Search"
                      aria-describedby="search-addon"
                    />
                    <span class="input-group-text border-0">
                      <BsSearch />
                    </span>
                  </div>
                  <div className="d-flex">
                    <div className="w-25  bg-white m-1 rounded-3">
                      <p>Hobbies x</p>
                    </div>
                    <div className="w-25 m-1 bg-danger rounded-3">
                      <p>Hobbies x</p>
                    </div>
                  </div>
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
