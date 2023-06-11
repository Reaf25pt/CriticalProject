import styles from "./footer.module.css";
import { BsXLg, BsSearch } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";

function Hobby() {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);
  const [hobbies, setHobbies] = useState([]);
  const [showHobbies, setShowHobbies] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/projetofinal/rest/user/ownhobbies", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((response) => response.json())
      .then((response) => {
        setShowHobbies(response);
      });
  }, [hobbies]);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleClick = (event) => {
    event.preventDefault();

    const title = credentials.hobbyInput;

    fetch("http://localhost:8080/projetofinal/rest/user/newhobby", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
      body: title,
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
      })
      .then((response) => {
        setHobbies((values) => [...values, response]);
      });
    document.getElementById("hobbyInput").value = "";
  };

  return (
    <div class=" bg-secondary rounded-3 p-4 h-100 ">
      <h3 className="bg-white text-center  rounded-5 p-0  ">
        Os meus Interesses:
      </h3>{" "}
      <div className="row">
        <div class="input-group rounded mb-3">
          <div class="input-group rounded mb-3 mt-2">
            <input
              type="search"
              class="form-control rounded "
              placeholder="Adicionar interesse"
              required
              aria-label="Search"
              aria-describedby="search-addon"
              id="hobbyInput"
              name="hobbyInput"
              defaultValue={""}
              onChange={handleChange}
              onKeyDown={(event) => {
                if (event.key === "Enter") {
                  handleClick(event);
                }
              }}
            />
            <span class="input-group-text border-0">
              <BsSearch />
            </span>
          </div>
        </div>
        <div className="row d-flex">
          {showHobbies && showHobbies.length !== 0 ? (
            <div className="row d-flex justify-content-around  align-items-center ">
              {showHobbies.map((hobby) => (
                <div
                  key={hobby.id}
                  className="d-flex justify-content-between  align-items-center  w-25  bg-white m-1 rounded-3 p-2"
                >
                  <div className="col-lg-10">{hobby.title} </div>
                  <div className="col-lg-2">
                    <BsXLg />
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div>Adicione os seus interesses</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Hobby;
