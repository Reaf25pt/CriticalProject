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
    <div className="arrow-select-container">
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
        placeholder={props.placeholder}
        className="form-control"
        defaultValue={props.defaultValue}
        style={{ backgroundColor: "white" }}
      >
        <option value="20">{props.placeholder}</option>
        {Object.entries(skillType).map(([key, value]) => (
          <option
            key={key}
            value={key}
            style={{
              backgroundColor:
                key === "0"
                  ? "#dc3545"
                  : key === "1"
                  ? "#28a745"
                  : key === "2"
                  ? "#007bff"
                  : "#ffc107",
              color: "white",
            }}
          >
            {value}
          </option>
        ))}
      </select>
    </div>
  );
}

export default SelectSkillType;
