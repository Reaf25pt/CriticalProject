import { Col, Container, Row } from "react-bootstrap";
import LinkButton from "../Components/LinkButton";

function Projects() {
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
            Projetos
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
                name={"Adicionar Projeto"}
                to={"/home/projectscreate"}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    // <Container fluid>
    //   <Row>
    //     <Col md={2} className="m-5">
    //       <LinkButton name={"Adicionar Projeto"} to={"/home/projectscreate"} />
    //     </Col>
    //     <Col md={10}></Col>
    //   </Row>
    // </Container>
  );
}

export default Projects;
