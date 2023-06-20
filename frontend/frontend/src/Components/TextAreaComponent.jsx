function TextAreaComponent(props) {
  return (
    <div className="d-flex flex-column d-flex align-items-center">
      <textarea
        className="text-dark bg-white h-100 w-100 rounded-2 "
        placeholder={props.placeholder}
        name={props.name}
        onChange={props.onChange}
        required={props.required}
        disabled={props.disabled}
        id={props.id}
        defaultValue={props.defaultValue}
        type={props.type}
      ></textarea>
    </div>
  );
}

export default TextAreaComponent;
