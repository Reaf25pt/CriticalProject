import { useState } from "react";
import ButtonComponent from "./ButtonComponent";
import InputComponent from "./InputComponent";
import SelectComponent from "../Components/SelectComponent";
import TextAreaComponent from "../Components/TextAreaComponent";
import { BsXLg, BsSearch } from "react-icons/bs";
import Keyword from "./Keyword";
import SkillsProject from "./SkillsProject";
import { userStore } from "../stores/UserStore";

function EditProject({ toggleComponent, project, set }) {
  const [credentials, setCredentials] = useState(project);
  const user = userStore((state) => state.user);
  const [keywords, setKeywords] = useState(project.keywords);
  const [skills, setSkills] = useState(project.skills);
  const addKeywords = (newKeyword) => {
    setKeywords((state) => [...state, newKeyword]);
  };
  const addSkills = (newSkill) => {
    setSkills((state) => [...state, newSkill]);
  };

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (keywords.length === 0) {
      alert("Tem de inserir 1 palavra-chave");
    } else if (
      credentials.title === null ||
      credentials.title === "undefined" ||
      credentials.title === undefined ||
      credentials.title === "" ||
      credentials.office === null ||
      credentials.office === "undefined" ||
      credentials.office === 20 ||
      credentials.office === "20" ||
      credentials.office === undefined
    ) {
      alert("Insira o nome e/ou local de trabalho do projecto");
    } else {
      var project = {
        id: credentials.id,
        title: credentials.title,
        keywords: keywords,
        skills: skills,
        membersNumber: credentials.membersNumber,
        resources: credentials.resources,
        details: credentials.details,
        office: credentials.office,
      };
      fetch("http://localhost:8080/projetofinal/rest/project/project", {
        method: "PATCH",
        headers: {
          Accept: "*/*",
          "Content-Type": "application/json",
          token: user.token,
        },
        body: JSON.stringify(project),
      }).then((response) => {
        if (response.status === 200) {
          alert("Projecto editado com sucesso");
          toggleComponent();
          set({});
        } else {
          alert("Algo correu mal");
        }
      });
    }
  };

  /*  const removeSkills = (position) => {
    console.log(position);

    setSkills((skills) => {
      const updateSkills = [...skills];
      updateSkills.splice(position, 1);
      console.log(updateSkills);
      return updateSkills;
    });
  };
  const removeKeywords = (position) => {
    console.log(position);

    setKeywords((keywords) => {
      const updateSkills = [...keywords];
      updateSkills.splice(position, 1);
      console.log(updateSkills);
      return updateSkills;
    });
  }; */
  return (
    <>
      <div className="container-fluid">
        <div className="row mt-5 justify-content-around">
          <div className="col-lg-3 ">
            <div className="row bg-secondary justify-content-center rounded-5 p-4 mb-3">
              <InputComponent
                placeholder={"Nome do projecto *"}
                id="title"
                required
                name="title"
                type="text"
                onChange={handleChange}
                defaultValue={project.title || ""}
              />
              <div class="form-group mt-3 mb-3">
                <div class="input-group rounded">
                  <SelectComponent
                    name="office"
                    id="officeInput"
                    onChange={handleChange}
                    defaultValue={project.office || "20"}
                    placeholder={"Local de trabalho "}
                    local={"Local de trabalho "}
                  />
                </div>
              </div>

              <InputComponent
                placeholder={"Número máximo de participantes"}
                id="membersNumber"
                name="membersNumber"
                type="number"
                min="1"
                onChange={handleChange}
                defaultValue={project.membersNumber || ""}
              />

              <div className="row mt-3 mb-3">
                <ButtonComponent
                  name={"Editar"}
                  onClick={handleSubmit}
                  type="submit"
                />
              </div>
              <div class="d-flex justify-content-around">
                <ButtonComponent
                  type="click"
                  name="Cancelar"
                  onClick={toggleComponent}
                />
              </div>
            </div>
          </div>
          <div className="col-lg-4 ">
            <div className="bg-secondary p-3 rounded-5 h-100">
              <div className="bg-white rounded-5 h-100 p-3">
                <h4>Descrição</h4>
                <hr />
                <TextAreaComponent
                  placeholder={"Descrição do projecto *"}
                  className="h-100 w-100"
                  id="details"
                  name="details"
                  required
                  type="text"
                  defaultValue={project.details || ""}
                  onChange={handleChange}
                />
                {/*   <textarea
      defaultValue={project.details || ""}
      className="h-75 w-100"
    ></textarea> */}
              </div>
            </div>
          </div>
          <div className="col-lg-4 ">
            <div className="bg-secondary p-3 rounded-5 h-100">
              <div className="bg-white rounded-5 h-100 p-3">
                <h4>Recursos</h4>
                <hr />
                <TextAreaComponent
                  placeholder={"Recursos"}
                  className="h-75 w-100"
                  id="resources"
                  name="resources"
                  type="text"
                  defaultValue={project.resources || ""}
                  onChange={handleChange}
                />
                {/*  <textarea
      defaultValue={project.resources || ""}
      className="h-75 w-100"
    ></textarea> */}
              </div>
            </div>
          </div>
        </div>{" "}
      </div>
      <div className="row mx-auto justify-content-around mt-5">
        <div className="col-lg-4">
          <div className="row bg-secondary rounded-5 p-4 mb-4">
            <div className="col-lg-12 bg-white rounded-5">
              <h4 className="text-center">Palavras-Chave</h4>
            </div>
            <Keyword
              keywords={keywords}
              setKeywords={setKeywords}
              addKeywords={addKeywords}
            />
            {/*   <div className="row mt-3 mx-auto">
              {project.keywords.length > 0 ? (
                <div className="row bg-white  p-2 mx-auto rounded-2 mt-3 mb-3 ">
                  <div className="form-outline  ">
                    <div className="d-flex ">
                      {project.keywords &&
                        project.keywords.map((item, position) => (
                          <>
                            <div className="bg-secondary text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                              {item.title}{" "}
                              <div className="">
                                <BsXLg
                                  onClick={() => removeKeywords(position)}
                                />
                              </div>
                            </div>
                          </>
                        ))}
                    </div>
                  </div>
                </div>
              ) : (
                "O projecto não tem palavras-chave associadas"
              )}
            </div> */}
          </div>
        </div>
        <div className="col-lg-4">
          <div className="row bg-secondary rounded-5 p-4">
            <div className="col-lg-12 bg-white rounded-5">
              <h4 className="text-center">Skills</h4>
            </div>
            <SkillsProject
              skills={skills}
              setSkills={setSkills}
              addSkills={addSkills}
            />
            {/*   {project.skills.length > 0 ? (
              <div className="row bg-white  p-2 mx-auto rounded-2 mt-3 mb-3 ">
                <div className="form-outline  ">
                  <div className="d-flex ">
                    {project.skills &&
                      project.skills.map((item, position) => (
                        <>
                          <div className="bg-secondary text-white rounded-3 p-2 m-1 d-flex justify-content-between">
                            {item.title}{" "}
                            <div className="">
                              <BsXLg onClick={() => removeSkills(position)} />
                            </div>
                          </div>
                        </>
                      ))}
                  </div>
                </div>
              </div>
            ) : (
              "O projecto não tem skills associadas"
            )} */}
          </div>
        </div>
      </div>
    </>
  );
}

export default EditProject;
