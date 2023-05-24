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
        minLength={props.minLength}
        pattern={props.pattern}
        required={props.required}
        id={props.id}
      />
    </div>
  );
}

export default InputComponent;
