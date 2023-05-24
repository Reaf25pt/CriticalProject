import styles from "./Button.module.css";
function Button(props) {
  return (
    <button
      type={props.type}
      onClick={props.onClick}
      onSubmit={props.onSubmit}
      className={styles.button}
    >
      {props.name}
    </button>
  );
}

export default Button;
