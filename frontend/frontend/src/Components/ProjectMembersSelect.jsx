import { useState, useEffect } from "react";
import { userStore } from "../stores/UserStore";
import { projOpenStore } from "../stores/projOpenStore";

function ProjectMembersSelect(props) {
  const user = userStore((state) => state.user);
  const members = projOpenStore((state) => state.members);

  return (
    <div className="arrow-select-container">
      <select
        name={props.name}
        id={props.id}
        onChange={props.onChange}
        required={props.required}
        placeholder={props.placeholder}
        className="form-control"
        // listMembers={props.listMembers}
        // projId={props.projId}
      >
        <option value="-1">{props.placeholder} </option>
        {/*  {Object.entries(props.listMembers).map(([key, member]) => ( */}
        {members.map((member) => (
          <option key={member.userInvitedId} value={member.userInvitedId}>
            {member.userInvitedFirstName} {member.userInvitedLastName}
          </option>
        ))}
      </select>
    </div>
  );
}

export default ProjectMembersSelect;
