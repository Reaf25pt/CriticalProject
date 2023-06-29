import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";

import { useState, useEffect } from "react";
import Form from "react-bootstrap/Form";
import "../Components/Chat.css";

function Chat() {
  const [listChatUsers, setlistChatUsers] = useState([]);
  const [showList, setShowList] = useState([]);
  const [credentials, setCredentials] = useState({});
  const [selectedUser, setSelectedUser] = useState(null);
  const [messages, setMessages] = useState([]);

  // const [userToChat, setUserToChat] = useState(null);
  // const messages = messageStore((state) => state.messages);

  // const updateMessages = messageStore((state) => state.updateMessages);
  // const addMessage = messageStore((state) => state.addMessage);
  /*
  useEffect(() => {
    fetch(
      "http://localhost:8080/joana-proj4/rest/todo_app/users/listchatusers",
      {
        method: "GET",
        headers: {
          Accept: "**",
          "Content-Type": "application/json",
          token: token,
        },
      }
    )
      .then((response) => response.json())
      .then((response) => {
        setlistChatUsers(response);
      });
  }, []);

  /* useEffect(() => {
    fetch(
      "http://localhost:8080/joana-proj4/rest/todo_app/users/listallmessages",
      {
        method: "GET",
        headers: {
          Accept: "**",
          "Content-Type": "application/json",
          token: token,
        },
      }
    )
      .then((response) => response.json())
      .then((response) => {
        updateMessages(response);
        console.log(messages);
      });
  }, []);*/

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      // console.log({ ...values });   // spread notation - faz spread ao objecto
      return { ...values, [name]: value };
    });
  };

  /*   const selectChat = (event) => {
    console.log(JSON.stringify(event));
    setUserToChat(event);
    console.log(userToChat);
    console.log(userToChat.userId);
  }; */

  const setUserToChat = (user) => {
    /*  console.log(user);
    console.log(user.data); */
    setSelectedUser(user);
  };

  /*   const handleClick = (event) => {
    event.preventDefault();

    console.log(credentials.message);
    //   console.log(user.userId);

    const messageObj = {
      message: credentials.message,
      messageReceiverId: selectedUser.userId,
      /* message: "Olá",
      messageReceiverId: "2",*/
  /*    };

    fetch("http://localhost:8080/joana-proj4/rest/todo_app/newmessage", {
      method: "POST",
      headers: {
        Accept: "**",
        "Content-Type": "application/json",
        token: token,
      },
      body: JSON.stringify(messageObj),
    }).then((response) => {
      if (response.status === 200) {
        alert("Mensagem enviada");

        //  return response.json();
        /*   } else if (response.status === 403) {
          alert("Não tem autorização para efectuar este pedido");
        } else if (response.status === 404) {
          alert("Categoria não encontrada"); */
  /*  } else {
        alert("Algo correu mal");
      }
      document.getElementById("messageInput").value = "";
    }); */
  /*    .then((response) => {
        set((values) => [...values, response]);

        handleClose();
      }); */

  // console.log(selectedUser);

  /*   if (userToChat == null) {
    return <Container>Seleccione uma caixa de mensagens</Container>;
  } */

  return (
    <>
      <Container id="outerBoxMessage">
        <Container id="leftSide">
          <Container>
            <Row id="headerListChat">
              {/* <FormattedMessage
                id="chatContacts.label"
                defaultMessage="Contactos"
              /> */}
              Contactos
            </Row>

            {listChatUsers.map((user) => (
              <Row
                className="rowEachUserChat"
                onClick={() => setUserToChat(user)}
              >
                {user.photoUrl ? (
                  <Col>{user.photoUrl}</Col>
                ) : (
                  <Col>
                    <img
                      id="imageUserToChat"
                      alt=""
                      xs={{ span: 6, offset: 3 }}
                      src="https://smallbusinessify.com/wp-content/uploads/2018/09/what-can-time-management-skills-help-you-do.jpg"
                      style={{
                        resizeMode: "contain",
                        /*   maxHeight: 60,
                    maxWidth: 80, */
                      }}
                    />
                  </Col>
                )}

                <Col>
                  {user.firstName} {""}
                  {user.lastName}{" "}
                </Col>

                <Col xs={1} id="badgeMsgEachUser">
                  {
                    messages.filter(
                      (message) =>
                        !message.seen && message.userSenderId === user.userId
                    ).length
                  }
                </Col>
              </Row>
            ))}
          </Container>
        </Container>
        <Container id="rightSide">
          {selectedUser ? (
            <Chat user={selectedUser} />
          ) : (
            <Container id="messageForUnexistentUser">
              {/*  <FormattedMessage
                id="noChatSelected.text"
                defaultMessage="  Seleccione uma caixa de mensagens"
              /> */}
            </Container>
          )}
        </Container>
      </Container>
    </>
  );
}

export default Chat;
