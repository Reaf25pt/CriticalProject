import { Col, Container, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";

function Contest() {
  return (
    <div>
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
            Concursos
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
          <div className="row">
            <div className="col mt-5">
              //{" "}
              <LinkButton
                name={"Adicionar Concurso"}
                to={"/home/contestcreate"}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Contest;
