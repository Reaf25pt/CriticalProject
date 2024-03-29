import styles from "./footer.module.css";
import { BsXLg, BsSearch } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";

function Keyword({ keywords, setKeywords, addKeywords }) {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);
  const [search, setSearch] = useState("");

  const [suggestions, setSuggestions] = useState([]);
  /*   const [keywords, setKeywords] = useState([]); // lista para enviar para backend
  const addKeyword = (newKeyword) => {
    setKeywords((state) => [...state, newKeyword]);
  }; */

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    if (value === "") {
      setSuggestions(null);
      return;
    }
    if (name === "keywordInput") {
      // setSearch(event.target.value);
      handleSearch(event.target.value);
    }

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSearch = (searchStr) => {
    //setValue(searchStr);

    fetch(
      `http://localhost:8080/projetofinal/rest/project/keywords?title=${searchStr}`,
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

  const handleClick = (event) => {
    if (
      credentials.keywordInput === "" ||
      credentials.keywordInput === undefined ||
      credentials.keywordInput === "undefined" ||
      credentials === {}
    ) {
      alert("Insira nome de palavra-chave");
    } else {
      var newKeyword;
      if (credentials.id) {
        newKeyword = { id: credentials.id, title: credentials.title };
      } else {
        newKeyword = {
          title: credentials.keywordInput,
        };
      }

      addKeywords(newKeyword);
      document.getElementById("keywordInput").value = "";
      setCredentials({});
    }
  };

  const handleSelection = (keyword) => {
    credentials.id = keyword.id;
    credentials.title = keyword.title;

    document.getElementById("keywordInput").value = keyword.title;
    setSuggestions([]);
  };

  const removeKeywords = (position) => {
    setKeywords((prevKeywords) => {
      const updateKeywords = [...prevKeywords];
      updateKeywords.splice(position, 1);
      return updateKeywords;
    });
  };

  return (
    <>
      <div className="row mt-3 ">
        <div className="col-lg-9 d-flex ">
          <div className="search-select-container">
            <InputComponent
              placeholder={"Adicionar palavra-chave *"}
              id="keywordInput"
              name="keywordInput"
              type="search"
              aria-label="Search"
              aria-describedby="search-addon"
              defaultValue={""}
              onChange={handleChange}
              // onBlur={() => setSuggestions(null)}
              /*    onKeyDown={(event) => {
                if (event.key === "Enter") {
                  handleClick(event);
                } else {
                  handleSearch(search);
                }
              }} */
            />
            {/* <div className="col-lg-2 input-group-text border-0 ">
              <BsSearch />
            </div> */}
            <div className="dropdownz" style={{ position: "absolute" }}>
              {suggestions &&
                suggestions
                  .filter((item) => {
                    return (
                      item &&
                      item.title.toLowerCase().includes(search) /* !== search */
                    );
                  })
                  .slice(0, 10)
                  .map((item) => (
                    <div
                      key={item.id}
                      onClick={() => handleSelection(item)}
                      className="dropdownz-row"
                    >
                      {item.title}
                    </div>
                  ))}
            </div>
          </div>
        </div>

        <div className="col-lg-3">
          <OverlayTrigger
            placement="top"
            overlay={<Tooltip>Adicionar</Tooltip>}
          >
            <span data-bs-toggle="tooltip" data-bs-placement="top">
              {" "}
              <ButtonComponent onClick={handleClick} name={"+"} />
            </span>
          </OverlayTrigger>
        </div>
      </div>
      {keywords && keywords.length > 0 ? (
        <div
          className="row p-2 mx-auto rounded-2 mt-3 mb-3 overflow-auto"
          style={{ maxHeight: "200px" }}
        >
          {keywords.map((item, position) => (
            <>
              <div className=" col-lg-7 mx-auto text-white bg-black rounded-3 p-2 m-1 d-flex justify-content-between">
                <h4>{item.title} </h4>
                <div className="">
                  <OverlayTrigger
                    placement="top"
                    overlay={<Tooltip>Apagar</Tooltip>}
                  >
                    <span data-bs-toggle="tooltip" data-bs-placement="top">
                      {" "}
                      <BsXLg onClick={() => removeKeywords(position)} />
                    </span>
                  </OverlayTrigger>
                </div>
              </div>
            </>
          ))}
        </div>
      ) : null}
    </>
  );
}

export default Keyword;
