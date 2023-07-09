import React from "react";
import { useParams } from "react-router-dom";

import { useState, useEffect, useRef } from "react";

import { userStore } from "../stores/UserStore";
import "react-chat-elements/dist/main.css";
import { Button, MessageBox } from "react-chat-elements";

import { Input } from "react-chat-elements";
import InputComponent from "./InputComponent";

function ProjectChat({ project }) {
  const [inputValue, setInputValue] = useState("");

  const [messages, setMessages] = useState([]);
  // const [showMessages, setShowMessages] = useState([]);

  const addMessage = (newMessage) => {
    setMessages((prevMessages) => [...prevMessages, newMessage]);
  };

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

  useEffect(() => {
    const ws = new WebSocket(
      "ws://localhost:8080/projetofinal/websocket/projectchat/" + user.token
    );

    ws.onopen = () => {
      console.log("connected projectChat socket");
    };

    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);

      addMessage(data);
    };

    return () => {
      ws.close();
    };
  }, []);

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
    <div className="container-fluid">
      <div
        className="row overflow-auto d-flex flex-column-reverse m-0  "
        style={{ height: "80vh" }}
      >
        <div
          className="col-lg-6 bg-secondary rounded-4 mx-auto p-1 mt-5"
          style={{ minHeight: "80vh" }}
        >
          {messages.length !== 0 ? (
            <div
              style={{
                maxHeight: "500px",
                marginTop: "0px",
                overflowY: "auto",
              }}
            >
              {messages
                .sort((a, b) => a.id - b.id)
                .map((message) => (
                  <div key={message.id}>
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
            <div>
              <h5 className="text-white">
                Inicie conversa com os restantes membros do projecto
              </h5>
            </div>
          )}
        </div>
      </div>
      <div className="row">
        <div className="col-lg-6 mx-auto">
          {project.statusInt === 5 || project.statusInt === 6 ? (
            <InputComponent
              placeholder="Chat desactivado"
              className="bg-dark  p-4 rounded-5 "
              value={inputValue}
              disabled
              // onChange={(event) => setInputValue(event.target.value)}
              rightButtons={
                <Button
                  text="Enviar"
                  className="bg-secondary text-white"
                  // onClick={handleSendMessage}
                  defaultValue={""}
                />
              }
            />
          ) : (
            <Input
              placeholder="Escreva a sua mensagem"
              className="bg-dark  p-2 rounded-5 "
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
          )}
        </div>
      </div>
    </div>
  );
}

export default ProjectChat;
