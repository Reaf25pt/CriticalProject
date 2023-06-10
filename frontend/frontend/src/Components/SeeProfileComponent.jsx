import { BsArrowDown, BsSearch } from "react-icons/bs";
import ButtonComponent from "../Components/ButtonComponent";
import LinkButton from "../Components/LinkButton";
import TextAreaComponent from "../Components/TextAreaComponent";
import SelectComponent from "../Components/SelectComponent";

import { userStore } from "../stores/UserStore";
import { useState } from "react";

function ProfileSee({ onEdit }) {
  const user = userStore((state) => state.user);
  const fullName = user.firstName + " " + user.lastName;
  return (
    <>
      <div class="col-12 col-sm-12 col-md-12 col-lg-4 mt-3 ">
        <div class="p-5 mb-4 bg-secondary h-100 rounded-5 ">
          <div class="text-center">
            {user.photo != null ? (
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
            )}
            {/*  <img
      src="https://randomuser.me/api/portraits/women/22.jpg"
      alt="avatar"
      class="rounded-circle img-responsive"
    /> */}
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
            {/*                     <p class="text-white mb-4">Privado</p>
             */}{" "}
            {/*  <div class="d-flex justify-content-around">
              <ButtonComponent type="button" name="Editar" onClick={onEdit} />

              {/*   <LinkButton name="Alterar password" /> */}
            {/*   </div> */}
          </div>
        </div>
      </div>
      <div class="col-12 col-sm-12 col-md-12 col-lg-8 mt-3 ">
        <div class="p-5 mb-4 bg-secondary h-100 rounded-5">
          {user.bio ? (
            <p class="text-white mb-4">{user.bio}</p>
          ) : (
            <textarea
              class="text-dark bg-white h-75 w-100 rounded-2 mb-5"
              placeholder=" Pode escrever aqui a sua biografia"
              disabled
            ></textarea>
          )}

          {/*  <div className="row">
            <ButtonComponent name={"Editar"} />
          </div> */}
        </div>
      </div>
      <div class="d-flex justify-content-around">
        <ButtonComponent type="button" name="Editar" onClick={onEdit} />

        {/*   <LinkButton name="Alterar password" /> */}
      </div>
    </>
  );
}
export default ProfileSee;
