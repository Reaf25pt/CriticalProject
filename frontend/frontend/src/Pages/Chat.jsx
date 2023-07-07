import React from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
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

  const [selectedContact, setSelectedContact] = useState(null);

  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const userId = queryParams.get("userId");
  console.log(userId);
  console.log(typeof userId);

  const idToChat = parseInt(userId);
  console.log(idToChat);
  console.log(typeof idToChat);

  useEffect(() => {
    fetch(
      `http://localhost:8080/projetofinal/rest/communication/contacts?idToChat=${idToChat}`,
      {
        method: "GET",
        headers: {
          Accept: "*/*",
          "Content-Type": "application/json",
          token: user.token,
        },
      }
    )
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

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      // console.log({ ...values });   // spread notation - faz spread ao objecto
      return { ...values, [name]: value };
    });
  };

  const setUserToChat = (user) => {
    setSelectedUser(user);
    console.log("selectuser");
    console.log(selectedUser);
  };

  return (
    <div className="container-fluid ">
      <div className="row d-flex ">
        <div className="col-lg-3 bg-secondary rounded-3 h-100">
          <div className="row ">
            <h3 className="text-center  text-white mt-3 mb-3">Contactos</h3>
            <hr />
          </div>

          <div>
            {contacts.map((user) => (
              <div
                className="row d-flex justify-content-between mb-3 p-2 rounded-5 align-items-center  mx-auto"
                key={user.id}
               onClick={() => setUserToChat(user)}
               /*   style={{
                  background: selectedUser.id === user.id ? "gray" : "white",
                }}    */
              >
                {user.openProfile && user.photo ? (
                  <div className="col-lg-2">
                    {" "}
                    <img
                      src={user.photo}
                      width={35}
                      height={35}
                      className="rounded-5"
                      alt=""
                    />
                  </div>
                ) : (
                  <div className="col-2">
                    <img
                      /*                     id="imageUserToChat"
                       */ alt=""
                      /*                     xs={{ span: 6, offset: 3 }}
                       */ src="https://static-00.iconduck.com/assets.00/user-avatar-icon-512x512-vufpcmdn.png"
                      width={35}
                      height={35}
                      /*  style={{
                      resizeMode: "contain",
                      maxHeight: 60,
                      maxWidth: 80,
                      
                    }} */
                    />
                  </div>
                )}

                <div className="col-lg-6 text-black">
                  {user.firstName} {""}
                  {user.lastName}{" "}
                </div>
                {messages.filter(
                  (message) => !message.seen && message.userSenderId === user.id
                ).length > 0 ? (
                  <span
                    className="col-lg-2 text-danger "
                    style={{ fontSize: "20px" }} /* id="badgeMsgEachUser" */
                  >
                    {
                      messages.filter(
                        (message) =>
                          !message.seen && message.userSenderId === user.id
                      ).length
                    }
                  </span>
                ) : null}
              </div>
            ))}
          </div>
        </div>
        <div className="col-lg-6">
          {selectedUser !== null ? (
            <ContactChat selectedUser={selectedUser} />
          ) : (
            <Container /* id="messageForUnexistentUser" */>
              <div> Seleccione uma caixa de mensagens</div>
              {/*  <FormattedMessage
                id="noChatSelected.text"
                defaultMessage="  Seleccione uma caixa de mensagens"
              /> */}
            </Container>
          )}
        </div>
      </div>
    </div>
  );
}

export default Chat;
