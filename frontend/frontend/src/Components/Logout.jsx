import { BsBoxArrowLeft } from "react-icons/bs";
import { Link } from "react-router-dom";
import style from "./linkimagecomponent.module.css";
import { useNavigate } from "react-router-dom";
import { userStore } from "../stores/UserStore";

function Logout() {
  const navigate = useNavigate();
  const user = userStore((state) => state.user);
  const clearLoggedUser = userStore((state) => state.clearLoggedUser);

  const handleLogout = (e) => {
    e.preventDefault();

    fetch("http://localhost:8080/projetofinal/rest/user/logout", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    }).then((response) => {
      if (response.status === 200) {
        navigate("/", { replace: true });
      } else {
        navigate("/", { replace: true });
      }
      clearLoggedUser();
      localStorage.clear();
      sessionStorage.clear();
    });
  };

  return (
    <Link class="dropdown-item" onClick={handleLogout}>
      Logout
      {/*       <BsBoxArrowLeft className={style.linkimagecomponent} /> */}
    </Link>
  );
}

export default Logout;
