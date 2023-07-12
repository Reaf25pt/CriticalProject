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

  const idToChat = parseInt(userId);

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
      });
  }, []);

  useEffect(() => {
    if (selectedUser !== prevSelectedUser.current) {
      prevSelectedUser.current = selectedUser;
    }
  }, [selectedUser]);

  const handleChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;

    setCredentials((values) => {
      return { ...values, [name]: value };
    });
  };

  const setUserToChat = (user) => {
    setSelectedUser(user);
  };

  return (
    <div className="container-fluid ">
      <div className="row d-flex ">
        <div
          className="col-lg-3 bg-secondary rounded-3 overflow-auto  m-0"
          style={{ height: "100vh" }}
        >
          <div className="row">
            <h3 className="text-center  text-white mt-3 mb-3">Contactos</h3>
            <hr />
          </div>

          <div>
            {contacts.map((user) => (
              <div
                className="row d-flex justify-content-between mb-3 p-2 rounded-5 align-items-center  mx-auto"
                key={user.id}
                onClick={() => setUserToChat(user)}
                /* style={{
                  background: selectedUser.id === user.id ? "gray" : "white",
                }} */
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
                      alt=""
                      src="https://static-00.iconduck.com/assets.00/user-avatar-icon-512x512-vufpcmdn.png"
                      width={35}
                      height={35}
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
                    style={{ fontSize: "20px" }}
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
            <Container>
              <div> Seleccione uma caixa de mensagens</div>
            </Container>
          )}
        </div>
      </div>
    </div>
  );
}

export default Chat;
