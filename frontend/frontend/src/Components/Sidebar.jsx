import { Link, Outlet } from "react-router-dom";
import {
  BsFillHouseFill,
  BsClipboardFill,
  BsTrophyFill,
  BsWechat,
  BsEnvelopeFill,
} from "react-icons/bs";
import { userStore } from "../stores/UserStore";
import { notificationStore } from "../stores/NotificationStore";
import { messageStore } from "../stores/MessageStore";

import Logout from "./Logout";
import { useState, useEffect } from "react";
import { toast, Toaster } from "react-hot-toast";

function Sidebar() {
  const user = userStore((state) => state.user);
  const fullName = user.firstName + " " + user.lastName;
  const notifications = notificationStore((state) => state.notifications);
  const updateNotifications = notificationStore(
    (state) => state.updateNotifications
  );
  const addNotifications = notificationStore((state) => state.addNotification);

  const messages = messageStore((state) => state.messages);
  const updateMessages = messageStore((state) => state.updateMessages);
  const addMessages = messageStore((state) => state.addMessages);

  useEffect(() => {
    fetch(
      `http://localhost:8080/projetofinal/rest/communication/notifications`,
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
        updateNotifications(response);
        // console.log(messages);
      });

    fetch(`http://localhost:8080/projetofinal/rest/communication/messages`, {
      method: "GET",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((response) => response.json())
      .then((response) => {
        updateMessages(response);
      });
  }, []);

  useEffect(() => {
    const ws = new WebSocket(
      "ws://localhost:8080/projetofinal/websocket/notifier/" + user.token
    );
    const wsM = new WebSocket(
      "ws://localhost:8080/projetofinal/websocket/personalchat/" + user.token
    );

    ws.onopen = () => {
      console.log("connected notif socket");
    };

    wsM.onopen = () => {
      console.log("connected chat socket");
    };

    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);

      addNotifications(data);
    };
    wsM.onmessage = (event) => {
      const data = JSON.parse(event.data);

      addMessages(data);
    };

    return () => {
      ws.close();
      wsM.close();
    };
  }, []);

  // var unreadNotif = notifications.filter((notif) => !notif.seen);

  return (
    <div className="container-fluid ">
      <Toaster />
      <div className="row flex-nowrap min-vh-100">
        <div
          className="col-1 col-md-auto col-xl-2 px-sm-2 px-0 "
          style={{ background: "#C01722" }}
        >
          <div className="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2 text-white  ">
            <span className="fs-5 d-none d-sm-inline  ">
              Laboratório de Inovação
              <hr className="hr" />
            </span>

            <ul
              className="nav nav-pills flex-column mb-sm-auto  align-items-center align-items-sm-start"
              id="menu"
            >
              <li className="nav-item ">
                <Link to={"/home/start"} className="nav-link align-middle px-0">
                  <BsFillHouseFill color="white" />{" "}
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Início
                  </span>
                </Link>
              </li>
              <li className="nav-item">
                <Link
                  to={"/home/projects"}
                  className="nav-link align-middle px-0"
                >
                  <BsClipboardFill color="white" />
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Projetos
                  </span>
                </Link>
              </li>
              <li className="nav-item">
                <Link
                  to={"/home/contests"}
                  className="nav-link align-middle px-0"
                >
                  <BsTrophyFill color="white" />
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Concursos
                  </span>
                </Link>
                <Link
                  /* to={"/home/chat"} */ to={`/home/chat?userId=${0}`}
                  className="nav-link align-middle px-0"
                >
                  <BsWechat color="white" />
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Mensagens
                  </span>

                  {messages.length > 0 ? (
                    <span className="badge badge bg-dark">
                      {
                        messages.filter(
                          (message) =>
                            !message.seen &&
                            message.userReceiverId === user.userId
                        ).length
                      }
                    </span>
                  ) : null}
                </Link>
                <Link
                  to={"/home/notifications"}
                  className="nav-link align-middle px-0"
                >
                  <BsEnvelopeFill color="white" />

                  {/*    {notifications.length > 0 ? (
                    <span class="badge badge-dark">{unreadNotif.length}</span>
                  ) : null} */}

                  <span className="ms-1 d-none d-sm-inline text-white">
                    Notificações
                    {/*  <span class="badge badge-light">5</span> */}
                  </span>
                  {notifications.length > 0 ? (
                    <span class="badge badge bg-dark">
                      {notifications.filter((notif) => !notif.seen).length}
                    </span>
                  ) : null}
                </Link>
              </li>
            </ul>
          </div>
          <hr style={{ borderColor: "white" }} />
          <div class="dropdown pb-4">
            <a
              href="#"
              class="d-flex align-items-center text-white text-decoration-none dropdown-toggle"
              id="dropdownUser1"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              {" "}
              {user.photo === null ? (
                <img
                  src="https://static-00.iconduck.com/assets.00/user-avatar-icon-512x512-vufpcmdn.png"
                  alt="avatar"
                  width="30"
                  height="30"
                  class="rounded-circle"
                />
              ) : user.photo === "" ? (
                <img
                  src="https://static-00.iconduck.com/assets.00/user-avatar-icon-512x512-vufpcmdn.png"
                  alt="avatar"
                  width="30"
                  height="30"
                  class="rounded-circle"
                />
              ) : (
                <img
                  src={user.photo}
                  width="30"
                  height="30"
                  class="rounded-circle"
                  alt=""
                />
              )}
              <span class="d-none d-sm-inline mx-1">{fullName}</span>
            </a>
            <ul
              class="dropdown-menu dropdown-menu-dark text-small shadow"
              aria-labelledby="dropdownUser1"
            >
              <li>
                <Link class="dropdown-item" to={"/home/profile"}>
                  Perfil
                </Link>
              </li>
              <li>
                <hr class="dropdown-divider" />
              </li>
              <li>
                <Logout />
                {/*   <Link class="dropdown-item" to={"/"}>
                  Logout
                </Link> */}
              </li>
            </ul>
          </div>
        </div>

        <div className="col py-3 bg-dark">
          <Outlet />{" "}
        </div>
      </div>
    </div>
  );
}

export default Sidebar;
