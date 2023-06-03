import { Image } from "react-bootstrap";
import { BsFillChatLeftTextFill, BsFillEnvelopeFill } from "react-icons/bs";
import style from "./headercomponent.module.css";
import Logout from "./Logout";

function HeaderComponent() {
  return (
    <div
      className="d-flex justify-content-end align-items-center bg-light
    "
    >
      <div className={style.buttons}>
        <BsFillChatLeftTextFill className={style.icon} />
        <BsFillEnvelopeFill className={style.icon} />
      </div>
      <div>
        <p>Nome</p>
        <p>Alcunha</p>
      </div>
      <Image
        className={style.image}
        src="https://randomuser.me/api/portraits/men/72.jpg"
        roundedCircle
      />
      <Logout />
    </div>
  );
}

export default HeaderComponent;
