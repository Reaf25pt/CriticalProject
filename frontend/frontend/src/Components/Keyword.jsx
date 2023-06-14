import styles from "./footer.module.css";
import { BsXLg, BsSearch } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import SkillCss from "../Components/SkillCss.css";

import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
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

    if (name === "keywordInput") {
      setSearch(event.target.value);
      console.log(event.target.value);
      console.log(search);
    }

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
    console.log(credentials.keywordInput);
  };

  const handleSearch = (searchStr) => {
    console.log(searchStr);
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
        console.log(suggestions);
      });
  };

  const handleClick = (event) => {
    if (
      credentials.keywordInput === "" ||
      credentials.keywordInput === undefined ||
      credentials.keywordInput === "undefined" ||
      credentials === {}
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

      addKeywords(newKeyword);
      //console.log(keywords);
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
              name="keywordInput"
              type="search"
              aria-label="Search"
              aria-describedby="search-addon"
              defaultValue={""}
              onChange={handleChange}
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
                <div className="keyword-create-project">{item.title}</div>
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