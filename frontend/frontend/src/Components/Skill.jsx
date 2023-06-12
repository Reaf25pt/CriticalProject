import styles from "./footer.module.css";
import { BsArrowDown, BsSearch, BsXLg } from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { useEffect, useState } from "react";
import SelectSkillType from "../Components/SelectSkillType";
import ModalDeleteUserSkill from "../Components/ModalDeleteUserSkill";
import SkillCss from "../Components/SkillCss.css";

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

    if (name === "skillInput") {
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
        console.log(suggestions);
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
    <div class="bg-secondary rounded-3 p-4 h-100">
      <h3 className="bg-white text-center text-nowrap rounded-5 p-0  ">
        As Minhas Skills:
      </h3>
      <div class="input-group rounded mb-3 mt-3">
        <div>
          <input
            type="search"
            class="form-control rounded "
            placeholder="Adicionar skill"
            required={true}
            aria-label="Search"
            aria-describedby="search-addon"
            id="skillInput"
            name="skillInput"
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

          <div>
            {" "}
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
        </div>

        <div class="form-group">
          <div class="input-group rounded">
            <SelectSkillType
              name="skillType"
              id="skillType"
              onChange={handleChange}
              placeholder={"Categoria"}
              defaultValue={selectedValue}
            />
            <span class="input-group-text border-0" id="search-addon">
              <BsArrowDown />
            </span>
          </div>
        </div>
        <span class="input-group-text border-0">
          <BsSearch />
        </span>
      </div>

      <div className="row  d-flex justify-content-around ">
        {showSkills && showSkills.length !== 0 ? (
          <div className="row d-flex   ">
            {showSkills.map(
              (skill) =>
                skill.skillType === 0 ? (
                  <div
                    key={skill.id}
                    className="d-flex justify-content-between  align-items-center  w-50  bg-danger m-0 rounded-3 pb-1 p-2"
                  >
                    <div className="col-lg-10">{skill.title} </div>
                    <div className="col-lg-2">
                      {" "}
                      <ModalDeleteUserSkill skill={skill} set={setSkills} />
                      {/*  <BsXLg onclick={handleDelete} /> */}
                    </div>
                  </div>
                ) : skill.skillType === 1 ? (
                  <div
                    key={skill.id}
                    className="d-flex justify-content-between  align-items-center  w-50  bg-success m-0 rounded-3 pb-1 p-2"
                  >
                    <div className="col-lg-10">{skill.title} </div>
                    <div className="col-lg-2">
                      {" "}
                      <ModalDeleteUserSkill skill={skill} set={setSkills} />
                      {/*  <BsXLg onclick={handleDelete} /> */}
                    </div>
                  </div>
                ) : skill.skillType === 2 ? (
                  <div
                    key={skill.id}
                    className="d-flex justify-content-between  align-items-center  w-50  bg-primary m-0 rounded-3 pb-1 p-2"
                  >
                    <div className="col-lg-10">{skill.title} </div>
                    <div className="col-lg-2">
                      {" "}
                      <ModalDeleteUserSkill skill={skill} set={setSkills} />
                      {/*  <BsXLg onclick={handleDelete} /> */}
                    </div>
                  </div>
                ) : (
                  <div
                    key={skill.id}
                    className="d-flex justify-content-between  align-items-center  w-50  bg-warning m-0 rounded-3 pb-1 p-2"
                  >
                    <div className="col-lg-10">{skill.title} </div>
                    <div className="col-lg-2">
                      {" "}
                      <ModalDeleteUserSkill skill={skill} set={setSkills} />
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
