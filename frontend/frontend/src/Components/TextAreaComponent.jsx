import style from "./textareacomponent.module.css";
function TextAreaComponent(props) {
  return (
    <div className="d-flex flex-column d-flex align-items-center">
      <p>{props.name}</p>
      <textarea
        class="form-control"
        // rows="13"
        // style={{ resize: "none", height: "100%" }}
        placeholder={props.placeholder}
      ></textarea>
    </div>
  );
}

export default TextAreaComponent;
