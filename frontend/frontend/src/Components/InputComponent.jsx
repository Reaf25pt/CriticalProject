import styles from "./inputcomponent.module.css";
function InputComponent(props) {
  return (
    <div className="mt-3">
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
        title={props.title}
      />
    </div>
  );
}

export default InputComponent;
