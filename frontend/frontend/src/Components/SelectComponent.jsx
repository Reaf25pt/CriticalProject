import style from "./selectcomponent.module.css";

function SelectComponent(props) {
  return (
    <div className={style.selectcomponent}>
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
      >
        <option value="8">Selecione uma opção *</option>
        <option value="0">Lisboa</option>
        <option value="1">Coimbra</option>
        <option value="2">Porto</option>
        <option value="3">Tomar</option>
        <option value="4">Viseu</option>
        <option value="5">Vila Real</option>
      </select>
    </div>
  );
}

export default SelectComponent;
