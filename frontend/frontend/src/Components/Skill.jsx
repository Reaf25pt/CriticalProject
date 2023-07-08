import styles from "./footer.module.css";
import { BsArrowDown, BsSearch, BsXLg } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import SelectSkillType from "../Components/SelectSkillType";
import ModalDeleteUserSkill from "../Components/ModalDeleteUserSkill";
import SkillCss from "../Components/SkillCss.css";
import ButtonComponent from "./ButtonComponent";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

function Skill() {
  const [credentials, setCredentials] = useState({});
  const [search, setSearch] = useState("");
  const user = userStore((state) => state.user);
  const [skills, setSkills] = useState([]);
  const [showSkills, setShowSkills] = useState([]);
  const [selectedValue, setSelectedValue] = useState("");
  const [suggestions, setSuggestions] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/projetofinal/rest/user/ownskills", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((response) => response.json())
      .then((response) => {
        setShowSkills(response);
      })
      .catch((err) => console.log(err));
  }, [skills]);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    if (value === "") {
      setSuggestions(null);
      return;
    }
    if (name === "skillInput") {
      // setSearch(event.target.value);
      handleSearch(event.target.value);
    }

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSearch = (searchStr) => {
    fetch(
      `http://localhost:8080/projetofinal/rest/user/skills?title=${searchStr}`,
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
    event.preventDefault();

    if (
      credentials.skillInput === undefined ||
      credentials.skillInput === "undefined" ||
      credentials.skillType === null ||
      credentials.skillType === "undefined" ||
      credentials.skillType === 20 ||
      credentials.skillType === "20" ||
      credentials.skillType === undefined
      /*  Acho que basta ter undefined */
    ) {
      alert("Insira nome e / ou categoria ");
    } else {
      const skill = {
        title: credentials.skillInput,
        skillType: credentials.skillType,
      };

      fetch("http://localhost:8080/projetofinal/rest/user/skill", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
        },
        body: JSON.stringify(skill),
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
          setSkills((values) => [...values, response]);
        });
      document.getElementById("skillInput").value = "";
      document.getElementById("skillType").value = "20";
      setCredentials({});

      setSelectedValue("");
    }
  };

  const handleSelection = (skill) => {
    credentials.skillInput = skill.title;
    credentials.skillType = skill.skillType;
    document.getElementById("skillInput").value = skill.title;

    setSuggestions([]);
  };

  return (
    <div className=" bg-secondary rounded-3 p-4 h-100">
      <div className="row">
        <h3 className="bg-white text-center text-nowrap rounded-5 p-0  ">
          As Minhas Skills:
        </h3>
      </div>
      <div class="row mb-3 mt-3 mb-3 d-flex justify-content-between d-flex align-items-center">
        <div className="col-6 col-sm-6 col-md-4 col-lg-6  ">
          <input
            type="search"
            class="rounded col-lg-12 p-1 "
            placeholder="Adicionar skill"
            required={true}
            aria-label="Search"
            aria-describedby="search-addon"
            id="skillInput"
            name="skillInput"
            defaultValue={""}
            onChange={handleChange}
            // onBlur={() => setSuggestions(null)}
          />{" "}
          <div className="dropdownz " style={{ position: "absolute" }}>
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
                    className="dropdownz-row "
                  >
                    {item.title}
                  </div>
                ))}
          </div>
        </div>
        <div class="form-group col-2 col-sm-6 col-md-6 col-lg-4">
          <div class="input-group ">
            <SelectSkillType
              name="skillType"
              id="skillType"
              onChange={handleChange}
              placeholder={"Categoria *"}
              defaultValue={selectedValue}
            />
            {/* <span class="input-group-text border-0" id="search-addon">
              <BsArrowDown />
            </span> */}
          </div>
        </div>
        <div className="col-2 col-sm-2 col-md-2 col-lg-2">
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

      {/*  <span class="input-group-text border-0">
          <BsSearch />
        </span> */}

      <div className="row overflow-auto" style={{ maxHeight: "200px" }}>
        {showSkills && showSkills.length !== 0 ? (
          <div className="row">
            {showSkills.map(
              (skill) =>
                skill.skillType === 0 ? (
                  <div
                    key={skill.id}
                    className="col-7 col-lg-7 mx-auto   bg-danger m-0 rounded-2 d-flex align-items-center text-white border border-white mb-1"
                  >
                    <div className="col-lg-10 ">
                      <h4>{skill.title} </h4>{" "}
                    </div>
                    <div className="col-lg-2  ">
                      {" "}
                      <ModalDeleteUserSkill
                        skill={skill}
                        set={setSkills}
                        setS={setShowSkills}
                      />
                    </div>
                  </div>
                ) : skill.skillType === 1 ? (
                  <div
                    key={skill.id}
                    className="col-7 col-lg-7  mx-auto  bg-success m-0 rounded-2 d-flex align-items-center text-white border border-white mb-1"
                  >
                    <div className="col-lg-10  ">
                      {" "}
                      <h4>{skill.title} </h4>{" "}
                    </div>
                    <div className="col-lg-2 ">
                      {" "}
                      <ModalDeleteUserSkill
                        skill={skill}
                        set={setSkills}
                        setS={setShowSkills}
                      />
                    </div>
                  </div>
                ) : skill.skillType === 2 ? (
                  <div
                    key={skill.id}
                    className=" col-7  col-lg-7 bg-primary d-flex mx-auto  m-0 rounded-2 d-flex align-items-center text-white border border-white mb-1"
                  >
                    <div className="col-lg-10 overflow-auto">
                      {" "}
                      <h4>{skill.title} </h4>{" "}
                    </div>
                    <div className="col-lg-2">
                      {" "}
                      <ModalDeleteUserSkill
                        skill={skill}
                        set={setSkills}
                        setS={setShowSkills}
                      />
                      {/*  <BsXLg onclick={handleDelete} /> */}
                    </div>
                  </div>
                ) : (
                  <div
                    key={skill.id}
                    className="col-7 col-lg-7 mx-auto  bg-warning d-flex mx-auto   m-0 rounded-2 d-flex align-items-center text-white border border-white mb-1 "
                  >
                    <div className="col-lg-10">
                      {" "}
                      <h4>{skill.title} </h4>{" "}
                    </div>
                    <div className="col-lg-2">
                      {" "}
                      <ModalDeleteUserSkill
                        skill={skill}
                        set={setSkills}
                        setS={setShowSkills}
                      />
                      {/*  <BsXLg onclick={handleDelete} /> */}
                    </div>
                  </div>
                )

              /* 
              <div
                key={skill.id}
                className="d-flex justify-content-between  align-items-center  w-50  bg-white m-0 rounded-3 pb-1 p-2"
              >
                <div className="col-lg-10">{skill.title} </div>
                <div className="col-lg-2">
                  {" "}
                  <ModalDeleteUserSkill skill={skill} set={setSkills} />
                  {/*  <BsXLg onclick={handleDelete} />  
                 </div>
              </div>  */
            )}
          </div>
        ) : (
          <p>Adicione as suas skills</p>
        )}
      </div>
    </div>
  );
}

export default Skill;
