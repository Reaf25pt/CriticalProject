import style from "./textareacomponent.module.css";
function TextAreaComponent(props) {
  return (
    <div className="d-flex flex-column d-flex align-items-center">
      <p>{props.name}</p>
      <textarea className={style.textareacomponent}></textarea>
    </div>
  );
}

export default TextAreaComponent;
