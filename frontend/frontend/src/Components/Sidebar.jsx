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

import Logout from "./Logout";
import { useState, useEffect } from "react";

function Sidebar() {
  const user = userStore((state) => state.user);
  const updateNotifications = notificationStore(
    (state) => state.updateNotifications
  );
  const fullName = user.firstName + " " + user.lastName;
  const addNotifications = notificationStore((state) => state.addNotifications);
  const notifications = notificationStore((state) => state.notifications);

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
  }, []);

  useEffect(() => {
    const ws = new WebSocket(
      "ws://localhost:8080/projetofinal/websocket/notifier/" + user.token
    );

    ws.onopen = () => {
      console.log("connected chat socket");
    };

    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);

      addNotifications(data);

      /* setBadgeValue(
        notifications.filter((notification) => !notification.read).length
      ); */
    };
    return () => {
      ws.close();
    };
  }, []);

  // var unreadNotif = notifications.filter((notif) => !notif.seen);

  return (
    <div className="container-fluid ">
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
                <Link to={"/home/chat"} className="nav-link align-middle px-0">
                  <BsWechat color="white" />
                  <span className="ms-1 d-none d-sm-inline text-white">
                    Mensagens
                  </span>
                </Link>
                <Link
                  to={"/home/notifications"}
                  className="nav-link align-middle px-0"
                >
                  <BsEnvelopeFill color="white" />
                  {notifications.length > 0 ? (
                    <span class="badge badge-dark">
                      {notifications.filter((notif) => !notif.seen).length}
                    </span>
                  ) : null}

                  {/*    {notifications.length > 0 ? (
                    <span class="badge badge-dark">{unreadNotif.length}</span>
                  ) : null} */}

                  <span className="ms-1 d-none d-sm-inline text-white">
                    Notificações
                    {/*  <span class="badge badge-light">5</span> */}
                  </span>
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
                  src="https://t3.ftcdn.net/jpg/00/36/94/26/360_F_36942622_9SUXpSuE5JlfxLFKB1jHu5Z07eVIWQ2W.jpg"
                  alt="avatar"
                  width="30"
                  height="30"
                  class="rounded-circle"
                />
              ) : user.photo === "" ? (
                <img
                  src="https://t3.ftcdn.net/jpg/00/36/94/26/360_F_36942622_9SUXpSuE5JlfxLFKB1jHu5Z07eVIWQ2W.jpg"
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
              {/*  <img
               
                src="https://github.com/mdo.png"
                alt="hugenerd"
                width="30"
                height="30"
                class="rounded-circle"
              /> */}
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
