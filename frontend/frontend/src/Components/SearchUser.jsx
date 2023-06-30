import { useState } from "react";
import { userStore } from "../stores/UserStore";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

import InputComponent from "../Components/InputComponent";
import { BsEyeFill, BsEyeSlashFill, BsMessenger } from "react-icons/bs";
import { Link } from "react-router-dom";

function SearchUser() {
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
      `http://localhost:8080/projetofinal/rest/user/suggestion?name=${searchStr}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
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

  /*
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
  /*    fetch("http://localhost:8080/projetofinal/rest/project/newmember", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
          userId: credentials.id,
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
  };*/

  /* const handleSelection = (user) => {
    const name = user.firstName + emptyStr + user.lastName;
    credentials.nameInput = name;
    credentials.id = user.id;

    document.getElementById("nameInput").value = name;

    setSuggestions([]);
  };*/

  function handleSeeProfile(userId) {
    // event.preventDefault();

    console.log(userId);
  }

  function handleSendMessage(userId) {
    // event.preventDefault();

    console.log(userId);
  }

  return (
    <div className="form-outline">
      <InputComponent
        type="search"
        class="form-control rounded "
        placeholder="Procurar utilizador *"
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
              <option key={item.id} /* onClick={() => handleSelection(item)} */>
                <div className="row d-flex justify-content-around p-2 ">
                  <div className="col-lg-2 ">
                    {item.photo === null ? (
                      <img
                        src="https://static-00.iconduck.com/assets.00/user-avatar-icon-512x512-vufpcmdn.png"
                        width={30}
                        height={30}
                        alt=""
                      />
                    ) : (
                      <img
                        src={item.photo}
                        width={35}
                        height={35}
                        className="rounded-5"
                        alt=""
                      />
                    )}
                  </div>

                  <div className="col-lg-5">
                    {item.firstName}
                    {emptyStr}
                    {item.lastName}
                  </div>
                  <div className="col-lg-1">
                    {!item.openProfile ? (
                      <OverlayTrigger
                        placement="top"
                        overlay={<Tooltip>Perfil privado</Tooltip>}
                      >
                        <span data-bs-toggle="tooltip" data-bs-placement="top">
                          {" "}
                          <BsEyeSlashFill size={25} />
                        </span>
                      </OverlayTrigger>
                    ) : (
                      <Link to={`/home/profile/${item.id}`}>
                        <OverlayTrigger
                          placement="top"
                          overlay={<Tooltip>Ver perfil</Tooltip>}
                        >
                          <span
                            data-bs-toggle="tooltip"
                            data-bs-placement="top"
                          >
                            {" "}
                            <BsEyeFill
                              size={25}
                              color="black"
                              //onClick={() => handleSeeProfile(item.id)}
                            />
                          </span>
                        </OverlayTrigger>
                      </Link>
                    )}
                  </div>
                  <div className="col-lg-1">
                    <Link>
                      {" "}
                      <OverlayTrigger
                        placement="top"
                        overlay={<Tooltip>Enviar mensagem</Tooltip>}
                      >
                        <span data-bs-toggle="tooltip" data-bs-placement="top">
                          {" "}
                          <BsMessenger
                            color="black"
                            onClick={() => handleSendMessage(item.id)}
                          />
                        </span>
                      </OverlayTrigger>
                    </Link>
                  </div>
                </div>
              </option>
            ))}
      </div>
    </div>
  );
}

export default SearchUser;
