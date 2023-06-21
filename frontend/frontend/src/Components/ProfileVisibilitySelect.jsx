import { useState, useEffect } from "react";
import { userStore } from "../stores/UserStore";
import { BsArrowDown, BsSearch } from "react-icons/bs";

function ProfileVisibilitySelect(props) {
  return (
    <div className="arrow-select-container">
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
        placeholder={props.placeholder}
        className="form-control"
        disabled={props.disabled}
      >
        <option value="0">Visibilidade do perfil </option>
        <option value="1">Público</option>
        <option value="2">Privado</option>
      </select>
    </div>
  );
}

export default ProfileVisibilitySelect;
