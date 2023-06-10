import { useState, useEffect } from "react";
import { userStore } from "../stores/UserStore";

function SelectSkillType(props) {
  const user = userStore((state) => state.user);
  const [skillType, setSkillTypet] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/projetofinal/rest/user/skilltypes", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
        }
      })
      .then((list) => {
        setSkillTypet(list);
      });
  }, []);

  return (
    <div>
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
        placeholder={props.placeholder}
        className="form-control"
        defaultValue={props.defaultValue}
      >
        <option value="20">{"Categoria *"}</option>
        {Object.entries(skillType).map(([key, value]) => (
          <option key={key} value={key}>
            {value}
          </option>
        ))}
      </select>
    </div>
  );
}

export default SelectSkillType;
