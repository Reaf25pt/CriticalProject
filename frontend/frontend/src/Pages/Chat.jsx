import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";

import { useState, useEffect, useRef } from "react";
import Form from "react-bootstrap/Form";
import "../Components/Chat.css";
import { userStore } from "../stores/UserStore";
import { messageStore } from "../stores/MessageStore";
import ContactChat from "../Components/ContactChat";

function Chat() {
  const [contacts, setContacts] = useState([]);
  const [showList, setShowList] = useState([]);
  const [credentials, setCredentials] = useState({});
  const [selectedUser, setSelectedUser] = useState(null);
  const prevSelectedUser = useRef(null);
  const messages = messageStore((state) => state.messages);

  const user = userStore((state) => state.user);

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/communication/contacts`, {
      method: "GET",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((response) => response.json())
      .then((response) => {
        setContacts(response);
        console.log(response);
        // console.log(messages);
      });
  }, []);

  useEffect(() => {
    if (selectedUser !== prevSelectedUser.current) {
      console.log(selectedUser);
      console.log(prevSelectedUser);
      prevSelectedUser.current = selectedUser;
    }
  }, [selectedUser]);

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
    console.log(user);
    console.log(user.data);
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

            {contacts.map((user) => (
              <Row
                className="rowEachUserChat"
                onClick={() => setUserToChat(user)}
              >
                {user.openProfile && user.photo ? (
                  <Col>
                    {" "}
                    <img
                      src={user.photo}
                      width={35}
                      height={35}
                      className="rounded-5"
                      alt=""
                    />
                  </Col>
                ) : (
                  <Col>
                    <img
                      id="imageUserToChat"
                      alt=""
                      xs={{ span: 6, offset: 3 }}
                      src="https://static-00.iconduck.com/assets.00/user-avatar-icon-512x512-vufpcmdn.png"
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
                        !message.seen && message.userSenderId === user.id
                    ).length
                  }
                </Col>
              </Row>
            ))}
          </Container>
        </Container>
        <Container id="rightSide">
          {selectedUser ? (
            <ContactChat selectedUser={selectedUser} />
          ) : (
            <Container id="messageForUnexistentUser">
              <div> Seleccione uma caixa de mensagens</div>
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
