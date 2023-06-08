import { Outlet } from "react-router-dom";
import Sidebar from "../Components/Sidebar";
import RegisterIn from "./RegisterIn";

function Home() {
  return (
    <div>
      <div>
        {/* <Sidebar /> */}
        <RegisterIn />
      </div>
    </div>
  );
}

export default Home;
