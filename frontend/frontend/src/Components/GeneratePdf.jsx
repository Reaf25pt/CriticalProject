import React, { useEffect, useState } from "react";
import { PDFDocument, StandardFonts, rgb } from "pdf-lib";
import styles from "./ButtonComponent.module.css";
import { useParams } from "react-router";
import { userStore } from "../stores/UserStore";
import { contestOpenStore } from "../stores/ContestOpenStore";

function GeneratePdf() {
  const [stats, setStats] = useState([]);
  const { id } = useParams();
  const user = userStore((state) => state.user);
  const contest = contestOpenStore((state) => state.contest);

  useEffect(() => {
    fetch(`http://localhost:8080/projetofinal/rest/contest/stats/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        token: user.token,
      },
    })
      .then((resp) => resp.json())
      .then((data) => {
        setStats(data);
        console.log(data);
      })
      .catch((err) => console.log(err));
  }, []);

  const handleClick = async () => {
    // Create a new PDF document
    const pdfDoc = await PDFDocument.create();

    // Add a new page to the document
    const page = pdfDoc.addPage();

    // Set the font and font size
    const font = await pdfDoc.embedFont(StandardFonts.Helvetica);
    page.setFont(font);
    page.setFontSize(12);

    // Primeiro retangulo
    page.drawText(stats.info[0], {
      x: 10,
      y: 800,
      size: 25,
    });
    page.drawText("Data de início: " + stats.info[1], { x: 450, y: 820 });
    page.drawText("Data de fim: " + stats.info[2], { x: 450, y: 800 });
    page.drawLine({
      start: { x: 0, y: 750 },
      end: { x: 800, y: 750 },
      thickness: 1,
      color: rgb(0, 0, 0),
    });
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    page.drawText("Projetos submetidos a concurso por localidade", {
      x: 90,
      y: 730,
      size: 20,
    });

    page.drawRectangle({
      x: 10,
      y: 600,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 1, 0), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Lisboa", { x: 30, y: 690 });
    page.drawText(stats.lisboa[0], { x: 30, y: 640 });
    page.drawText(stats.lisboa[1] + "%", { x: 30, y: 620 });

    page.drawRectangle({
      x: 110,
      y: 600,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 0, 0), // black color: ;
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Coimbra", { x: 125, y: 690 });
    page.drawText(stats.coimbra[0], { x: 130, y: 640 });
    page.drawText(stats.coimbra[1] + "%", { x: 130, y: 620 });

    page.drawRectangle({
      x: 210,
      y: 600,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 0, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Porto", { x: 230, y: 690 });
    page.drawText(stats.porto[0], { x: 230, y: 640 });
    page.drawText(stats.porto[1] + "%", { x: 230, y: 620 });

    page.drawRectangle({
      x: 310,
      y: 600,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 1, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Tomar", { x: 330, y: 690 });
    page.drawText(stats.tomar[0], { x: 330, y: 640 });
    page.drawText(stats.tomar[1] + "%", { x: 330, y: 620 });

    page.drawRectangle({
      x: 410,
      y: 600,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 0, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Viseu", { x: 430, y: 690 });
    page.drawText(stats.viseu[0], { x: 430, y: 640 });
    page.drawText(stats.viseu[1] + "%", { x: 430, y: 620 });

    page.drawRectangle({
      x: 510,
      y: 600,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 1, 0), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Vila Real", { x: 530, y: 690 });
    page.drawText(stats.vilareal[0], { x: 530, y: 640 });
    page.drawText(stats.vilareal[1] + "%", { x: 530, y: 620 });

    page.drawLine({
      start: { x: 0, y: 550 },
      end: { x: 800, y: 550 },
      thickness: 1,
      color: rgb(0, 0, 0),
    });

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    page.drawText("Projetos aceites a concurso por localidade", {
      x: 90,
      y: 530,
      size: 20,
    });

    page.drawRectangle({
      x: 10,
      y: 400,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 1, 0), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Lisboa", { x: 30, y: 490 });
    page.drawText(stats.lisboaaccepted[0], { x: 30, y: 440 });
    page.drawText(stats.lisboaaccepted[1] + "%", { x: 30, y: 420 });

    page.drawRectangle({
      x: 110,
      y: 400,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 0, 0), // black color: ;
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Coimbra", { x: 125, y: 490 });
    page.drawText(stats.coimbraaccepted[0], { x: 130, y: 440 });
    page.drawText(stats.coimbraaccepted[1] + "%", { x: 130, y: 420 });

    page.drawRectangle({
      x: 210,
      y: 400,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 0, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Porto", { x: 230, y: 490 });
    page.drawText(stats.portoaccepted[0], { x: 230, y: 440 });
    page.drawText(stats.portoaccepted[1] + "%", { x: 230, y: 420 });

    page.drawRectangle({
      x: 310,
      y: 400,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 1, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Tomar", { x: 330, y: 490 });
    page.drawText(stats.tomaraccepted[0], { x: 330, y: 440 });
    page.drawText(stats.tomaraccepted[1] + "%", { x: 330, y: 420 });

    page.drawRectangle({
      x: 410,
      y: 400,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 0, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Viseu", { x: 430, y: 490 });
    page.drawText(stats.viseuaccepted[0], { x: 430, y: 440 });
    page.drawText(stats.viseuaccepted[1] + "%", { x: 430, y: 420 });

    page.drawRectangle({
      x: 510,
      y: 400,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 1, 0), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Vila Real", { x: 530, y: 490 });
    page.drawText(stats.vilarealaccepted[0], { x: 530, y: 440 });
    page.drawText(stats.vilarealaccepted[1] + "%", { x: 530, y: 420 });

    page.drawLine({
      start: { x: 0, y: 550 },
      end: { x: 800, y: 550 },
      thickness: 1,
      color: rgb(0, 0, 0),
    });
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    page.drawText("Projetos terminados por localidade", {
      x: 150,
      y: 330,
      size: 20,
    });

    page.drawRectangle({
      x: 10,
      y: 200,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 1, 0), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Lisboa", { x: 30, y: 290 });
    page.drawText(stats.lisboafinished[0], { x: 30, y: 240 });
    page.drawText(stats.lisboafinished[1] + "%", { x: 30, y: 220 });

    page.drawRectangle({
      x: 110,
      y: 200,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 0, 0), // black color: ;
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Coimbra", { x: 125, y: 290 });
    page.drawText(stats.coimbrafinished[0], { x: 130, y: 240 });
    page.drawText(stats.coimbrafinished[1] + "%", { x: 130, y: 220 });

    page.drawRectangle({
      x: 210,
      y: 200,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 0, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Porto", { x: 230, y: 290 });
    page.drawText(stats.portofinished[0], { x: 230, y: 240 });
    page.drawText(stats.portofinished[1] + "%", { x: 230, y: 220 });

    page.drawRectangle({
      x: 310,
      y: 200,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 1, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Tomar", { x: 330, y: 290 });
    page.drawText(stats.tomarfinished[0], { x: 330, y: 240 });
    page.drawText(stats.tomarfinished[1] + "%", { x: 330, y: 220 });

    page.drawRectangle({
      x: 410,
      y: 200,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(1, 0, 1), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Viseu", { x: 430, y: 290 });
    page.drawText(stats.viseufinished[0], { x: 430, y: 240 });
    page.drawText(stats.viseufinished[1] + "%", { x: 430, y: 220 });

    page.drawRectangle({
      x: 510,
      y: 200,
      width: 80,
      height: 80,
      borderWidth: 2,
      borderColor: rgb(0, 1, 0), // black color
      borderOpacity: 1,
      fillOpacity: 0,
    });

    page.drawText("Vila Real", { x: 530, y: 290 });
    page.drawText(stats.vilarealfinished[0], { x: 530, y: 240 });
    page.drawText(stats.vilarealfinished[1] + "%", { x: 530, y: 220 });

    page.drawLine({
      start: { x: 0, y: 350 },
      end: { x: 800, y: 350 },
      thickness: 1,
      color: rgb(0, 0, 0),
    });

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    page.drawLine({
      start: { x: 0, y: 150 },
      end: { x: 800, y: 150 },
      thickness: 1,
      color: rgb(0, 0, 0),
    });

    page.drawText("Número médio elementos por projeto: ", {
      x: 10,
      y: 100,
      size: 20,
    });
    page.drawText(stats.averages[0], { x: 400, y: 100, size: 15 });

    page.drawText("Tempo médio de execução dos projetos:  ", {
      x: 10,
      y: 50,
      size: 20,
    });
    page.drawText(stats.averages[1], { x: 400, y: 50, size: 15 });

    // Serialize the PDF document to a Uint8Array
    const pdfBytes = await pdfDoc.save();

    // Create a Blob from the Uint8Array
    const pdfBlob = new Blob([pdfBytes], { type: "application/pdf" });

    // Create a download link for the PDF file
    const downloadLink = document.createElement("a");
    downloadLink.href = URL.createObjectURL(pdfBlob);
    downloadLink.download = `Estatísticas ${contest.title}.pdf`;

    // Append the link to the document body and click it programmatically
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
  };

  return (
    <div>
      <button className={styles.button} onClick={handleClick}>
        Exportar PDF
      </button>
    </div>
  );
}

export default GeneratePdf;
