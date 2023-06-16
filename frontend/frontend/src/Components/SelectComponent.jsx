import { useState, useEffect } from "react";
import { userStore } from "../stores/UserStore";

function SelectComponent(props) {
  const user = userStore((state) => state.user);
  const [officeList, setOfficeList] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/projetofinal/rest/user/offices", {
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
        setOfficeList(list);
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
      >
        <option value="20">{props.local} </option>
        {Object.entries(officeList).map(([key, value]) => (
          <option key={key} value={key}>
            {value}
          </option>
        ))}
      </select>
    </div>
  );
}

export default SelectComponent;
