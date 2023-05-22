import styles from "./inputcomponent.module.css";
function InputComponent(props) {
  return (
    <div>
      <input
        className={styles.inputcomponent}
        type={props.type}
        name={props.name}
        onChange={props.onChange}
        placeholder={props.placeholder}
      />
    </div>
  );
}

export default InputComponent;
