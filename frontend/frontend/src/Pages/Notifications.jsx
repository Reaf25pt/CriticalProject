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

function Notifications() {
  const user = userStore((state) => state.user);

  const [showAllNotifications, setShowAllNotifications] = useState([]);
  const [notification, setNotification] = useState([]);

  const convertTimestampToDate = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString(); // Adjust the format as per your requirement
  };

  useEffect(() => {
    fetch(
      `http://localhost:8080/projetofinal/rest/communication/notifications`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
        },
      }
    )
      .then((resp) => resp.json())
      .then((data) => {
        console.log(data);
        setShowAllNotifications(data);
      })
      .catch((err) => console.log(err));
  }, [notification]);

  function handleInvitation(status, notifId) {
    // event.preventDefault();

    console.log(status + " " + notifId);

    fetch(
      `http://localhost:8080/projetofinal/rest/communication/invitation/${notifId}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
          status: status,
        },
      }
    ).then((response) => {
      if (response.status === 200) {
        console.log(response);
        setNotification([]);
        alert("notif respondida");

        //navigate("/home", { replace: true });
      } else {
        alert("Algo correu mal. Tente novamente");
      }
    });
  }

  return (
    <div>
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
          {showAllNotifications && showAllNotifications.length > 0 ? (
            <div className="row mx-auto col-10 col-md-8 col-lg-6 mt-5">
              <div class="card bg-light">
                {showAllNotifications.map((item) => (
                  <div class="card-body">
                    <div class="card-title d-flex justify-content-between">
                      <h5>{convertTimestampToDate(item.creationTime)}</h5>
                      <div>
                        <BsTrash size={25} />
                        {item.seen === true ? (
                          <BsEnvelopeFill size={25} />
                        ) : (
                          <BsEnvelopeOpenFill size={25} />
                        )}
                      </div>
                    </div>

                    <hr />
                    <div className="row">
                      <div className="col-lg-10">
                        <p class="card-text">{item.message}</p>
                      </div>
                      {item.needsInput ? (
                        <div className="col-lg-2 d-flex justify-content-around">
                          <BsCheck2Circle
                            color="green"
                            size={40}
                            onClick={() => handleInvitation(1, item.id)}
                          />
                          <BsXLg
                            color="red"
                            size={40}
                            onClick={() => handleInvitation(0, item.id)}
                          />
                        </div>
                      ) : null}
                    </div>
                  </div>
                ))}
              </div>
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
