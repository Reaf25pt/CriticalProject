import { Outlet } from "react-router-dom";
import Sidebar from "../Components/Sidebar";
import RegisterIn from "./RegisterIn";
import { userStore } from "../stores/UserStore";
import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";

function Home() {
  const user = userStore((state) => state.user);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    //  TODO bom para colocar no outlet / context

    if (!user && location.pathname !== "/") {
      navigate("/");
    }
  }, [user]);

  if (!user) {
    return null;
  }

  if (user.fillInfo) {
    return (
      <div>
        <div>
          <Sidebar />
        </div>
      </div>
    );
  }

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
