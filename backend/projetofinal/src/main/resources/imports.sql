INSERT INTO user (office, userId, contestManager, email, fillInfo, firstName, lastName, openProfile, password, validated, timestampForToken) values (1,1, false, 'jccramalho@hotmail.com', true, 'Joana', 'Ramalho', false, '25D55AD283AA400AF464C76D713C07AD', true, 0);
INSERT INTO user (office, userId, contestManager, email, fillInfo, firstName, lastName, openProfile, password, validated,timestampForToken) values (2,2, false, 'jccramalho+1@hotmail.com', true, 'João', 'Ramalho', false, '25D55AD283AA400AF464C76D713C07AD', true, 0);
INSERT INTO user (office, userId, contestManager, email, fillInfo, firstName, lastName, openProfile, password, validated,timestampForToken) values (3,3, false, 'jccramalho+2@hotmail.com', true, 'Rodrigo', 'Ferreira', false, '25D55AD283AA400AF464C76D713C07AD', true, 0);
INSERT INTO user (office, userId, contestManager, email, fillInfo, firstName, lastName, openProfile, password, validated,timestampForToken) values (4,4, false, 'jccramalho+3@hotmail.com', true, 'Raquel', 'Soares', false, '25D55AD283AA400AF464C76D713C07AD', true, 0);
INSERT INTO hobby (id, title) value (1, 'viajar');
INSERT INTO hobby (id, title) value (2, 'passear');
INSERT INTO hobby (id, title) value (3, 'comer');
INSERT INTO hobby (id, title) value (4, 'ler');
INSERT INTO hobby (id, title) value (5, 'fazer trilhos');
INSERT INTO skill (skillId, title, skillType) value (1, 'java', 1);
INSERT INTO skill (skillId, title, skillType) value (2, 'react', 2);
INSERT INTO skill (skillId, title, skillType) value (3, 'IDE', 3);
INSERT INTO skill (skillId, title, skillType) value (4, 'JUnit', 0);
INSERT INTO skill (skillId, title, skillType) value (5, 'python', 1);
INSERT INTO keyword (id, title) value (1, 'programação');
INSERT INTO keyword (id, title) value (2, 'backend');
INSERT INTO keyword (id, title) value (3, 'testes');
INSERT INTO keyword (id, title) value (4, 'frontend');
INSERT INTO keyword (id, title) value (5, 'dados');
INSERT INTO project (id, title, details, creationDate, membersNumber, status) value (1, 'Proj1', 'tbd', '2023-04-02', 3, 0 );
INSERT INTO project (id, title, details, creationDate, membersNumber, status) value (2, 'Proj2', 'tbd', '2023-04-02', 5, 0 );
INSERT INTO projectmembers (id, accepted, answered, manager, removed, selfInvite, projectToParticipate_id, userInvited_userId) VALUE (1, true, true, true, false, true, 1, 1);
INSERT INTO projectmembers (id, accepted, answered, manager, removed, selfInvite, projectToParticipate_id, userInvited_userId) VALUE (2, true, true, true, false, true, 2, 2);
INSERT INTO projectmembers (id, accepted, answered, manager, removed, selfInvite, projectToParticipate_id, userInvited_userId) VALUE (3, true, true, false, false, false, 1, 3);
INSERT INTO task (id, details, finishDate, startDate, status, title, project_id, taskOwner_userId) VALUE (1, 'tbd', '2023-05-07' , '2023-05-05',0, 'Tarefa 1', 1, 1);
INSERT INTO task (id, details, finishDate, startDate, status, title, project_id, taskOwner_userId) VALUE (2, 'tbd', '2023-05-07' , '2023-05-05',0, 'Tarefa 1', 2, 2);





