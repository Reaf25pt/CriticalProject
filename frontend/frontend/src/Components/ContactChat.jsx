import React from "react";
import { useParams } from "react-router-dom";

import { useState, useEffect } from "react";

import { userStore } from "../stores/UserStore";
import "react-chat-elements/dist/main.css";
import { Button, MessageBox } from "react-chat-elements";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { Input } from "react-chat-elements";
import InputComponent from "./InputComponent";
import { messageStore } from "../stores/MessageStore";

function ContactChat({ selectedUser }) {
  const [inputValue, setInputValue] = useState("");

  const messages = messageStore((state) => state.messages);
  const updateMessages = messageStore((state) => state.updateMessages);
  const addMessages = messageStore((state) => state.addMessages);
  //const [contactMessages, setContactMessages] = useState([]);
  /* const addMessage = (newMessage) => {
    setContactMessages((prevMessages) => [...prevMessages, newMessage]);
  };*/

  const user = userStore((state) => state.user);

  const contactMessages = messages.filter(
    (message) =>
      message.userSenderId === selectedUser.id ||
      message.userReceiverId === selectedUser.id
  );

  useEffect(() => {
    fetch("http://localhost:8080/projetofinal/rest/communication/messages", {
      method: "PATCH",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
        contactId: selectedUser.id,
      },
    })
      .then((response) => response.json())
      .then((response) => {
        updateMessages(response);
      });
  }, [selectedUser]);

  const handleSendMessage = (event) => {
    event.preventDefault();

    if (inputValue !== "") {
      const newMessage = {
        message: inputValue,
        userReceiverId: selectedUser.id,
      };

      fetch(`http://localhost:8080/projetofinal/rest/communication/message`, {
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
          addMessages(response);
          setInputValue("");
        });
    }
  };

  return (
    <div className="container-fluid ">
      <div
        className="row overflow-auto d-flex flex-column-reverse m-0  "
        style={{ height: "80vh" }}
      >
        <div
          className="col-lg-12 bg-secondary rounded-4  p-5 "
          style={{ minHeight: "80vh" }}
        >
          {contactMessages.length !== 0 ? (
            <div
              style={{
                maxHeight: "500px",
                marginTop: "20px",
                overflowY: "auto",
              }}
            >
              {contactMessages
                .sort((a, b) => a.id - b.id)
                .map((message) => (
                  <div key={message.id}>
                    {message.userSenderId === user.userId ? (
                      <MessageBox
                        position={"right"}
                        type="text"
                        date={message.creationTime}
                        text={message.message}
                      />
                    ) : message.userSenderId === selectedUser.id ? (
                      <MessageBox
                        position={"left"}
                        type="text"
                        date={message.creationTime}
                        text={message.message}
                      />
                    ) : null}
                  </div>
                ))}
            </div>
          ) : (
            <p>Inicie conversa com {selectedUser.firstName}</p>
          )}
        </div>
      </div>

      <div className="row m-0">
        <hr />
        <Input
          placeholder="Escreva a sua mensagem"
          className="bg-secondary  p-2 rounded-5 "
          value={inputValue}
          onChange={(event) => setInputValue(event.target.value)}
          rightButtons={
            <Button
              text="Enviar"
              className="bg-black text-white"
              onClick={handleSendMessage}
              defaultValue={""}
            />
          }
        />
      </div>
    </div>
  );
}

export default ContactChat;
