import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";

import ButtonComponent from "./ButtonComponent";
import InputComponent from "../Components/InputComponent";

function InviteMember({ projId }) {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);
  /*    const [hobbies, setHobbies] = useState([]);
     const [showHobbies, setShowHobbies] = useState([]); */
  const [search, setSearch] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const emptyStr = " ";

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    if (name === "nameInput") {
      // setSearch(event.target.value);
      handleSearch(event.target.value);
    }

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSearch = (searchStr) => {
    console.log(searchStr);
    //setValue(searchStr);

    fetch(
      `http://localhost:8080/projetofinal/rest/project/possiblemembers?name=${searchStr}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
          projId: projId,
        },
      }
    )
      .then((response) => {
        if (response.status === 200) {
          return response.json();
          //navigate("/home", { replace: true });
        }
      })
      .then((response) => {
        setSuggestions(response);
        console.log(suggestions);
      });
  };

  const handleClick = (event) => {
    event.preventDefault();

    if (
      credentials.nameInput === undefined ||
      credentials.nameInput === "undefined"
    ) {
      alert("Insira nome ");
    } else {
      /*  const userToInvite = {
        id: credentials.id,
      }; */
      fetch("http://localhost:8080/projetofinal/rest/project/newmember", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
          userId: credentials.id,
          projId: projId,
        },
      }).then((response) => {
        if (response.status === 200) {
          alert("Convite efectuado");
          //navigate("/home", { replace: true });
        } else {
          alert("Algo correu mal. Tente novamente");
        }
      });

      document.getElementById("nameInput").value = "";
      setCredentials({});
    }
  };

  const handleSelection = (user) => {
    const name = user.firstName + emptyStr + user.lastName;
    credentials.nameInput = name;
    credentials.id = user.id;

    document.getElementById("nameInput").value = name;

    setSuggestions([]);
  };

  return (
    <div className="mx-auto col-10 col-md-8 col-lg-6">
      <div className="mt-5 p-5 bg-secondary rounded-5 ">
        <div className="row mb-3 ">
          <div className="col ">
            <div className="form-outline">
              <InputComponent
                type="search"
                class="form-control rounded "
                placeholder="Pesquisar por nome *"
                required={true}
                aria-label="Search"
                aria-describedby="search-addon"
                id="nameInput"
                name="nameInput"
                defaultValue={""}
                onChange={handleChange}
              />
              <div className="dropdown bg-white">
                {suggestions &&
                  suggestions
                    /*  .filter((item) => {
                      return (
                        item && item.firstName.toLowerCase().includes(search)
                      );
                    })
                    .slice(0, 10) */
                    .map((item) => (
                      <option
                        key={item.id}
                        onClick={() => handleSelection(item)}
                      >
                        <div className="">
                          {item.firstName}
                          {emptyStr}
                          {item.lastName}
                        </div>
                      </option>
                    ))}
              </div>
            </div>
          </div>
        </div>
        <div className="col-lg-6 mx-auto">
          <ButtonComponent onClick={handleClick} name={"Convidar"} />
        </div>
      </div>
    </div>
  );
}

export default InviteMember;
