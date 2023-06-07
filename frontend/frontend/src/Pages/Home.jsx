import { Col, Container, Row } from "react-bootstrap";
import MainTitle from "../Components/MainTitle";
import HeaderComponent from "../Components/HeaderComponent";
import Sidebar from "../Components/Sidebar";
import { Outlet } from "react-router-dom";
import Footer from "../Components/Footer";
import style from "./home.module.css";

function Home() {
  return (
    <div>
      <Sidebar />
    </div>
  );
}

export default Home;
