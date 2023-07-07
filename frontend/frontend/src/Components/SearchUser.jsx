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

    if (value === "") {
      setSuggestions(null);
      return;
    }

    if (name === "nameInput") {
      // setSearch(event.target.value);
      handleSearch(event.target.value);
    }

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSearch = (searchStr) => {
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
      });
  };

  function handleSeeProfile(userId) {
    // event.preventDefault();
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
        onBlur={() => setSuggestions(null)}
      />
      <div className="dropdown bg-white w-25" style={{ position: "absolute" }}>
        {suggestions &&
          suggestions.map((item) => (
            <option key={item.id}>
              <div className="row d-flex justify-content-around p-2">
                <div className="col-lg-2 ">
                  {item.photo === null || !item.openProfile ? (
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
                        <span data-bs-toggle="tooltip" data-bs-placement="top">
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
                  <Link to={`/home/chat?userId=${item.id}`}>
                    {" "}
                    <OverlayTrigger
                      placement="top"
                      overlay={<Tooltip>Enviar mensagem</Tooltip>}
                    >
                      <span data-bs-toggle="tooltip" data-bs-placement="top">
                        {" "}
                        <BsMessenger
                          color="black"
                          // onClick={() => handleSendMessage(item.id)}
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
