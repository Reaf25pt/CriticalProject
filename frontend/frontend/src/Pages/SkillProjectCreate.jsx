import { BsXLg, BsSearch } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

import InputComponent from "../Components/InputComponent";
import ButtonComponent from "../Components/ButtonComponent";
import SelectSkillType from "../Components/SelectSkillType";
import { toast, Toaster } from "react-hot-toast";

function SkillProjectCreate({ skills, setSkills, addSkills }) {
  const [credentials, setCredentials] = useState({});
  const user = userStore((state) => state.user);
  const [search, setSearch] = useState("");
  const [selectedValue, setSelectedValue] = useState("");
  const [suggestions, setSuggestions] = useState([]);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    if (value === "") {
      setSuggestions(null);
      return;
    }
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
      toast.error("Insira nome e / ou categoria de skill ");
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
  };

  return (
    <div className="cointaner-fluid">
      <Toaster position="top-right" />

      <div className="row mt-3">
        <div className="col-lg-6">
          <InputComponent
            placeholder={"Adicionar skill "}
            id="skillProjInput"
            name="skillProjInput"
            type="search"
            aria-label="Search"
            aria-describedby="search-addon"
            defaultValue={""}
            onChange={handleChange}
          />

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
        <div class="col-lg-4">
          <div class="input-group rounded">
            <SelectSkillType
              name="skillType"
              id="skillType"
              onChange={handleChange}
              placeholder={"Categoria"}
              defaultValue={selectedValue}
            />
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
        <div
          className="row  p-2 mx-auto rounded-2 mt-3 mb-3 overflow-auto d-flex justify-content-between mx-auto"
          style={{ maxHeight: "200px" }}
        >
          {skills &&
            skills.map(
              (item, position) =>
                item.skillType == 0 ? (
                  <div className="col-lg-3 bg-danger text-white rounded-3 p-1 m-1 d-flex justify-content-between">
                    <h6>{item.title} </h6>
                    <div className="">
                      <OverlayTrigger
                        placement="top"
                        overlay={<Tooltip>Apagar</Tooltip>}
                      >
                        <span data-bs-toggle="tooltip" data-bs-placement="top">
                          {" "}
                          <BsXLg onClick={() => removeSkills(position)} />
                        </span>
                      </OverlayTrigger>
                    </div>
                  </div>
                ) : item.skillType == 1 ? (
                  <div className="col-lg-3  bg-success text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                    <h6>{item.title} </h6>
                    <div className="">
                      <OverlayTrigger
                        placement="top"
                        overlay={<Tooltip>Apagar</Tooltip>}
                      >
                        <span data-bs-toggle="tooltip" data-bs-placement="top">
                          {" "}
                          <BsXLg onClick={() => removeSkills(position)} />
                        </span>
                      </OverlayTrigger>
                    </div>
                  </div>
                ) : item.skillType == 2 ? (
                  <div className="col-lg-3 bg-primary text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                    <h6>{item.title} </h6>
                    <div className="">
                      <OverlayTrigger
                        placement="top"
                        overlay={<Tooltip>Apagar</Tooltip>}
                      >
                        <span data-bs-toggle="tooltip" data-bs-placement="top">
                          {" "}
                          <BsXLg onClick={() => removeSkills(position)} />
                        </span>
                      </OverlayTrigger>
                    </div>
                  </div>
                ) : item.skillType == 3 ? (
                  <div className="col-lg-3  bg-warning text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                    <h5>{item.title} </h5>
                    <div className="">
                      <OverlayTrigger
                        placement="top"
                        overlay={<Tooltip>Apagar</Tooltip>}
                      >
                        <span data-bs-toggle="tooltip" data-bs-placement="top">
                          {" "}
                          <BsXLg onClick={() => removeSkills(position)} />
                        </span>
                      </OverlayTrigger>
                    </div>
                  </div>
                ) : (
                  <div className="col-lg-3 bg-info text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                    {item.title}{" "}
                    <div className="">
                      <OverlayTrigger
                        placement="top"
                        overlay={<Tooltip>Apagar</Tooltip>}
                      >
                        <span data-bs-toggle="tooltip" data-bs-placement="top">
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
      ) : null}
    </div>
  );
}

export default SkillProjectCreate;
