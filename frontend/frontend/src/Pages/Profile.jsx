import { BsSearch } from "react-icons/bs";
import ButtonComponent from "../Components/ButtonComponent";
import LinkButton from "../Components/LinkButton";
import TextAreaComponent from "../Components/TextAreaComponent";

function Profile() {
  return (
    <div>
      <section>
        <div class="container py-5">
          <div class="row">
            <div class="col"></div>
          </div>

          <div class="row">
            <div class="col-lg-4 ">
              <div class="card mb-4 bg-secondary">
                <div class="card-body text-center">
                  <img
                    src="https://randomuser.me/api/portraits/women/22.jpg"
                    alt="avatar"
                    class="rounded-circle img-responsive"
                  />
                  <h5 class="my-3 text-white">Rodrigo.a.ferreira@gmail.com</h5>
                  <p class="text-white mb-1">Rodrigo Ferreira(Alcunha)</p>
                  <p class="text-white mb-4">Coimbra</p>
                  <p class="text-white mb-4">Privado</p>
                  <div class="d-flex justify-content-around mb-2">
                    <ButtonComponent type="button" name="Editar" />

                    <LinkButton name="Alterar password" />
                  </div>
                </div>
              </div>
              <div class="card mb-4 mb-lg-0">
                <div class="card-body p-0 bg-secondary">
                  <div>
                    <h3 className="bg-white mt-5 text-center text-nowrap rounded-5 mb-3 ">
                      Projetos
                    </h3>
                    <div className="text-white p-1 m-1 rounded-3 w-75 p-3 mx-auto">
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
            </div>
            <div class="col-lg-8">
              <div class=" mb-4">
                <div class="bg-secondary p-3">
                  <div class="row">
                    <TextAreaComponent placeholder={"Biografia...."} />
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-4 mb-md-0">
                    <div class="bg-secondary">
                      <div class="input-group rounded mb-3">
                        <input
                          type="search"
                          class="form-control rounded "
                          placeholder="Skills..."
                          aria-label="Search"
                          aria-describedby="search-addon"
                        />
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
                </div>
                <div class="col-md-6 ">
                  <div class=" mb-4">
                    <div class=" bg-secondary rounded-3 p-2">
                      <div class="input-group rounded mb-3 mt-3">
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
          </div>
        </div>
      </section>
    </div>
  );
}

export default Profile;
