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
    <div>
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
        placeholder={props.placeholder}
        className="form-control"
      >
        <option value="20">{"Local de trabalho *"}</option>
        {Object.entries(officeList).map(([key, value]) => (
          <option key={key} value={key}>
            {value}
          </option>
        ))}

        {/*   <option value="0">Lisboa</option>
        <option value="1">Coimbra</option>
        <option value="2">Porto</option>
        <option value="3">Tomar</option>
        <option value="4">Viseu</option>
        <option value="5">Vila Real</option> */}
      </select>
    </div>
  );
}

export default SelectComponent;
