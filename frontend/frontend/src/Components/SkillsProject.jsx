import styles from "./footer.module.css";
import { BsXLg, BsSearch } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import SelectSkillType from "../Components/SelectSkillType";

function SkillsProject({ skills, setSkills, addSkills }) {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);
  const [search, setSearch] = useState("");
  const [selectedValue, setSelectedValue] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  /*   const [keywords, setKeywords] = useState([]); // lista para enviar para backend
  const addKeyword = (newKeyword) => {
    setKeywords((state) => [...state, newKeyword]);
  }; */

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    if (name === "skillProjInput") {
      // setSearch(event.target.value);
      handleSearch(event.target.value);
    }

    if (name === "skillType") {
    }

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSearch = (searchStr) => {
    //setValue(searchStr);

    fetch(
      `http://localhost:8080/projetofinal/rest/project/skills?title=${searchStr}`,
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
      credentials.skillProjInput === "" ||
      credentials.skillProjInput === undefined ||
      credentials.skillProjInput === "undefined" ||
      credentials.skillType === null ||
      credentials.skillType === "undefined" ||
      credentials.skillType === 20 ||
      credentials.skillType === "20" ||
      credentials.skillType === undefined ||
      credentials === {}
    ) {
      alert("Insira nome e / ou categoria de skill ");
    } else {
      var newSkill;
      if (credentials.id) {
        newSkill = {
          id: credentials.id,
          title: credentials.title,
          skillType: credentials.skillType,
        };
      } else {
        newSkill = {
          title: credentials.skillProjInput,
          skillType: credentials.skillType,
        };
      }

      addSkills(newSkill);
      document.getElementById("skillProjInput").value = "";
      document.getElementById("skillType").value = "20";
      setCredentials({});
      setSelectedValue("");
    }
  };

  const handleSelection = (skill) => {
    credentials.id = skill.id;
    credentials.title = skill.title;
    credentials.skillType = skill.skillType;

    document.getElementById("skillProjInput").value = skill.title;
    setSuggestions([]);
  };

  const removeSkills = (position) => {
    setSkills((prevSkills) => {
      const updateSkills = [...prevSkills];
      updateSkills.splice(position, 1);
      return updateSkills;
    });
    /*   setKeywords((prevKeywords) => {
      const updateKeywords = [...prevKeywords];
      updateKeywords.splice(position, 1);
      return updateKeywords;



        const removeKeywords = (position) => {
   
  };
    }); */
  };

  return (
    <div className="cointaner-fluid">
      <div className="row mt-3">
        <div className="col-lg-6">
          <div className="search-select-container ">
            <InputComponent
              placeholder={"Adicionar skill "}
              id="skillProjInput"
              name="skillProjInput"
              type="search"
              aria-label="Search"
              aria-describedby="search-addon"
              defaultValue={""}
              onChange={handleChange}
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
        <div class="form-group col-lg-4">
          <div class="input-group rounded">
            <SelectSkillType
              name="skillType"
              id="skillType"
              onChange={handleChange}
              placeholder={"Categoria"}
              defaultValue={selectedValue}
            />
            {/* <span class="input-group-text border-0" id="search-addon">
              <BsArrowDown />
            </span> */}
          </div>
        </div>
        <div className="col-lg-2">
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
      {skills.length > 0 ? (
        <div className="row bg-white  p-2 mx-auto rounded-2 mt-3 mb-3 ">
          <div className="form-outline  ">
            <div className="d-flex ">
              {skills &&
                skills.map(
                  (item, position) =>
                    item.skillType == 0 ? (
                      <div className="bg-danger text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                        {item.title}{" "}
                        <div className="">
                          <OverlayTrigger
                            placement="top"
                            overlay={<Tooltip>Apagar</Tooltip>}
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <BsXLg onClick={() => removeSkills(position)} />
                            </span>
                          </OverlayTrigger>
                        </div>
                      </div>
                    ) : item.skillType == 1 ? (
                      <div className="bg-success text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                        {item.title}{" "}
                        <div className="">
                          <OverlayTrigger
                            placement="top"
                            overlay={<Tooltip>Apagar</Tooltip>}
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <BsXLg onClick={() => removeSkills(position)} />
                            </span>
                          </OverlayTrigger>
                        </div>
                      </div>
                    ) : item.skillType == 2 ? (
                      <div className="bg-primary text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                        {item.title}{" "}
                        <div className="">
                          <OverlayTrigger
                            placement="top"
                            overlay={<Tooltip>Apagar</Tooltip>}
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <BsXLg onClick={() => removeSkills(position)} />
                            </span>
                          </OverlayTrigger>
                        </div>
                      </div>
                    ) : item.skillType == 3 ? (
                      <div className="bg-warning text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                        {item.title}{" "}
                        <div className="">
                          <OverlayTrigger
                            placement="top"
                            overlay={<Tooltip>Apagar</Tooltip>}
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <BsXLg onClick={() => removeSkills(position)} />
                            </span>
                          </OverlayTrigger>
                        </div>
                      </div>
                    ) : (
                      <div className="bg-info text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                        {item.title}{" "}
                        <div className="">
                          <OverlayTrigger
                            placement="top"
                            overlay={<Tooltip>Apagar</Tooltip>}
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <BsXLg onClick={() => removeSkills(position)} />
                            </span>
                          </OverlayTrigger>
                        </div>
                      </div>
                    ) /* (
                      <>
                        <div className="bg-secondary text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                          {item.title}{" "}
                          <div className="">
                            <BsXLg onClick={() => removeSkills(position)} />
                          </div>
                        </div>
                      </>
                    ) */
                )}
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

export default SkillsProject;
