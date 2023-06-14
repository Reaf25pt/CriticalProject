import styles from "./footer.module.css";
import { BsXLg, BsSearch } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import SkillCss from "../Components/SkillCss.css";

import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
import ButtonComponent from "../Components/ButtonComponent";

function Keyword() {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);
  const [search, setSearch] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [keywords, setKeywords] = useState([]);
  const addKeyword = (newKeyword) => {
    setKeywords((state) => [...state, newKeyword]);
  };

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    if (name === "keywordInput") {
      setSearch(event.target.value);
    }

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSearch = (searchStr) => {
    console.log(searchStr);
    //setValue(searchStr);

    fetch(
      `http://localhost:8080/projetofinal/rest/user/keyword?title=${searchStr}`,
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

  const handleClick = (event) => {
    event.preventDefault();

    if (
      credentials.keywordInput === undefined ||
      credentials.keywordInput === "undefined"
    ) {
      alert("Insira nome ");
    } else {
      var newKeyword;
      if (credentials.id) {
        newKeyword = { id: credentials.id, title: credentials.title };
      } else {
        newKeyword = {
          title: credentials.keywordInput,
        };
      }

      addKeyword(newKeyword);

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

  return (
    <>
      <div className="row mt-3 ">
        <div className="col-lg-6 d-flex ">
          <div className="search-select-container">
            <InputComponent
              placeholder={"Adicionar palavra-chave *"}
              id="keywordInput"
              required
              name="keywordInput"
              type="search"
              onChange={handleChange}
              defaultValue={""}
              onKeyDown={(event) => {
                if (event.key === "Enter") {
                  handleClick(event);
                } else {
                  handleSearch(search);
                }
              }}
            />
            {/* <div className="col-lg-2 input-group-text border-0 ">
              <BsSearch />
            </div> */}
          </div>
          <div className="dropdownz">
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

        <div className="col-lg-3">
          <ButtonComponent onClick={handleClick} name={"+"} />
        </div>
      </div>
      <div className="form-outline mt-3">
        <div className="bg-white d-flex ">
          {keywords &&
            keywords.map((item) => (
              <>
                <p>
                  {" "}
                  {item.title} {""}
                </p>
                <p>{"    "}</p>
              </>
            ))}
          {/* <p>Keywords</p>
          <p>Keywords</p> */}
        </div>
      </div>
    </>
  );
}

export default Keyword;
