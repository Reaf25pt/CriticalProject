import { useEffect } from "react";
import { useState } from "react";
import { userStore } from "../stores/UserStore";
import { BsXLg, BsCheck2Circle } from "react-icons/bs";

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
          <div className="row mx-auto col-10 col-md-8 col-lg-6 mt-5">
            <div class="card bg-light">
              <div class="card-body">
                <h5 class="card-title ">Card title</h5>
                <hr />
                <div className="row">
                  <div className="col-lg-10">
                    <p class="card-text">
                      Some quick example text to build on the card title and
                      make up the bulk of the card's content.
                    </p>
                  </div>
                  <div className="col-lg-2 d-flex justify-content-around">
                    <BsCheck2Circle color="green" size={40} />
                    <BsXLg color="red" size={40} />
                  </div>
                </div>
              </div>
            </div>{" "}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Notifications;
