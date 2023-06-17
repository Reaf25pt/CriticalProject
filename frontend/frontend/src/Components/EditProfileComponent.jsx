import { BsArrowDown, BsSearch } from "react-icons/bs";
import ButtonComponent from "../Components/ButtonComponent";
import LinkButton from "../Components/LinkButton";
import TextAreaComponent from "../Components/TextAreaComponent";
import SelectComponent from "../Components/SelectComponent";
import ProfileVisibilitySelect from "./ProfileVisibilitySelect";
import InputComponent from "../Components/InputComponent";

import { userStore } from "../stores/UserStore";

function ProfileEdit({ onChange, onSubmit, onClick }) {
  const user = userStore((state) => state.user);
  const fullName = user.firstName + " " + user.lastName;

  return (
    <div className="container-fluid">
      <form className="row d-flex" onSubmit={onSubmit}>
        <div class="col-12 col-sm-12 col-md-12 col-lg-4 mt-3">
          <div class="p-5 mb-4 bg-secondary h-100 rounded-5 ">
            <div class="text-center">
              <h5 class="my-3 text-white">{user.email}</h5>
              <div className="mb-3 form-outline">
                <InputComponent
                  placeholder={"Primeiro Nome*"}
                  id="firstNameInput"
                  required
                  name="firstName"
                  type="text"
                  defaultValue={user.firstName || ""}
                  onChange={onChange}
                />
              </div>
              <div className="mb-3 form-outline">
                <InputComponent
                  placeholder={"Último Nome *"}
                  id="lastNameInput"
                  required
                  name="lastName"
                  type="text"
                  defaultValue={user.lastName || ""}
                  onChange={onChange}
                />
              </div>
              <div className="mb-3 form-outline">
                <InputComponent
                  placeholder={"Alcunha "}
                  id="nicknameInput"
                  name="nickname"
                  type="text"
                  defaultValue={user.nickname || ""}
                  onChange={onChange}
                />
              </div>
              <div class="form-group ">
                <div class="input-group rounded">
                  <SelectComponent
                    name="office"
                    id="officeInput"
                    required={true}
                    defaultValue={user.office || ""}
                    onChange={onChange}
                    placeholder={"Local de trabalho *"}
                    local={user.office}
                  />
                </div>
                <div className="row mt-3">
                  <div class="input-group rounded">
                    <ProfileVisibilitySelect
                      name="openProfile"
                      id="openProfileInput"
                      placeholder={"Visibilidade do perfil"}
                      onChange={onChange}
                    />
                  </div>
                </div>
                <div className="row mt-3">
                  <div className="form-outline">
                    <InputComponent
                      placeholder={"Link da fotografia "}
                      id="photoInput"
                      name="photo"
                      type="text"
                      defaultValue={user.photo || ""}
                      onChange={onChange}
                      title="Url deve terminar em .jpg, .gif ou .png"
                      pattern="(http(s?):)([/|.|\w|\s|-])*\.(?:jpg|gif|png)"
                    />
                  </div>
                </div>

                <div className="row mt-5">
                  <div class="d-flex justify-content-around mb-1">
                    <ButtonComponent type="submit" name="Guardar" />

                    {/*   <LinkButton name="Alterar password" /> */}
                  </div>
                  <div class="d-flex justify-content-around">
                    <ButtonComponent
                      type="click"
                      name="Cancelar"
                      onClick={onClick}
                    />

                    {/*   <LinkButton name="Alterar password" /> */}
                  </div>
                </div>
              </div>
              {/* {user.openProfile ? (
                <p class="text-white mb-4">Público</p>
              ) : (
                <p class="text-white mb-4">Privado</p>
              )} */}
              {/*                     <p class="text-white mb-4">Privado</p>
               */}{" "}
              {/* <div class="d-flex justify-content-around">
                    <ButtonComponent type="submit" name="Guardar" />

                    {/*   <LinkButton name="Alterar password" /> */}
              {/*  </div> */}
            </div>
          </div>
        </div>
        <div class="col-12 col-sm-12 col-md-12 col-lg-8 mt-3">
          <div class="p-5 bg-secondary h-100 rounded-5">
            <textarea
              class="text-dark bg-white h-100 w-100 rounded-2  "
              placeholder="Escreva aqui a sua biografia"
              id="bioInput"
              name="bio"
              type="text"
              defaultValue={user.bio || ""}
              onChange={onChange}
            ></textarea>
            {/*  <div className="row">
                  <ButtonComponent name={"Editar"} />
                </div> */}
          </div>
        </div>
      </form>
    </div>
  );
}
export default ProfileEdit;
