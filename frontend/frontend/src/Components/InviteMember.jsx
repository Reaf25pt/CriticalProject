import { useEffect, useState } from "react";
import { userStore } from "../stores/UserStore";

import ButtonComponent from "./ButtonComponent";
import InputComponent from "../Components/InputComponent";
import { toast, Toaster } from "react-hot-toast";
import { projOpenStore } from "../stores/projOpenStore";

function InviteMember() {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);
  const setPendingInvites = projOpenStore((state) => state.setPendingInvites);
  const project = projOpenStore((state) => state.project);

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
      `http://localhost:8080/projetofinal/rest/project/possiblemembers?name=${searchStr}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
          projId: project.id,
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
          projId: project.id,
        },
      })
        .then((response) => {
          if (response.status === 200) {
            toast.success("Convite enviado");

            return response.json();
          } else {
            throw new Error("Pedido não satisfeito");
          }
        })
        .then((data) => {
          setPendingInvites(data);
        })
        .catch((error) => {
          toast.error(error.message);
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
    <div className="container-fluid">
      <Toaster position="top-right" />

      <div className="mt-2 p-5 bg-secondary rounded-5 ">
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
                // onBlur={() => setSuggestions(null)}
              />
              <div
                className="dropdown bg-white"
                style={{ position: "absolute" }}
              >
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
