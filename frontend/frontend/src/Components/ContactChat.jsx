import React from "react";
import { useParams } from "react-router-dom";

import { useState, useEffect } from "react";

import { userStore } from "../stores/UserStore";
import "react-chat-elements/dist/main.css";
import { Button, MessageBox } from "react-chat-elements";

import { Input } from "react-chat-elements";
import InputComponent from "./InputComponent";
import { messageStore } from "../stores/MessageStore";

function ContactChat({ selectedUser }) {
  const [inputValue, setInputValue] = useState("");

  const messages = messageStore((state) => state.messages);
  const updateMessages = messageStore((state) => state.updateMessages);
  const addMessages = messageStore((state) => state.addMessages);
  const [contactMessages, setContactMessages] = useState([]);
  /*   const addMessage = (newMessage) => {
    setMessages((prevMessages) => [...prevMessages, newMessage]);
  }; */

  const user = userStore((state) => state.user);

  useEffect(() => {
    fetch(
      `http://localhost:8080/projetofinal/rest/communication/messages/${selectedUser.id}`,
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
        // setlistChatUsers(response);
        setContactMessages(response);
        console.log(response);
        markMessagesAsRead();

        /*  const element = document.getElementById("chat");

        element.scrollIntoView({ behavior: "smooth" }); */
      });
  }, [selectedUser]);

  const markMessagesAsRead = () => {
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
        console.log(response);
        // console.log(messages);
      });
  };

  /*  const ws = new WebSocket(
      "ws://localhost:8080/projetofinal/websocket/projectchat/" + user.token
    );

    ws.onopen = () => {
      console.log("connected projectChat socket");
    };

    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);

      addMessages(data);
    };

    return () => {
      ws.close();
    };*/

  /*
  const handleSendMessage = (event) => {
    event.preventDefault();

    if (inputValue !== "") {
      const newMessage = {
        message: inputValue,
        userSenderId: user.userId,
      };

      fetch(`http://localhost:8080/projetofinal/rest/project/chat/`, {
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
          setInputValue("");
        });
    }
  };*/

  return (
    <div className="container">
      <div className="row overflow-auto" style={{ maxHeight: "100vh" }}>
        <div className="col-lg-6 bg-secondary rounded-4 mx-auto p-5 mt-5">
          {contactMessages.length !== 0 ? (
            <div>
              {contactMessages.map((message, index) => (
                <div key={index}>
                  {message.userSenderId === user.userId ? (
                    <MessageBox
                      position={"right"}
                      type="text"
                      date={message.creationTime}
                      text={message.message}
                    />
                  ) : message.userReceiverId === selectedUser.id ? (
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
            <p></p>
          )}
          <div>
            <hr />
            <Input
              placeholder="Escreva a sua mensagem"
              className="bg-dark  p-4 rounded-5 "
              value={inputValue}
              onChange={(event) => setInputValue(event.target.value)}
              rightButtons={
                <Button
                  text="Enviar"
                  className="bg-secondary text-white"
                  /*    onClick={handleSendMessage} */
                  defaultValue={""}
                />
              }
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default ContactChat;