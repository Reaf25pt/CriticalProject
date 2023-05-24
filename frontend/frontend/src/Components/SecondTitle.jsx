import styles from "./secondtitle.module.css";

function SecondTitle(props) {
  return (
    <div className={styles.secondtitle}>
      <p>{props.name}</p>
    </div>
  );
}

export default SecondTitle;
