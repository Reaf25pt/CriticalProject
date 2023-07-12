import ButtonComponent from "./ButtonComponent";
import TextAreaComponent from "./TextAreaComponent";
import InputComponent from "./InputComponent";
import { contestOpenStore } from "../stores/ContestOpenStore";
import { userStore } from "../stores/UserStore";
import { useState } from "react";
import { toast, Toaster } from "react-hot-toast";

function EditContestComponent({ toggleComponent }) {
  const contest = contestOpenStore((state) => state.contest);
  const setContestOpen = contestOpenStore((state) => state.setContestOpen);
  const user = userStore((state) => state.user);
  const [credentials, setCredentials] = useState(contest);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (
      !credentials.title ||
      !credentials.startOpenCall ||
      !credentials.finishOpenCall ||
      !credentials.startDate ||
      !credentials.finishDate ||
      !credentials.maxNumberProjects ||
      !credentials.details ||
      !credentials.rules
    ) {
      toast.error("Insira os dados assinalados como obrigatórios");
    } else if (
      credentials.startOpenCall >= credentials.finishOpenCall ||
      credentials.finishOpenCall >= credentials.startDate ||
      credentials.startDate >= credentials.finishDate ||
      credentials.finishDate <= credentials.startDate ||
      credentials.finishDate <= credentials.startOpenCall ||
      credentials.finishDate <= credentials.finishOpenCall
    ) {
      toast.error(
        "Reveja as datas inseridas: a fase de candidaturas tem de ser anterior ao início da execução"
      );
    } else if (credentials.maxNumberProjects <= 0) {
      toast.error("Insira um número de participantes válido");
    } else {
      var editedContest = credentials;

      fetch("http://localhost:8080/projetofinal/rest/contest/", {
        method: "PATCH",
        headers: {
          Accept: "*/*",
          "Content-Type": "application/json",
          token: user.token,
        },
        body: JSON.stringify(editedContest),
      })
        .then((response) => {
          if (response.status === 200) {
            toggleComponent();
            return response.json();
          } else {
            throw new Error("Pedido não satisfeito");
          }
        })
        .then((data) => {
          setContestOpen(data);
          toast.success("Concurso editado");
        })
        .catch((error) => {
          toast.error(error.message);
        });
    }
  };

  return (
    <div class="container-fluid">
      <Toaster position="top-right" />

      <div className="row mt-5">
        <div className="col-lg-6 mx-auto bg-secondary rounded-3 p-5 mx-auto">
          <div className="row mb-5">
            <InputComponent
              placeholder={"Nome do concurso *"}
              id="title"
              required
              name="title"
              type="text"
              onChange={handleChange}
              defaultValue={contest.title || ""}
            />
            {/*   <hr className="text-white" /> */}
          </div>
          <div className="row">
            <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white">
                Data de início de candidatura *
              </h5>
              <InputComponent
                placeholder={" *"}
                id="startOpenCall"
                required
                name="startOpenCall"
                type="date"
                onChange={handleChange}
              />
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">
                Data de fim de candidatura *
              </h5>
              <InputComponent
                placeholder={" *"}
                id="finishOpenCall"
                required
                name="finishOpenCall"
                type="date"
                onChange={handleChange}
              />
            </div>
          </div>
          <div className="row mt-5">
            <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white">
                Data de início de execução *
              </h5>
              <InputComponent
                placeholder={" *"}
                id="startDate"
                required
                name="startDate"
                type="date"
                onChange={handleChange}
              />
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">
                Data de fim de execução *
              </h5>
              <InputComponent
                placeholder={" *"}
                id="finishDate"
                required
                name="finishDate"
                type="date"
                onChange={handleChange}
              />
            </div>
          </div>
          <div className="row mt-5">
            <div className="col-lg-6">
              {" "}
              <InputComponent
                placeholder={contest.maxNumberProjects}
                id="maxNumberProjects"
                name="maxNumberProjects"
                required
                type="number"
                min="1"
                onChange={handleChange}
              />
            </div>
            {/*   <div className="col-lg-6">
              <h5 className="text-center text-white">Estado </h5>
              <h3 className="text-center text-white bg-danger rounded-3  mt-3">
                Planning
              </h3>
            </div> */}
            <div className="row mb-3 mt-5">
              <ButtonComponent
                type="button"
                name="Editar"
                onClick={handleSubmit}
                //onClick={toggleComponent}
              />
            </div>
            <div className="row">
              <ButtonComponent
                type="button"
                name="Cancelar"
                onClick={toggleComponent}
              />
            </div>
          </div>
        </div>
        <div className="col-lg-3 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <TextAreaComponent
                placeholder="Descrição"
                id="details"
                name="details"
                type="text"
                onChange={handleChange}
                defaultValue={contest.details || ""}
              />
            </div>
          </div>
        </div>
        <div className="col-lg-3 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <TextAreaComponent
                placeholder="Regras do concurso"
                id="rules"
                name="rules"
                required
                type="text"
                onChange={handleChange}
                defaultValue={contest.rules || ""}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditContestComponent;
