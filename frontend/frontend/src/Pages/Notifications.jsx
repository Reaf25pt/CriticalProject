import { useEffect } from "react";
import { useState } from "react";
import { userStore } from "../stores/UserStore";
import {
  BsXLg,
  BsCheck2Circle,
  BsEnvelopeFill,
  BsTrash,
  BsEnvelopeOpenFill,
} from "react-icons/bs";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { notificationStore } from "../stores/NotificationStore";
import { toast, Toaster } from "react-hot-toast";

function Notifications() {
  const user = userStore((state) => state.user);
  const setOwnProj = userStore((state) => state.setOwnProj);

  const updateNotifications = notificationStore(
    (state) => state.updateNotifications
  );
  const addNotifications = notificationStore((state) => state.addNotifications);
  const notifications = notificationStore((state) => state.notifications);
  const filterNotification = notificationStore(
    (state) => state.filterNotification
  );

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
  };

  function handleRemove(id) {
    fetch(
      `http://localhost:8080/projetofinal/rest/communication/notification/${id}`,
      {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
        },
      }
    )
      .then((resp) => resp.json())
      .then((data) => {
        updateNotifications(data);
      })
      .catch((err) => console.log(err));
  }

  function handleRead(id) {
    fetch(
      `http://localhost:8080/projetofinal/rest/communication/notification/${id}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
        },
      }
    )
      .then((resp) => resp.json())
      .then((data) => {
        updateNotifications(data);
        // filterNotification(data);
      })
      .catch((err) => console.log(err));
  }

  function handleInvitation(status, notifId) {
    // event.preventDefault();

    fetch(
      `http://localhost:8080/projetofinal/rest/communication/invitation/${notifId}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
          answer: status,
        },
      }
    )
      .then((response) => {
        if (response.status === 200) {
          return response.json();

          //navigate("/home", { replace: true });
        } else {
          alert("Pedido não satisfeito");
        }
      })
      .then((response) => {
        updateNotifications(response);
        handleActiveProject();
        toast.success("Convite respondido");
        //filterNotification(response);
      });
  }

  function handleActiveProject() {
    fetch(`http://localhost:8080/projetofinal/rest/user/activeproject`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setOwnProj(data);
      })
      .catch((err) => console.log(err));
  }

  return (
    <div>
      <Toaster />
      <ul className="nav nav-tabs" id="myTab" role="tablist">
        <li className="nav-item" role="presentation">
          <button
            className="nav-link active"
            id="home-tab"
            data-bs-toggle="tab"
            data-bs-target="#home"
            type="button"
            role="tab"
            aria-controls="home"
            aria-selected="true"
            style={{ background: "#C01722", color: "white" }}
          >
            Notificações
          </button>
        </li>
      </ul>
      <div className="tab-content" id="myTabContent">
        <div
          className="tab-pane fade show active"
          id="home"
          role="tabpanel"
          aria-labelledby="home-tab"
        >
          {notifications && notifications.length > 0 ? (
            <div
              className="notifications-container"
              style={{
                maxHeight: "750px",
                marginTop: "50px",
                overflowY: "auto",
              }}
            >
              <div className="row mx-auto col-10 col-md-8 col-lg-6 mt-5">
                {notifications.map((item) => (
                  <div class="card bg-light card border-primary mb-3">
                    <div class="card-body ">
                      <div class="card-title d-flex justify-content-between">
                        <h5>{convertTimestampToDate(item.creationTime)}</h5>
                        <div>
                          <OverlayTrigger
                            placement="top"
                            overlay={<Tooltip>Apagar</Tooltip>}
                          >
                            <span
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                            >
                              {" "}
                              <BsTrash
                                onClick={() => handleRemove(item.id)}
                                size={25}
                              />
                            </span>
                          </OverlayTrigger>

                          {item.seen /*  <BsEnvelopeFill size={25} /> */ ? null : (
                            <OverlayTrigger
                              placement="top"
                              overlay={<Tooltip>Marcar como lida</Tooltip>}
                            >
                              <span
                                data-bs-toggle="tooltip"
                                data-bs-placement="top"
                              >
                                {" "}
                                <BsEnvelopeFill
                                  size={25}
                                  onClick={() => handleRead(item.id)}
                                />
                              </span>
                            </OverlayTrigger>

                            /*    <Tooltip placement="top" title="Read">
                            <BsEnvelopeOpenFill
                              size={25}
                              onClick={() => handleRead(item.id)}
                            />
                          </Tooltip> */
                          )}
                        </div>
                      </div>

                      <hr />
                      <div className="row">
                        <div className="col-lg-10">
                          {item.seen ? (
                            <p class="card-text">{item.message}</p>
                          ) : (
                            <p
                              class="card-text"
                              style={{ fontWeight: "bolder" }}
                            >
                              {item.message}
                            </p>
                          )}
                        </div>
                        {item.needsInput ? (
                          <div className="col-lg-2 d-flex justify-content-around">
                            <OverlayTrigger
                              placement="top"
                              overlay={<Tooltip>Aceitar</Tooltip>}
                            >
                              <span
                                data-bs-toggle="tooltip"
                                data-bs-placement="top"
                              >
                                {" "}
                                <BsCheck2Circle
                                  color="green"
                                  size={40}
                                  onClick={() => handleInvitation(1, item.id)}
                                />
                              </span>
                            </OverlayTrigger>
                            <OverlayTrigger
                              placement="top"
                              overlay={<Tooltip>Recusar</Tooltip>}
                            >
                              <span
                                data-bs-toggle="tooltip"
                                data-bs-placement="top"
                              >
                                {" "}
                                <BsXLg
                                  color="red"
                                  size={40}
                                  onClick={() => handleInvitation(0, item.id)}
                                />
                              </span>
                            </OverlayTrigger>
                          </div>
                        ) : null}
                      </div>
                    </div>
                  </div>
                ))}
              </div>{" "}
            </div>
          ) : (
            <p class="card-text">Não tem notificações</p>
          )}
        </div>{" "}
      </div>
    </div>
  );
}

export default Notifications;
