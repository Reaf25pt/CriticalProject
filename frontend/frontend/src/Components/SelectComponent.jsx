function SelectComponent(props) {
  return (
    <div>
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
        className="form-control"
      >
        <option value="8">{props.placeholder}</option>
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
