import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

import Form from "react-bootstrap/Form";

import { useState, useEffect } from "react";

import { userStore } from "../stores/UserStore";
import { projOpenStore } from "../stores/projOpenStore";
import "./Chat.css";

function ProjectChat({ project }) {
  const [credentials, setCredentials] = useState({});

  const [chatMessages, setMessageChat] = useState([]);
  const user = userStore((state) => state.user);
  const [showChatMessages, setShowMessageChat] = useState([]);
  // const chatMessages = projOpenStore((state) => state.chatMessages); // array
  //const updateChatMessages = projOpenStore((state) => state.updateChatMessages); // set do array
  //const addChatMessage = projOpenStore((state) => state.addChatMessage); // add nova msg
  // const addChatMessage = (newChatMessage) =>
  //  setMessageChat((prevChatMessages) => [...prevChatMessages, newChatMessage]);

  const { id } = useParams(); // id do projecto

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/project/chat/${id}`, {
      method: "GET",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((response) => response.json())
      .then((response) => {
        setShowMessageChat(response);

        const element = document.getElementById("chatBox");

        element.scrollIntoView({ behavior: "smooth" });
      })
      .catch((err) => console.log(err));
  }, [chatMessages]);

  const handleClick = (event) => {
    event.preventDefault();

    const newMessage = {
      message: credentials.messageInput,
      userSenderId: user.userId,
    };

    fetch(`http://localhost:8080/projetofinal/rest/project/chat/${id}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
      body: JSON.stringify(newMessage),
    })
      .then((response) => {
        if (response.status === 200) {
          return response.json();
        }
      })
      .then((response) => {
        // addChatMessage(response);
        setMessageChat([]);
        document.getElementById("messageInput").value = "";
      });
  };

  /*   if (!project) {
    return <div>Loading...</div>;
  } */
  return (
    <>
      <Container id="topContainerWholeRightSideChat">
        <Container id="chatBox">
          {showChatMessages && showChatMessages.length > 0 && (
            <div>
              {showChatMessages.map((message) => (
                <Row className="rowEachMessage">
                  {message.userSenderId === user.userId ? (
                    <Col className="message friendMessage">
                      <p>
                        {message.message} <br />
                        <span id="messageDate">
                          {new Date(message.creationTime)
                            .toLocaleString()
                            .split(",")[1]
                            .slice(0, 6)}
                          {"     "}
                          {
                            new Date(message.creationTime)
                              .toLocaleString()
                              .split(",")[0]
                          }
                        </span>
                      </p>
                    </Col>
                  ) : (
                    <Col className="message myMessage">
                      <p>
                        {message.message} <br />
                        <span id="messageDate">
                          {new Date(message.creationTime)
                            .toLocaleString()
                            .split(",")[1]
                            .slice(0, 6)}
                          {"     "}
                          {
                            new Date(message.creationTime)
                              .toLocaleString()
                              .split(",")[0]
                          }
                        </span>
                      </p>
                    </Col>
                  )}
                </Row>
              ))}
            </div>
          )}
        </Container>
      </Container>
      <Container>
        <Row id="chatBoxInput">
          {project.statusInt === 5 || project.statusInt === 6 ? (
            <Form.Control
              id="messageInput"
              type="text"
              /*    placeholder={intl.formatMessage({
              id: "newChatMessage.placeholder",
              defaultMessage: "Escreva a sua mensagem",
            })} */
              placeholder="Chat inactivo"
              name="messageInput"
              disabled
              defaultValue={""}
              //  onChange={handleChange}
              /*  onKeyPress={(event) => {
                if (event.key === "Enter") {
                  handleClick(event);
                }
              }} */
            />
          ) : (
            <Form.Control
              id="messageInput"
              type="text"
              /*    placeholder={intl.formatMessage({
              id: "newChatMessage.placeholder",
              defaultMessage: "Escreva a sua mensagem",
            })} */
              placeholder="Escreva a sua mensagem"
              name="messageInput"
              defaultValue={""}
              onChange={handleChange}
              onKeyPress={(event) => {
                if (event.key === "Enter") {
                  handleClick(event);
                }
              }}
            />
          )}
        </Row>
      </Container>
    </>
  );
}

export default ProjectChat;
