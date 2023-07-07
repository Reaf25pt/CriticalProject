import styles from "./footer.module.css";
import { BsXLg, BsSearch } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import SkillCss from "../Components/SkillCss.css";
import ModalDeleteHobby from "../Components/ModalDeleteHobby";
import ButtonComponent from "./ButtonComponent";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

function Hobby() {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);
  const [hobbies, setHobbies] = useState([]);
  const [showHobbies, setShowHobbies] = useState([]);
  const [search, setSearch] = useState("");
  const [suggestions, setSuggestions] = useState([]);

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
      })
      .catch((err) => console.log(err));
  }, [hobbies]);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    if (value === "") {
      setSuggestions(null);
      return;
    }

    if (name === "hobbyInput") {
      // setSearch(event.target.value);
      handleSearch(event.target.value);
    }

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSearch = (searchStr) => {
    const handleFetchData = async () => {
      const response = await fetch(
        `http://localhost:8080/projetofinal/rest/user/hobby?title=${searchStr}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            token: user.token,
          },
        }
      );

      const data = await response.json();
      setSuggestions(data);
    };

    handleFetchData();
  };

  const handleClick = (event) => {
    event.preventDefault();

    if (
      credentials.hobbyInput === undefined ||
      credentials.hobbyInput === "undefined"
    ) {
      alert("Insira nome ");
    } else {
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
      setCredentials({});
    }
  };

  const handleSelection = (hobby) => {
    credentials.hobbyInput = hobby.title;

    document.getElementById("hobbyInput").value = hobby.title;

    setSuggestions([]);
  };

  return (
    <div className="container-fluid">
      <div class=" bg-secondary rounded-3 p-4 h-100 ">
        <div className="row">
          <h3 className="bg-white text-center rounded-5 p-0">
            Os meus Interesses:
          </h3>{" "}
        </div>
        <div class="row mb-3 mt-3 d-flex justify-content-between d-flex align-items-center">
          <div className="col-10 col-sm-10 col-md-10 col-lg-10">
            <input
              type="search"
              class="form-control rounded "
              placeholder="Adicionar interesse"
              required={true}
              aria-label="Search"
              aria-describedby="search-addon"
              id="hobbyInput"
              name="hobbyInput"
              defaultValue={""}
              onChange={handleChange}
              onBlur={() => setSuggestions(null)}
              /*    onKeyDown={(event) => {
                if (event.key === "Enter") {
                  handleClick(event);
                } else {
                  handleSearch(search);
                }
              }} */
            />
            <div className="dropdown bg-white">
              {suggestions &&
                suggestions.slice(0, 10).map((item) => (
                  <option key={item.id} onClick={() => handleSelection(item)}>
                    <div>{item.title}</div>
                  </option>
                ))}
            </div>
          </div>
          <div className="col-2 col-sm-2 col-md-2 col-lg-2">
            <OverlayTrigger
              placement="top"
              overlay={<Tooltip>Adicionar</Tooltip>}
            >
              <span data-bs-toggle="tooltip" data-bs-placement="top">
                {" "}
                <ButtonComponent onClick={handleClick} name={"+"} />{" "}
              </span>
            </OverlayTrigger>
          </div>
        </div>
        <div className="row overflow-auto" style={{ maxHeight: "200px" }}>
          {showHobbies && showHobbies.length !== 0 ? (
            <div className="row d-flex  ">
              {showHobbies.map((hobby) => (
                <div
                  key={hobby.id}
                  className="col-8 bg-dark mx-auto m-1 p-2 rounded-2 d-flex align-items-center text-white border border-white mb-1"
                >
                  <div className="col-lg-10">{hobby.title} </div>
                  <div className="col-lg-2">
                    <ModalDeleteHobby
                      hobby={hobby}
                      set={setHobbies}
                      setS={setShowHobbies}
                    />
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
