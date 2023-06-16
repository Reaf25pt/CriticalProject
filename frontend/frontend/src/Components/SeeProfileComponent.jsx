import ButtonComponent from "../Components/ButtonComponent";

import { userStore } from "../stores/UserStore";

function ProfileSee({ onEdit }) {
  const user = userStore((state) => state.user);
  const fullName = user.firstName + " " + user.lastName;
  return (
    <div className="container-fluid">
      <div className="row d-flex">
        <div class="col-12 col-sm-12 col-md-12 col-lg-4 mt-3 ">
          <div class="p-5 mb-4 bg-secondary h-100 rounded-5 ">
            <div class="text-center">
              {user.photo != null ? (
                <img
                  src={user.photo}
                  alt="avatar"
                  class="rounded-circle img-responsive"
                  width={"200px"}
                  height={"200px"}
                />
              ) : (
                <img
                  src="https://t3.ftcdn.net/jpg/00/36/94/26/360_F_36942622_9SUXpSuE5JlfxLFKB1jHu5Z07eVIWQ2W.jpg"
                  alt="avatar"
                  class="rounded-circle img-responsive"
                  width={"200px"}
                  height={"200px"}
                />
              )}
              <h5 class="my-3 text-white">{user.email}</h5>
              <p class="text-white mb-1">
                {fullName} {user.nickname && `(${user.nickname})`}
              </p>
              <p class="text-white mb-4">{user.office}</p>
              {user.openProfile ? (
                <p class="text-white mb-4">Público</p>
              ) : (
                <p class="text-white mb-4">Privado</p>
              )}
            </div>
            <div class="d-flex justify-content-around pb-5">
              <ButtonComponent
                type="button"
                name="Editar Perfil"
                onClick={onEdit}
              />
            </div>
          </div>
        </div>
        <div class="col-12 col-sm-12 col-md-12 col-lg-8 mt-3 ">
          <div class="p-5  bg-secondary h-100 rounded-5">
            <div className="bg-white h-100 ">
              {user.bio ? (
                <textarea class="text-dark bg-white h-100 w-100 rounded-2">
                  {user.bio}
                </textarea>
              ) : (
                <textarea
                  class="text-dark bg-white h-100 w-100 rounded-2"
                  placeholder="Edite o seu perfil para adicionar a sua biografia"
                  disabled
                ></textarea>
              )}

              {/*  <div className="row">
            <ButtonComponent name={"Editar"} />
          </div> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
export default ProfileSee;
