import { useEffect } from "react";
import { useState } from "react";
import { userStore } from "../stores/UserStore";

function Notifications() {
  const user = userStore((state) => state.user);

  const [showAllNotifications, setShowAllNotifications] = useState([]);
  const [notification, setNotification] = useState([]);
  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/project/allprojects`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        console.log(data);
        setShowAllNotifications(data);
      })
      .catch((err) => console.log(err));
  }, [notification]);
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
          <div className="row mx-auto col-10 col-md-8 col-lg-6">
            {/* {showAllNotifications.map((notification)=> (
                          <div className="bg-secondary">{notification.}</div>


            ))} */}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Notifications;
