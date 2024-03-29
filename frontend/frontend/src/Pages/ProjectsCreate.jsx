import InputComponent from "../Components/InputComponent";
import SelectComponent from "../Components/SelectComponent";
import ButtonComponent from "../Components/ButtonComponent";
import TextAreaComponent from "../Components/TextAreaComponent";
import { BsSearch, BsArrowDown } from "react-icons/bs";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import Keyword from "../Components/Keyword";
import SkillsProject from "../Components/SkillsProject";
import SkillProjectCreate from "./SkillProjectCreate";
import KeywordProjectCreate from "./KeywordProjectCreate";
import { toast, Toaster } from "react-hot-toast";

function ProjectsCreate() {
  const [credentials, setCredentials] = useState({});
  const navigate = useNavigate();
  const user = userStore((state) => state.user);
  const updateUser = userStore((state) => state.updateUser);

  const [keywords, setKeywords] = useState([]); // lista para enviar para backend
  const addKeywords = (newKeyword) => {
    setKeywords((state) => [...state, newKeyword]);
  };
  const [skills, setSkills] = useState([]); // lista para enviar para backend
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
      toast.error("Tem de inserir 1 palavra-chave");
    } else if (
      credentials.projectName === null ||
      credentials.projectName === "undefined" ||
      credentials.projectName === undefined ||
      credentials.projectName === "" ||
      credentials.details === null ||
      credentials.details === "undefined" ||
      credentials.details === undefined ||
      credentials.details === ""
    ) {
      toast.error("Insira, pelo menos, o nome e descrição do projecto");
    } else {
      if (
        credentials.office === null ||
        credentials.office === "undefined" ||
        credentials.office === 20 ||
        credentials.office === "20" ||
        credentials.office === undefined
      ) {
        var project = {
          title: credentials.projectName,
          keywords: keywords,
          skills: skills,
          membersNumber: credentials.maxMembers,
          resources: credentials.resources,
          details: credentials.details,
          office: "20",
        };
      } else {
        var project = {
          title: credentials.projectName,
          keywords: keywords,
          skills: skills,
          membersNumber: credentials.maxMembers,
          resources: credentials.resources,
          details: credentials.details,
          office: credentials.office,
        };
      }

      fetch("http://localhost:8080/projetofinal/rest/project/newproject", {
        method: "POST",
        headers: {
          Accept: "*/*",
          "Content-Type": "application/json",
          token: user.token,
        },
        body: JSON.stringify(project),
      }).then((response) => {
        if (response.status === 200) {
          alert("Projeto criado com sucesso");
          updateUser("noActiveProject", false);
          navigate("/home/start", { replace: true });
        } else {
          toast.error("Pedido não satisfeito");
        }
      });
    }
  };

  return (
    <div>
      <Toaster />
      <ul className="nav nav-tabs" id="myTab" role="tablist">
        <li className="nav-item" role="presentation">
          <button
            className="nav-link active"
            id="home-tab"
            data-bs-toggle="tab"
            data-bs-target="#home"
            type="button"
            role="tab"
            aria-controls="home"
            aria-selected="true"
            style={{ background: "#C01722", color: "white" }}
          >
            Criar Projeto
          </button>
        </li>
      </ul>
      <div className="tab-content" id="myTabContent">
        <div
          className="tab-pane fade show active"
          id="home"
          role="tabpanel"
          aria-labelledby="home-tab"
        >
          <div className="row mx-auto col-10 col-md-8 col-lg-6">
            <div className="mt-5 p-5 bg-secondary rounded-5  ">
              <div className="row mb-3">
                <div className="col ">
                  <div className="form-outline">
                    <InputComponent
                      placeholder={"Nome do projecto *"}
                      id="projectName"
                      required
                      name="projectName"
                      type="text"
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>
              {/*  <Keyword
                keywords={keywords}
                setKeywords={setKeywords}
                addKeywords={addKeywords}
              /> */}
              <KeywordProjectCreate
                keywords={keywords}
                setKeywords={setKeywords}
                addKeywords={addKeywords}
              />

              <div class="form-outline mb-4 mt-3">
                <TextAreaComponent
                  placeholder={"Descrição do projecto *"}
                  id="details"
                  name="details"
                  required
                  type="text"
                  onChange={handleChange}
                />
              </div>

              {/*   <SkillsProject
                skills={skills}
                setSkills={setSkills}
                addSkills={addSkills}
              /> */}
              <SkillProjectCreate
                skills={skills}
                setSkills={setSkills}
                addSkills={addSkills}
              />

              <div class="row mt-3 mb-3 d-flex justify-content-around">
                <div class=" col-lg-6 mx-auto">
                  <SelectComponent
                    name="office"
                    id="officeInput"
                    onChange={handleChange}
                    defaultValue={"20"}
                    placeholder={"Local de trabalho "}
                    local={"Local de trabalho "}
                  />
                </div>
              </div>

              <div className="form-outline mb-4">
                <InputComponent
                  placeholder={"Número máximo de participantes"}
                  id="maxMembers"
                  name="maxMembers"
                  type="number"
                  min="1"
                  onChange={handleChange}
                />
              </div>

              <div class="form-outline mb-4">
                <TextAreaComponent
                  placeholder={"Recursos necessários"}
                  id="resources"
                  name="resources"
                  type="text"
                  onChange={handleChange}
                />
              </div>

              <div className="row">
                <ButtonComponent
                  name={"Criar"}
                  type="submit"
                  onClick={handleSubmit}
                />
              </div>
            </div>{" "}
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProjectsCreate;
