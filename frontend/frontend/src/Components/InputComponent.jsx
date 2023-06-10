import styles from "./inputcomponent.module.css";
function InputComponent(props) {
  return (
    <div>
      <input
        className="form-control bg-light h-100"
        type={props.type}
        name={props.name}
        onChange={props.onChange}
        placeholder={props.placeholder}
        minLength={props.minLength}
        pattern={props.pattern}
        required={props.required}
        id={props.id}
        accept={props.accept}
        defaultValue={props.defaultValue}
      />
    </div>
  );
}

export default InputComponent;
