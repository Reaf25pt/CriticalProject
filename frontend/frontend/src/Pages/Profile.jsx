import { Col, Container, Image, Row } from "react-bootstrap";
import { BsFillEyeFill, BsFillEyeSlashFill } from "react-icons/bs";

function Profile() {
  return (
    <Container>
      <Row>
        <Col md={3}>
          <div>
            <Image
              src="https://randomuser.me/api/portraits/men/72.jpg"
              roundedCircle
            />
            <p>Rodrigoferreira@gmail.com</p>
            <p>Rodrigo Ferreira</p>
            <p>Coimbra</p>
            <p>
              <BsFillEyeSlashFill />
            </p>
            <p>
              <BsFillEyeFill />
            </p>
          </div>
        </Col>
        <Col md={9}>2</Col>
      </Row>
    </Container>
  );
}

export default Profile;
