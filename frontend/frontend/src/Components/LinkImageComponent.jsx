import { Link } from "react-router-dom";
import { BsReplyFill } from "react-icons/bs";
import style from "./linkimagecomponent.module.css";

function LinkImageComponent(props) {
  return (
    <div>
      <Link to={props.to}>
        <BsReplyFill className={style.linkimagecomponent} />
      </Link>
    </div>
  );
}

export default LinkImageComponent;
