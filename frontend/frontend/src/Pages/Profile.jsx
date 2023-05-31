import { Accordion, Col, Container, Form, Image, Row } from "react-bootstrap";
import { BsFillEyeFill, BsFillEyeSlashFill } from "react-icons/bs";
import ButtonComponent from "../Components/ButtonComponent";
import style from "./profile.module.css";
import ProjectComponent from "../Components/ProjectComponent";

function Profile() {
  return (
    <Container className="ms-5 mt-5 ">
      <Row>
        <Col md={3} className="me-5">
          <Form className="m-0">
            <Row className={style.box}>
              <Image
                className={style.image}
                src="https://randomuser.me/api/portraits/men/72.jpg"
                roundedCircle
              />
              <p>Rodrigoferreira@gmail.com</p>
              <p>Rodrigo Ferreira</p>
              <p>Coimbra</p>
              <p>
                <BsFillEyeSlashFill />
              </p>
              {/* <p>
              <BsFillEyeFill />
            </p> */}
              <Row>
                <ButtonComponent name={"Editar"} />
              </Row>
            </Row>
          </Form>
        </Col>
        <Col md={4} className="">
          <h3>Lista de Projetos</h3>
          <ProjectComponent />
        </Col>
        <Col md={5}>2</Col>
      </Row>
    </Container>
  );
}

export default Profile;
