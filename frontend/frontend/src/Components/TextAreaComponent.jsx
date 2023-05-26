import style from "./textareacomponent.module.css";
function TextAreaComponent(props) {
  return (
    <div>
      <p>{props.name}</p>
      <textarea className={style.textareacomponent}></textarea>
    </div>
  );
}

export default TextAreaComponent;
