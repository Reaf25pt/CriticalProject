import style from "./selectcomponent.module.css";

function SelectComponent(props) {
  return (
    <div className={style.selectcomponent}>
      <select name={props.name}>
        <option>Selecione uma opção</option>
        <option>Lisboa</option>
        <option>Coimbra</option>
        <option>Açores</option>
      </select>
    </div>
  );
}

export default SelectComponent;
