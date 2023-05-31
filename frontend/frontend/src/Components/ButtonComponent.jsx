import styles from "./ButtonComponent.module.css";
function ButtonComponent(props) {
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

export default ButtonComponent;
