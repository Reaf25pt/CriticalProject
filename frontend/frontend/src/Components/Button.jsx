import styles from "./Button.module.css";
function Button(props) {
  return (
    <button type="submit" className={styles.button}>
      {props.name}
    </button>
  );
}

export default Button;
