import React from "react";
import { useParams } from "react-router-dom";

import { useState, useEffect } from "react";

import { userStore } from "../stores/UserStore";
import "react-chat-elements/dist/main.css";
import { Button, MessageBox } from "react-chat-elements";

import { Input } from "react-chat-elements";

function ProjectChat({ project }) {
  const [inputValue, setInputValue] = useState("");

  const [messages, setMessages] = useState([]);
  const [showMessages, setShowMessages] = useState([]);

  const user = userStore((state) => state.user);

  const { id } = useParams(); // id do projecto

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
        setMessages(response);
      })
      .catch((err) => console.log(err));
  }, [inputValue]);

  const handleSendMessage = (event) => {
    event.preventDefault();

    if (inputValue !== "") {
      const newMessage = {
        message: inputValue,
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
          setInputValue("");
        });
    }
  };

  return (
    <div className="container">
      <div className="row overflow-auto" style={{ maxHeight: "100vh" }}>
        <div className="col-lg-6 bg-secondary rounded-4 mx-auto p-5 mt-5">
          {messages.length !== 0 ? (
            <div>
              {messages.map((message, index) => (
                <div key={index}>
                  {message.userSenderId === user.userId ? (
                    <MessageBox
                      position={"right"}
                      type="text"
                      title={
                        message.userSenderFirstName +
                        " " +
                        message.userSenderLastName
                      }
                      date={message.creationTime}
                      text={message.message}
                    />
                  ) : (
                    <MessageBox
                      position={"left"}
                      type="text"
                      title={
                        message.userSenderFirstName +
                        " " +
                        message.userSenderLastName
                      }
                      date={message.creationTime}
                      text={message.message}
                    />
                  )}
                </div>
              ))}
            </div>
          ) : (
            <p>Inicie conversa com os restantes membros do projecto</p>
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
                  onClick={handleSendMessage}
                  defaultValue={""}
                />
              }
            />{" "}
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProjectChat;
