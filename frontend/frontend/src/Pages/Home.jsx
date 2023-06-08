import { Outlet } from "react-router-dom";
import Sidebar from "../Components/Sidebar";

function Home() {
  return (
    <div>
      <div>
        <Sidebar />
      </div>
    </div>
  );
}

export default Home;
