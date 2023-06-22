import { useState, useEffect } from "react";
import { userStore } from "../stores/UserStore";

function ProjectMembersSelect(props) {
  const user = userStore((state) => state.user);
  const [projMembers, setProjMembers] = useState([]);

  useEffect(() => {
    console.log(props.listMembers);
    console.log(props.projId);

    fetch(
      `http://localhost:8080/projetofinal/rest/project/${props.projId}/members`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          token: user.token,
        },
      }
    )
      .then((resp) => resp.json())
      .then((data) => {
        //console.log(data);
        setProjMembers(data);
        // console.log(showMembers);
        //console.log(members);
      })
      .catch((err) => console.log(err));
  }, []);

  return (
    <div className="arrow-select-container">
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
        placeholder={props.placeholder}
        className="form-control"
        listMembers={props.listMembers}
        projId={props.projId}
      >
        <option value="-1">{props.placeholder} </option>
        {/*  {Object.entries(props.listMembers).map(([key, member]) => ( */}
        {projMembers.map((member) => (
          <option key={member.userInvitedId} value={member.userInvitedId}>
            {member.userInvitedFirstName} {member.userInvitedLastName}
          </option>
        ))}
      </select>
    </div>
  );
}

export default ProjectMembersSelect;
