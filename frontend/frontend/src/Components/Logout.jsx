import { BsBoxArrowLeft } from "react-icons/bs";
import { Link } from "react-router-dom";
import style from "./linkimagecomponent.module.css";

function Logout() {
  return (
    <Link to="/">
      <BsBoxArrowLeft className={style.linkimagecomponent} />
    </Link>
  );
}

export default Logout;
