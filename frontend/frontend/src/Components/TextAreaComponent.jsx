function TextAreaComponent(props) {
  return (
    <div className="d-flex flex-column d-flex align-items-center">
      <textarea
        className="text-dark bg-white h-75 w-100 rounded-2 "
        placeholder={props.placeholder}
        name={props.name}
        onChange={props.onChange}
        required={props.required}
        disabled={props.disabled}
        id={props.id}
      ></textarea>
    </div>
  );
}

export default TextAreaComponent;
