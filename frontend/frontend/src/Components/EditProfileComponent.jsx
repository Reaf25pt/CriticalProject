import { BsArrowDown, BsSearch } from "react-icons/bs";
import ButtonComponent from "../Components/ButtonComponent";
import LinkButton from "../Components/LinkButton";
import TextAreaComponent from "../Components/TextAreaComponent";
import SelectComponent from "../Components/SelectComponent";
import InputComponent from "../Components/InputComponent";

import { userStore } from "../stores/UserStore";
import { useState } from "react";

function ProfileEdit({ onChange, onSubmit }) {
  const user = userStore((state) => state.user);
  const fullName = user.firstName + " " + user.lastName;

  return (
    <>
      <div className="container-fluid">
        <div className="row d-flex">
          <form onSubmit={onSubmit}>
            <div class="col-6 col-sm-6 col-md-6 col-lg-6 mt-3 ">
              <div class="p-5 mb-4 bg-secondary h-100 rounded-5 ">
                <div class="text-center">
                  {/*  {user.photo != null ? (
                <img
                  src={user.photo}
                  alt="avatar"
                  class="rounded-circle img-responsive"
                />
              ) : (
                <img
                  src="https://randomuser.me/api/portraits/women/22.jpg"
                  alt="avatar"
                  class="rounded-circle img-responsive"
                />
              )} */}
                  {/*  <img
      src="https://randomuser.me/api/portraits/women/22.jpg"
      alt="avatar"
      class="rounded-circle img-responsive"
    /> */}
                  <h5 class="my-3 text-white">{user.email}</h5>
                  <div className="mb-3 form-outline">
                    <InputComponent
                      placeholder={"Primeiro Nome*"}
                      id="firstNameInput"
                      required
                      name="firstName"
                      type="text"
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
                      onChange={onChange}
                    />
                  </div>
                  <div className="mb-3 form-outline">
                    <InputComponent
                      placeholder={"Alcunha "}
                      id="nicknameInput"
                      name="nickname"
                      type="text"
                      onChange={onChange}
                    />
                  </div>
                  <div class="form-group mt-3 mb-3">
                    <div class="input-group rounded">
                      <SelectComponent
                        name="office"
                        id="officeInput"
                        required={true}
                        onChange={onChange}
                        placeholder={"Local de trabalho *"}
                      />
                      <span class="input-group-text border-0" id="search-addon">
                        <BsArrowDown />
                      </span>
                    </div>
                  </div>
                  {/* {user.openProfile ? (
                <p class="text-white mb-4">Público</p>
              ) : (
                <p class="text-white mb-4">Privado</p>
              )} */}
                  {/*                     <p class="text-white mb-4">Privado</p>
                   */}{" "}
                  <div class="d-flex justify-content-around">
                    <ButtonComponent type="submit" name="Guardar" />

                    {/*   <LinkButton name="Alterar password" /> */}
                  </div>
                </div>
              </div>
            </div>
            <div class="col-6 col-sm-6 col-md-6 col-lg-6 mt-3 ">
              <div class="p-5 mb-4 bg-secondary h-100 rounded-5">
                <textarea
                  class="text-dark bg-white h-75 w-100 rounded-2 mb-5"
                  placeholder="Biografia..."
                  id="bioInput"
                  name="bio"
                  type="text"
                  onChange={onChange}
                ></textarea>
                <div className="row">
                  <ButtonComponent name={"Editar"} />
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </>
  );
}
export default ProfileEdit;
