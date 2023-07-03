import TextAreaComponent from "../Components/TextAreaComponent";
import ButtonComponent from "./ButtonComponent";
import { contestOpenStore } from "../stores/ContestOpenStore";
import { userStore } from "../stores/UserStore";
import ModalConcludeContest from "./ModalConcludeContest";
import ModalChangeContestStatus from "./ModalChangeContestStatus";

function ContestComponent({ toggleComponent }) {
  const contest = contestOpenStore((state) => state.contest);
  const user = userStore((state) => state.user);
  const setContestOpen = contestOpenStore((state) => state.setContestOpen);
  const ownProj = userStore((state) => state.ownProj);
  const setProjList = contestOpenStore((state) => state.setProjectList);
  const projList = contestOpenStore((state) => state.projectList);
  const answeredProjects = projList.filter((item) => item.answered); // projectos já respondidos

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
  };

  const renderApplyButton =
    ownProj &&
    ownProj.id !== 0 &&
    !answeredProjects.some(
      (project) => project.id === ownProj.id
      // botão para candidatar a projecto só aparece se projecto activo do token não estiver na lista de respondidos
    );

  function canButtonRender(contestDate) {
    // renderiza botão se given data for igual ou anterior à data de hoje
    // no backend uma das validações para alterar status é esta
    const currentDate = Date.now();
    const contDate = new Date(contestDate).getTime();

    return currentDate >= contDate;
  }

  const applyToContest = (event) => {
    event.preventDefault();

    fetch("http://localhost:8080/projetofinal/rest/contest/application", {
      method: "POST",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
        contestId: contest.id,
      },
    }).then((response) => {
      if (response.status === 200) {
        alert("Candidatura com sucesso");
        //navigate("/home", { replace: true });
      } else {
        alert("Algo correu mal");
      }
    });
  };

  return (
    <div class="container-fluid">
      <div className="row mt-5">
        <div className="col-lg-5 mx-auto bg-secondary rounded-3 p-5 mx-auto">
          <div className="row mb-5">
            <h2 className="text-center text-white">{contest.title}</h2>
            <hr className="text-white" />
          </div>
          <div className="row">
            <h5 className="text-center text-white">Candidaturas</h5>
            <h3 className="text-center text-white">
              {convertTimestampToDate(contest.startOpenCall)} -{" "}
              {convertTimestampToDate(contest.finishOpenCall)}
            </h3>
          </div>

          <div className="row">
            <h5 className="text-center text-white">
              Período de execução dos projectos
            </h5>
            <h3 className="text-center text-white">
              {convertTimestampToDate(contest.startDate)} -{" "}
              {convertTimestampToDate(contest.finishDate)}
            </h3>
          </div>

          <div className="row mt-5">
            <div className="row d-flex align-items-center">
              {contest.statusInt !== 0 ? (
                <div className="col-lg-6">
                  {" "}
                  <h5 className="text-center text-white mb-3">
                    Projectos participantes
                  </h5>
                  <h3 className="text-center text-white bg-warning rounded-3 p-2 mt-3">
                    {answeredProjects.length} / {contest.maxNumberProjects}
                  </h3>
                </div>
              ) : (
                <div className="col-lg-6">
                  <h5 className="text-center text-white mb-3">
                    Vagas disponíveis
                  </h5>
                  <h3 className="text-center text-white bg-warning rounded-3 p-2 mt-3">
                    {contest.maxNumberProjects}
                  </h3>
                </div>
              )}
              <div className="col-lg-6">
                <h5 className="text-center text-white mb-3">Estado</h5>
                <h3 className="text-center text-white bg-danger rounded-3 p-2 mt-3">
                  {contest.status}
                </h3>
              </div>
            </div>
            <div className="row mx-auto justify-content-around mt-5">
              {contest.statusInt === 0 && user.contestManager ? (
                <>
                  <div className="col-lg-12">
                    <ButtonComponent
                      type="button"
                      name="Editar concurso"
                      onClick={toggleComponent}
                    />
                  </div>
                  {canButtonRender(contest.startOpenCall) ? (
                    <div className="col-lg-12">
                      <ModalChangeContestStatus />
                      {/*    <ButtonComponent
                        type="button"
                        name="Abrir candidaturas: Open"
                        onClick={() => handleStatus(1)}
                      /> */}
                    </div>
                  ) : null}
                </>
              ) : contest.statusInt === 1 &&
                user.contestManager &&
                canButtonRender(contest.startDate) ? (
                <div className="col-lg-12">
                  <ModalChangeContestStatus />
                  {/*   <ButtonComponent
                    type="button"
                    name="Fechar candidaturas: Ongoing"
                    onClick={() => handleStatus(2)}
                  /> */}
                </div>
              ) : contest.statusInt === 2 &&
                user.contestManager &&
                canButtonRender(contest.finishDate) ? (
                <div className="col-lg-12">
                  <ModalConcludeContest />
                  {/*  <ButtonComponent
                    type="button"
                    name="Terminar concurso"
                    onClick={() => handleStatus(3)}
                  /> */}
                </div>
              ) : null}
              {contest.statusInt === 1 &&
              renderApplyButton &&
              !user.contestManager ? (
                <div className="row mx-auto justify-content-around mt-5">
                  <div className="col-lg-12">
                    <ButtonComponent
                      type="button"
                      name="Concorrer com o meu projecto"
                      onClick={applyToContest}
                    />
                  </div>
                </div>
              ) : (
                <div className="row"></div>
              )}
            </div>{" "}
          </div>
        </div>
        <div className="col-lg-3 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Descrição</h4>
              <hr />
              <h5>{contest.details}</h5>
            </div>
          </div>
        </div>
        <div className="col-lg-3 ">
          <div className="bg-secondary p-3 rounded-5 h-100">
            <div className="bg-white rounded-5 h-100 p-3">
              <h4>Regras do concurso</h4>
              <hr />
              <h5>{contest.rules}</h5>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ContestComponent;
