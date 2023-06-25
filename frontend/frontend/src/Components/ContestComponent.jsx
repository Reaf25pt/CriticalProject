import TextAreaComponent from "../Components/TextAreaComponent";
import ButtonComponent from "./ButtonComponent";
import { contestOpenStore } from "../stores/ContestOpenStore";
import { userStore } from "../stores/UserStore";

function ContestComponent({ toggleComponent }) {
  const contest = contestOpenStore((state) => state.contest);
  const user = userStore((state) => state.user);

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
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
            {/*   <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white">
                Início das candidaturas
              </h5>
              <h3 className="text-center text-white">
                {convertTimestampToDate(contest.startOpenCall)}
              </h3>
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">Fim das candidaturas</h5>
              <h3 className="text-center text-white">
                {convertTimestampToDate(contest.finishOpenCall)}
              </h3>
            </div> */}
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
          {/*   <div className="row mt-5">
            <div className="col-lg-6">
              {" "}
              <h5 className="text-center text-white">
                Início da fase de execução do concurso
              </h5>
              <h3 className="text-center text-white">
                {" "}
                {convertTimestampToDate(contest.startDate)}
              </h3>
            </div>
            <div className="col-lg-6">
              <h5 className="text-center text-white">
                Fim da fase de execução do concurso
              </h5>
              <h3 className="text-center text-white">
                {" "}
                {convertTimestampToDate(contest.finishDate)}
              </h3>
            </div>
          </div> */}
          <div className="row mt-5">
            {contest.statusInt !== 0 ? (
              <div className="col-lg-6">
                {" "}
                <h5 className="text-center text-white mb-3">
                  Projectos participantes
                </h5>
                <h3 className="text-center text-white bg-warning rounded-3 p-2 mt-3">
                  Inserir size da lista de projectos
                </h3>
              </div>
            ) : (
              <div className="col-lg-6">
                {" "}
                <h5 className="text-center text-white mb-3">
                  Vagas disponíveis a concurso
                </h5>
                <h3 className="text-center text-white bg-warning rounded-3 p-2 mt-3">
                  {contest.maxNumberProjects}
                </h3>
              </div>
            )}
            <div className="col-lg-6">
              {/*    <h5 className="text-center text-white">Estado </h5> */}
              <h3 className="text-center text-white bg-danger rounded-3 p-2 mt-3">
                {contest.status}
              </h3>
            </div>
            <div className="row mx-auto justify-content-around mt-5">
              {contest.statusInt === 0 && user.contestManager ? (
                <div className="col-lg-12">
                  <ButtonComponent
                    type="button"
                    name="Editar Concurso"
                    onClick={toggleComponent}
                  />
                </div>
              ) : null}
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
