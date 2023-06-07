import ButtonComponent from "../Components/ButtonComponent";
import InputComponent from "../Components/InputComponent";

function ChangePasswordIn() {
  const handleSubmit = "";

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-lg-5">
          <form
            className="mt-5 p-5 bg-secondary rounded-5 vh-50 d-flex-column   "
            onSubmit={handleSubmit}
          >
            <div className="row mb-3">
              <div className="form-outline">
                <InputComponent
                  placeholder={"Password"}
                  id="password"
                  required
                  name="password"
                  type="password"
                />
              </div>
            </div>
            <div className="row mb-3">
              <div className="form-outline">
                <InputComponent
                  placeholder={"Confirmar a Password"}
                  id="password"
                  required
                  name="password"
                  type="password"
                />
              </div>
            </div>
            <div className="row">
              <ButtonComponent name={"Alterar password"} type="submit" />
            </div>
          </form>{" "}
        </div>
      </div>
    </div>
  );
}

export default ChangePasswordIn;
