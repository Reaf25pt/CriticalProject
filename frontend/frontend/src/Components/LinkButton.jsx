import { Link } from "react-router-dom";
import styles from "./linkbutton.module.css";
function LinkButton(props) {
  return (
    <>
      <Link className={styles.linkbutton} to={props.to}>
        {props.name}
      </Link>
    </>
  );
}
export default LinkButton;
