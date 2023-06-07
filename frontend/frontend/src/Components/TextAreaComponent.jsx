function TextAreaComponent(props) {
  return (
    <div className="d-flex flex-column d-flex align-items-center">
      <p>{props.name}</p>
      <textarea
        className="bg-white h-100 w-100 rounded-2"
        // rows="13"
        // style={{ resize: "none", height: "100%" }}
        placeholder={props.placeholder}
        name={props.name}
        onChange={props.onChange}
        required={props.required}
        id={props.id}
      ></textarea>
    </div>
  );
}

export default TextAreaComponent;
