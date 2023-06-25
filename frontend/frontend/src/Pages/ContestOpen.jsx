import { useEffect, useState } from "react";
import ContestComponent from "../Components/ContestComponent";
import TextAreaComponent from "../Components/TextAreaComponent";
import EditContestComponent from "../Components/EditContestComponent";
import { useParams } from "react-router-dom";
import { userStore } from "../stores/UserStore";
import { contestOpenStore } from "../stores/ContestOpenStore";

function ContestOpen() {
  const [showComponentA, setShowComponentA] = useState(true);
  const user = userStore((state) => state.user);
  const setContestOpen = contestOpenStore((state) => state.setContestOpen);
  const contest = contestOpenStore((state) => state.contest);

  const toggleComponent = () => {
    setShowComponentA(!showComponentA);
  };

  const { id } = useParams();
  console.log(id);

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/contest/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setContestOpen(data);
        // console.log(data);
        // setShowProjects(data);
        // console.log(showProjects);
      })
      .catch((err) => console.log(err));
  }, []);

  if (!contest) {
    return <div>Loading...</div>;
  }

  return (
    <div>
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
              Dados
            </button>
          </li>
          <li className="nav-item" role="presentation">
            <button
              className="nav-link"
              id="profile-tab"
              data-bs-toggle="tab"
              data-bs-target="#profile"
              type="button"
              role="tab"
              aria-controls="profile"
              aria-selected="false"
              style={{ background: "#C01722", color: "white" }}
            >
              Projetos
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
            <div>
              {showComponentA ? (
                <ContestComponent toggleComponent={toggleComponent} />
              ) : (
                <EditContestComponent toggleComponent={toggleComponent} />
              )}
            </div>{" "}
          </div>
          <div className="tab-content" id="myTabContent">
            <div
              className="tab-pane fade"
              id="profile"
              role="tabpanel"
              aria-labelledby="profile-tab"
            >
              <h5 className="text-white">Projetos</h5>
            </div>
          </div>
        </div>
      </div>{" "}
    </div>
  );
}
export default ContestOpen;
