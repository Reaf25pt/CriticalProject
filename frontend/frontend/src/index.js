import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle";
import "primereact/resources/themes/lara-light-indigo/theme.css";
import "primereact/resources/primereact.min.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./Pages/Register";
import ForgetPassword from "./Pages/ForgetPassword";
import ChangePassword from "./Pages/ChangePassword";
import ActivateAccount from "./Pages/ActivateAccount";
import Home from "./Pages/Home";
import Profile from "./Pages/Profile";
import Projects from "./Pages/Projects";
import Contest from "./Pages/Contest";
import ProjectsCreate from "./Pages/ProjectsCreate";
import ProjectOpen from "./Pages/ProjectOpen";
import "./index.css";
import ContestCreate from "./Pages/ContestCreate";
import Chat from "./Pages/Chat";
import Notifications from "./Pages/Notifications";
import ContestOpen from "./Pages/ContestOpen";
import Start from "./Pages/Start";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<App />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgetpassword" element={<ForgetPassword />} />
      <Route path="/changepassword/:token" element={<ChangePassword />} />
      <Route path="/ativateaccount/:token" element={<ActivateAccount />} />
      <Route path="/home" element={<Home />}>
        <Route path="start" element={<Start />} />
        <Route path="profile" element={<Profile />} />
        <Route path="projects" element={<Projects />} />
        <Route path="contests" element={<Contest />} />
        <Route path="projectscreate" element={<ProjectsCreate />} />
        <Route path="contestcreate" element={<ContestCreate />} />
        <Route path="chat" element={<Chat />} />
        <Route path="notifications" element={<Notifications />} />
        <Route path="projects/:id" element={<ProjectOpen />} />
        <Route path="contests/:id" element={<ContestOpen />} />
      </Route>
    </Routes>
  </BrowserRouter>
);
