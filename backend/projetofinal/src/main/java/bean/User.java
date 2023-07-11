package bean;

import ENUM.Office;
import ENUM.SkillType;
import ENUM.StatusProject;
import dto.*;
import dto.Project;
import entity.Token;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import mail.AskRecoverPassword;
import mail.ValidateNewAccount;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.jboss.logging.Logger;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final org.jboss.logging.Logger LOGGER = Logger.getLogger(User.class);

    @Inject
    private HttpServletRequest request;

    @EJB
    dao.User userDao;
    @EJB
    dao.Token tokenDao;
    @EJB
    dao.ProjectMember projMemberDao;
    @Inject
    bean.Project projBean;
    @Inject
    HttpServletRequest req;
    @EJB
    dao.Hobby hobbyDao;
    @EJB
    dao.Skill skillDao;

    public User() {
    }

    /**
     * Verifies if email and password used to login have some valid information
     *
     * @param email    represents email inserted when attempting login
     * @param password represents password inserted when attempting login
     * @return true if both parameters have indeed valid information
     */
    public boolean validateLoginInfo(String email, String password) {
        // validates info that is sent from frontend
        boolean res;

        if (email == null || password == null || email.isBlank() || email.isEmpty() || password.isBlank()
                || password.isEmpty()) {
            res = false;
        } else {
            res = true;
        }
        return res;
    }

    /**
     * Verifies if there is a validated account that matches email and password inserted when attempting login. If so creates session identified by a unique token
     *
     * @param email    represents email inserted when attempting login
     * @param password represents password inserted when attempting login
     * @return Profile information, including token that identifies unique session in subsequent requests
     */
    public Profile validateLogin(String email, String password) {

        Profile user = null;

        entity.User userEnt = userDao.findUserByEmail(email);

        if (userEnt != null) {
            if (userEnt.isValidated()) {
                String maskPasswordFromLogin = passMask(password);
                if (userEnt.getPassword().equals(maskPasswordFromLogin)) {

                    Token tokenEnt = new Token();
                    String maskToken = tokenMask(tokenEnt.createToken(email));
                    tokenEnt.setToken(maskToken);
                    tokenEnt.setTokenOwner(userEnt);
                    tokenEnt.setTimeOut(tokenEnt.createTimeOutTimeStamp());

                    userDao.merge(userEnt);
                    tokenDao.merge(tokenEnt);
                    user = convertToProfileDto(userEnt, tokenEnt.getToken());

                    LOGGER.info("User whose user ID is " + userEnt.getUserId() + " has logged in its account. IP Address of request is " + request.getRemoteAddr());

                }
            }
        }

        return user;
    }

    /**
     * Gets information on IP Address that makes HTTP request
     *
     * @return string with IP Address information
     */
    public String getIPAddress() {
        String ipAddress = req.getHeader("X-FORWARDED-FOR");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getRemoteAddr();
        }
        return ipAddress;
    }


    /**
     * Masks password before being persisted in database by calculating the MD5 digest and returning the value as a 32 character hex string
     *
     * @param password represents password inserted in frontend by user
     * @return string of masked password
     */
    private String passMask(String password) {

        return DigestUtils.md5Hex(password).toUpperCase();
    }

    /**
     * Masks token before being persisted in database by calculating the MD5 digest and returning the value as a 32 character hex string
     *
     * @param token represents token generated when login authentication is successful
     * @return string of masked token
     */
    private String tokenMask(String token) {

        return DigestUtils.md5Hex(token).toUpperCase();
    }

    /**
     * Verifies if given string has some valid information
     *
     * @param str represents string to be evaluated
     * @return true if string has no valid information
     */
    public boolean checkStringInfo(String str) {
        boolean res = false;

        if (str == null || str.isBlank() || str.isEmpty()) {
            res = true;
            // info is not filled in as it should
        }
        return res;
    }

    /**
     * Removes token that identifies unique session from database, when user logs out of its account
     *
     * @param token represents token that identifies unique session
     * @return status code 200 when token is removed from database, status code 400 if no session is found for given token and status code 403 when session exists but no user is associated with it
     */
    public int validateLogout(String token) {
        int res;

        Token tokenEnt = tokenDao.findTokenEntByToken(token);

        if (tokenEnt != null) {
            entity.User userEnt = tokenEnt.getTokenOwner();

            if (userEnt != null) {

                tokenDao.remove(tokenEnt);
                LOGGER.info("User whose user ID is " + userEnt.getUserId() + " has logged out of its account. IP Address of request is " + getIPAddress());


                res = 200;
            } else
                res = 400; // sem sessão iniciada
        } else
            res = 403; // sem user associado ao token

        return res;
    }


    /**
     * Verifies if email inserted when creating a new account already exists in database, given that email must be a unique attribute
     * Only if email does not exist proceeds to create a new account
     *
     * @param email represents email inserted to create new account
     * @return status code 400 if there is a validated account using given email, 409 if there is an account waiting for validation (a new email requesting to validate account is sent)
     */
    public int checkEmailInDatabase(String email) {
        // if it is in Database, check if account is validated (could be that user forgot previous regist and never validated account
        int res = 100;

        entity.User user = userDao.findUserByEmail(email);

        if (user != null) {
            if (user.isValidated()) {
                // email is registered in DB and account is validated
                res = 400;
            } else if (!user.isValidated()) {
                // email is registered in DB but account is not validated. A new link is sent by email to validate account

                reNewTokenToValidateAccount(user);
                res = 409;
            }
        }

        return res;
    }

    /**
     * Creates a new account, persisted in table USER for given email. Password is persisted in dabatase masked and not directly for security reasons
     * If successful, an email to validate account is sent to email used to regist
     *
     * @param email    represents email associated with new account
     * @param password represents password associated with new account
     * @return true if account is successfully created and persisted in database (table USER)
     */
    public boolean createNewAccount(String email, String password) {
        boolean res = false;
        entity.User newUser = new entity.User();
        newUser.setEmail(email);
        newUser.setPassword(passMask(password));
        newUser.setContestManager(false);
        newUser.setOpenProfile(false);
        newUser.setValidated(false);
        newUser.setFillInfo(false);
        newUser.setToken(newUser.createTokenForActivation());
        newUser.setTimestampForToken(newUser.createTimeoutTimeStamp());

        userDao.persist(newUser);
        ValidateNewAccount.main(newUser.getEmail(), newUser.getToken());
        res = true;
        LOGGER.info("A new account is created for email " + newUser.getEmail() + " User ID is " + newUser.getUserId() + " . IP Address of request is " + getIPAddress());

        return res;
    }

    /**
     * Validates an account identified by given param if it is still valid, given that link to validate account is valid for a limited time
     * In case link is no longer valid, a new link to validate account is sent to email
     *
     * @param tokenForValidation is a unique token that represents account that is being validated
     * @return status code 200 if account is successfully validated, 400 if link is no longer valid, 404 if no account is found for param
     */
    public int validateNewAccount(String tokenForValidation) {

        int res;

        entity.User userEnt = userDao.findUserByTokenForActivationOrRecoverPass(tokenForValidation);

        if (userEnt != null) {

            if (verifyTimestampOfToken(userEnt, tokenForValidation)) {

                userEnt.setValidated(true);
                userEnt.setToken(null);

                long OL = 0;
                userEnt.setTimestampForToken(OL);

                userDao.merge(userEnt);
                res = 200;
                LOGGER.info("Account of user ID " + userEnt.getUserId() + " is validated. IP Address of request is " + getIPAddress());

            } else {

                reNewTokenToValidateAccount(userEnt);
                res = 400; // bad request - link is not valid anymore
            }

        } else {
            res = 404; // user not found for given token
        }

        return res;


    }

    /**
     * Sends a new email requesting to validate an account
     * Method is called in case there is an attempt to register in the app using email that already exists in database but account is not validated
     * Method is called in case user attempts to validate account through email received but token is no longer valid
     *
     * @param user represents user for given email
     */
    private void reNewTokenToValidateAccount(entity.User user) {
        // could be that user forgot previous regist and never validated account

        String newToken = user.createTokenForActivation();
        user.setToken(newToken);
        user.setTimestampForToken(user.createTimeoutTimeStamp());

        ValidateNewAccount.main(user.getEmail(), newToken);

        userDao.merge(user);
    }

    /**
     * Verifies if link via email, to validate account or to recover password, is valid to complete request
     * Link is valid for 60 minutes
     *
     * @param userEnt represents account whose link refers to
     * @param token   is a unique token that identifies account that is being validated or account whose password is being recovered - it identifies the link
     * @return true if link is valid
     */
    public boolean verifyTimestampOfToken(entity.User userEnt, String token) {
        // 1hr = 3600000 miliseconds

        boolean res = false;

        if (userEnt != null && token != null) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long validTimestamp = userEnt.getTimestampForToken() + 3600000;

            if (currentTime <= validTimestamp) {
                res = true;
            }
        }

        return res;
    }

    /**
     * Sends an email with a link, valid for limited time, with a unique token that identifies validated account to recover password
     * If account is not validated, it will not allow to recover password
     *
     * @param email represents email that identifies account
     * @return true if a link to recover password is sent to given email
     */
    public boolean askToRecoverPassword(String email) {
        // Ask to recover password and send result to email if it exists in DB and account is valid

        boolean res = false;

        entity.User userEnt = userDao.findUserByEmail(email);

        if (userEnt != null) {
            if (userEnt.isValidated()) { // só altera password se conta estiver válida
                String token = userEnt.createTokenToRecoverPassword();
                userEnt.setToken(token);
                userEnt.setTimestampForToken(userEnt.createTimeoutTimeStamp());
                AskRecoverPassword.main(userEnt.getEmail(), token);

                userDao.merge(userEnt);

                res = true;
                LOGGER.info("User ID " + userEnt.getUserId() + " ask to recover password. IP Address of request is " + getIPAddress());

            }
        }
        return res;
    }

    /**
     * Modifies password of account identified by TokenToRecoverPass if link is still valid,  given that link is valid for a limited time
     * In case link is no longer valid, a new link to recover password is sent to email
     *
     * @param tokenToRecoverPass is a unique token that represents account that asked to recover password
     * @param newPassword        represents the new password that should be persisted in database, after being masked
     * @return status code 200 if password is modified successfully, 400 if link is no longer valid, 404 if no account is found for TokenToRecoverPass
     */
    public int newPasswordViaLink(String tokenToRecoverPass, String newPassword) {
        int res;

        entity.User userEnt = userDao.findUserByTokenForActivationOrRecoverPass(tokenToRecoverPass);

        if (userEnt != null) {

            if (verifyTimestampOfToken(userEnt, tokenToRecoverPass)) {

                userEnt.setPassword(passMask(newPassword));
                userEnt.setToken(null);

                long OL = 0;
                userEnt.setTimestampForToken(OL);

                userDao.merge(userEnt);

                res = 200;
                LOGGER.info("User ID " + userEnt.getUserId() + " recovers its password. IP Address of request is " + getIPAddress());

            } else {

                String newToken = userEnt.createTokenToRecoverPassword();
                userEnt.setToken(newToken);
                userEnt.setTimestampForToken(userEnt.createTimeoutTimeStamp());

                AskRecoverPassword.main(userEnt.getEmail(), newToken);

                userDao.merge(userEnt);

                res = 400; // bad request - link is not valid anymore
            }

        } else {
            res = 404; // user not found for given token
        }

        return res;
    }

    /**
     * Verifies if token that makes request has a valid session and a valid account (in another method)
     *
     * @param token identifies session that makes the request
     * @return true if account is valid and token has a valid session
     */
    public boolean checkUserPermission(String token) {
        boolean res = false;

        Token tokenEnt = tokenDao.findTokenEntByToken(token);

        if (tokenEnt != null) {
            if (checkUserAccount(tokenEnt.getTokenOwner())) {
                res = true;
            }
        }

        return res;
    }

    /**
     * Checks if user has a validated account
     *
     * @param user represents user of given token
     * @return true if account is validated
     */
    private boolean checkUserAccount(entity.User user) {
        boolean res = false;

        if (user != null) {
            if (user.isValidated()) {
                res = true;
            }
        }

        return res;
    }

    /**
     * Extends session time for given token
     *
     * @param token identifies session that makes the request
     */
    public void updateSessionTime(String token) {
        Token tokenEnt = tokenDao.findTokenEntByToken(token);

        if (tokenEnt != null) {
            long newSessionTimeOut = tokenEnt.createTimeOutTimeStamp();
            tokenEnt.setTimeOut(newSessionTimeOut);
            tokenDao.merge(tokenEnt);
        }
    }

    /**
     * Edits token profile by adding mandatory information (first and last name, office) in the first time user logs in its account
     * User can also associate a nickname and a photo to its profile
     *
     * @param token   identifies session that makes the request
     * @param newInfo stores information (format: dto) that is sent from frontend
     * @return Profile information updated. Format is DTO
     */
    public Profile addMandatoryInfo(String token, Profile newInfo) {
        Profile updatedUser = null;

        if (newInfo != null) {
            entity.User user = tokenDao.findUserEntByToken(token);

            if (user != null) {
                user.setFirstName(newInfo.getFirstName());
                user.setLastName(newInfo.getLastName());

                if (newInfo.getNickname() != null) {
                    user.setNickname(newInfo.getNickname());
                }

                if (newInfo.getPhoto() != null) {
                    user.setPhoto(newInfo.getPhoto());
                }

                user.setOffice(projBean.setOffice(newInfo.getOfficeInfo()));

                user.setFillInfo(true);
            }

            userDao.merge(user);

            LOGGER.info("User ID " + user.getUserId() + " fills in profile mandatory information in its first login. IP Address of request is " + getIPAddress());

            updatedUser = convertToProfileDto(user, token);
        }

        return updatedUser;
    }

    /**
     * Edits token profile information by choice
     *
     * @param token   identifies session that makes the request
     * @param newInfo stores information (format: dto) that is sent from frontend
     * @return Profile information updated. Format is DTO
     */
    public Profile updateProfile(String token, Profile newInfo) {

        Profile updatedUser = null;

        if (newInfo != null) {

            entity.User userEnt = tokenDao.findUserEntByToken(token);

            if (userEnt != null) {

                if (newInfo.getFirstName() != null) {
                    userEnt.setFirstName(newInfo.getFirstName());
                }

                if (newInfo.getLastName() != null) {
                    userEnt.setLastName(newInfo.getLastName());
                }

                if (newInfo.getNickname() != null) {
                    userEnt.setNickname(newInfo.getNickname());
                }

                if (newInfo.getPhoto() != null) {
                    userEnt.setPhoto(newInfo.getPhoto());
                }

                if (newInfo.getBio() != null) {
                    userEnt.setBio(newInfo.getBio());

                }

                if (userEnt.isContestManager()) {
                    // nunca pode colocar a sua página como pública
                    userEnt.setOpenProfile(false);
                } else {
                    userEnt.setOpenProfile(newInfo.isOpenProfile());
                }

                userEnt.setOffice(projBean.setOffice(newInfo.getOfficeInfo()));
            }
            userDao.merge(userEnt);

            LOGGER.info("User ID " + userEnt.getUserId() + " updates its profile information. IP Address of request is " + getIPAddress());

            updatedUser = convertToProfileDto(userEnt, token);
        }

        return updatedUser;
    }

    /**
     * Converts User Entity to Profile DTO so that information can be sent as HTTP response
     *
     * @param user  contains information of user entity
     * @param token identifies session that makes the request
     * @return Profile information. Format is DTO
     */
    public Profile convertToProfileDto(entity.User user, String token) {
        Profile userDto = new Profile();

        userDto.setUserId(user.getUserId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());

        userDto.setToken(token);
        userDto.setEmail(user.getEmail());

        if (user.getOffice() != null) {
            userDto.setOffice(user.getOffice().getCity());
            userDto.setOfficeInfo(user.getOffice().ordinal());
        }
        //  userDto.setOffice(user.getOffice());
        userDto.setNickname(user.getNickname());
        userDto.setPhoto(user.getPhoto());
        userDto.setBio(user.getBio());
        userDto.setOpenProfile(user.isOpenProfile());
        userDto.setFillInfo(user.isFillInfo());
        userDto.setContestManager(user.isContestManager());
        userDto.setNoActiveProject(projBean.verifyIfUserHasActiveProject(token));

        return userDto;
    }

    /**
     * Modifies password of authenticated user if old password inserted matches password associated with account (saved) in table USER
     * Password is masked before being saved in database, for security reasons
     *
     * @param token       represents session of logged user that makes the request
     * @param oldPassword represents the password persisted in database for account (user) identified by token
     * @param newPassword represents the new password that logged user attempts to associate with its account
     * @return status code 200 if password is modified successfully, 400 if old password inserted does not match password saved in database, 404 if no account is found for token
     */
    public int changeOwnPassword(String token, String oldPassword, String newPassword) {
        // change own password (logged user)

        int res;
        entity.User user = tokenDao.findUserEntByToken(token);

        if (user != null) {
            String oldPassMasked = passMask(oldPassword);
            if (oldPassMasked.equals(user.getPassword())) {
                user.setPassword(passMask(newPassword));
                userDao.merge(user);

                res = 200;
                LOGGER.info("User ID " + user.getUserId() + " changes own password. IP Address of request is " + getIPAddress());

            } else {
                res = 400; // bad request - old password does not match password saved in DB
            }
        } else {
            res = 404; // user not found for given token
        }

        return res;
    }

    /**
     * Get list of projects of which logged user is a member
     *
     * @param token represents session of logged user that makes the request
     * @return list of Project - DTO that stores information of a given project
     */
    public List<Project> getOwnProjectsList(String token) {

        List<Project> projectsList = new ArrayList<>();
        entity.User user = tokenDao.findUserEntByToken(token);

        List<entity.Project> list = projMemberDao.findListOfProjectsByUserId(user.getUserId());

        if (list != null) {
            for (entity.Project p : list) {
                projectsList.add(projBean.convertProjEntityToDto(p));
            }
        }
        return projectsList;
    }

    /**
     * Get list of hobbies associated with token's account
     *
     * @param token represents session of logged user that makes the request
     * @return list of Hobby - DTO that contains information of given hobby
     */
    public List<Hobby> getOwnHobbiesList(String token) {
        List<Hobby> hobbiesList = new ArrayList<>();
        entity.User user = tokenDao.findUserEntByToken(token);

        List<entity.Hobby> list = hobbyDao.findListOfHobbiesByUserId(user.getUserId());

        if (list != null) {
            for (entity.Hobby h : list) {
                hobbiesList.add(convertToHobbyDto(h));
            }
        }

        return hobbiesList;
    }

    /**
     * Associates a hobby with user identified by token
     * Starts by checking if hobby's name is already persisted in database (meaning it is a pre-existing hobby). If that is the case, checks if user already is associated with hobby
     * In case there is no association between hobby and user, it adds such relationship, merging entities with new association. If association already exists, nothing is changed (it means user tried to add hobby that already is in its list of hobbies)
     * If hobby title is a new one, it persists new hobby in database (table HOBBY) and then associates hobby with user.
     *
     * @param token identifies session that makes the request
     * @param title represents name of hobby to be associated with user
     * @return Hobby Dto
     */
    public Hobby addHobby(String token, String title) {
        // adicionar hobby a DB, se não existir, e à lista de hobbies do token

        Hobby hobbyDto = new Hobby();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Hobby hobby = hobbyDao.findHobbyByTitle(title.trim());
            if (hobby != null) {
                // significa que hobby já está na DB -> verificar se já existe relação hobby-user para, n havendo, adicionar a lista de user

                Long res = hobbyDao.findRelationBetweenUserAndHobby(hobby.getHobbyId(), user.getUserId());
                System.out.println(res);
                if (res == 0) {
                    user.getListHobbies().add(hobby);
                    hobby.getListUsers_Hobbies().add(user);

                    userDao.merge(user);
                    hobbyDao.merge(hobby);

                    hobbyDto = convertToHobbyDto(hobby);
                    LOGGER.info("Hobby " + hobby.getHobbyId() + " is associated with user, user ID: " + user.getUserId() + ". IP Address of request is " + getIPAddress());
                } else {
                    //  já existe relação user-hobby
                    hobbyDto = null;
                }
            } else {
                // hobby n está na DB, precisa de ser persisted
                entity.Hobby newHobby = new entity.Hobby();
                newHobby.setHobbyTitle(title.trim());
                newHobby.getListUsers_Hobbies().add(user);
                hobbyDao.persist(newHobby);

                user.getListHobbies().add(newHobby);
                userDao.merge(user);
                LOGGER.info("Hobby " + newHobby.getHobbyId() + " is persisted in database and associated with user, user ID: " + user.getUserId() + ". IP Address of request is " + getIPAddress());
                hobbyDto = convertToHobbyDto(newHobby);
            }

        }
        return hobbyDto;
    }

    /**
     * Converts hobby Entity to Hobby DTO so that information can be sent as HTTP response
     *
     * @param hobby contains information of hobby entity
     * @return Hobby information. Format is DTO
     */
    private Hobby convertToHobbyDto(entity.Hobby hobby) {
        Hobby hobbyDto = new Hobby();

        hobbyDto.setId(hobby.getHobbyId());
        hobbyDto.setTitle(hobby.getHobbyTitle());

        return hobbyDto;
    }

    /**
     * Get list of possible Office values, which are defined as ENUM
     *
     * @return HashMap where index is associated with specific corresponding name
     */
    public HashMap<Integer, String> getOfficeList() {
        HashMap<Integer, String> officeList = new HashMap<>();
        Office[] list = Office.values();

        for (int i = 0; i < list.length; i++) {
            officeList.put(i, list[i].getCity());
        }
        return officeList;
    }

    /**
     * Get list of possible skill type values, which are defined as ENUM
     *
     * @return HashMap where index is associated with specific corresponding name
     */
    public HashMap<Integer, String> getSkillTypesList() {
        HashMap<Integer, String> skillTypesList = new HashMap<>();
        SkillType[] list = SkillType.values();

        for (int i = 0; i < list.length; i++) {
            skillTypesList.put(i, list[i].getType());
        }
        return skillTypesList;
    }

    /**
     * Associates a skill with user identified by token
     * Starts by checking if skill's name is already persisted in database (meaning it is a pre-existing skill). If that is the case, checks if user already is associated with skill
     * In case there is no association between skill and user, it adds such relationship, merging entities with new association. If association already exists, nothing is changed (it means user tried to add skill that already is in its list of skills)
     * If skill title is a new one, it persists new skill in database (table SKILL), including skill type and then associates skill with user.
     *
     * @param token identifies session that makes the request
     * @param skill represents skill information (DTO) to be associated with user
     * @return Skill Dto
     */
    public Skill addSkillToOwnProfile(String token, Skill skill) {

        Skill skillDto = new Skill();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Skill skillInDB = skillDao.findSkillByTitle(skill.getTitle().trim());
            if (skillInDB != null) {
                // significa que skill já está na DB -> verificar se já existe relação skill-user para, n havendo, adicionar a lista de user

                Long res = skillDao.findRelationBetweenUserAndSkill(skillInDB.getSkillId(), user.getUserId());

                if (res == 0) {

                    user.getListSkills().add(skillInDB);
                    skillInDB.getListUsers_Skills().add(user);

                    userDao.merge(user);
                    skillDao.merge(skillInDB);

                    skillDto = convertToSkillDto(skillInDB);
                    LOGGER.info("Skill " + skillInDB.getTitle() + " is associated with user, user ID: " + user.getUserId() + ". IP Address of request is " + getIPAddress());
                } else {
                    skillDto = null;
                }
            } else {
                // skill n está na DB, precisa de ser persisted

                entity.Skill newSkill = new entity.Skill();
                newSkill.setTitle(skill.getTitle().trim());
                attributeSkillType(skill.getSkillType(), newSkill);
                newSkill.getListUsers_Skills().add(user);

                skillDao.persist(newSkill);

                user.getListSkills().add(newSkill);
                userDao.merge(user);
                LOGGER.info("Skill " + newSkill.getSkillId() + " is persisted in database and associated with user, user ID: " + user.getUserId() + ". IP Address of request is " + getIPAddress());
                skillDto = convertToSkillDto(newSkill);
            }

        }
        return skillDto;
    }

    /**
     * Converts skill Entity to Skill DTO so that information can be sent as HTTP response
     *
     * @param skill contains information of skill entity
     * @return Skill information. Format is DTO
     */
    public Skill convertToSkillDto(entity.Skill skill) {
        Skill skillDto = new Skill();

        skillDto.setId(skill.getSkillId());
        skillDto.setTitle(skill.getTitle());
        skillDto.setSkillType(skill.getType().ordinal());

        return skillDto;
    }

    /**
     * Attributes skill type ENUM according to int value that is sent from frontend
     *
     * @param skillType: int that identifies corresponding skillType ENUM
     * @param newSkill   represents a new skill to be persisted in database, therefore no skill type is attributed
     */
    public void attributeSkillType(int skillType, entity.Skill newSkill) {

        switch (skillType) {
            case 0:
                newSkill.setType(SkillType.KNOWLEDGE);
                break;
            case 1:
                newSkill.setType(SkillType.SOFTWARE);
                break;
            case 2:
                newSkill.setType(SkillType.HARDWARE);
                break;
            case 3:
                newSkill.setType(SkillType.TOOL);
                break;
        }
    }

    /**
     * Verifies if skill DTO has mandatory information when token tries to associate a new skill with its profile
     *
     * @param skill represents skill information to be associated with given token
     * @return true if mandatory information is not available
     */
    public boolean checkSkillInfo(Skill skill) {
        boolean res = false;

        if (skill == null) {
            res = true;
        } else {
            if (checkStringInfo(skill.getTitle())) {

                res = true;
            }
        }
        return res;
    }

    /**
     * Get list of skills associated with token's account
     *
     * @param token represents session of logged user that makes the request
     * @return list of Skill - DTO that contains information of given skill
     */
    public List<Skill> getOwnSkillsList(String token) {

        List<Skill> skillsList = new ArrayList<>();
        entity.User user = tokenDao.findUserEntByToken(token);

        List<entity.Skill> list = skillDao.findListOfSkillsByUserId(user.getUserId());
        if (list != null) {
            for (entity.Skill s : list) {
                skillsList.add(convertToSkillDto(s));
            }
        }

        return skillsList;
    }

    /**
     * Removes association between hobby and user if such relationship exists
     *
     * @param token identifies session (and user) that makes the request
     * @param id    identifies hobby to be removed from list of hobbies of user
     * @return true if hobby is removed from user's list of hobbies
     */
    public boolean deleteHobby(String token, int id) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Hobby hobby = hobbyDao.findHobbyOfUserById(user.getUserId(), id);

            if (hobby != null) {
                // apagar apenas da lista do user

                hobby.getListUsers_Hobbies().remove(user);
                user.getListHobbies().remove(hobby);

                userDao.merge(user);
                hobbyDao.merge(hobby);

                res = true;
                LOGGER.info("Hobby " + hobby.getHobbyTitle() + " is removed from user's hobbies list, user ID: " + user.getUserId() + ". IP Address of request is " + getIPAddress());

            }
        }
        return res;
    }

    /**
     * Removes association between skill and user if such relationship exists
     *
     * @param token identifies session (and user) that makes the request
     * @param id    identifies skill to be removed from list of skills of user
     * @return true if skill is removed from user's list of skills
     */
    public boolean deleteSkill(String token, int id) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Skill skill = skillDao.findSkillOfUserById(user.getUserId(), id);

            if (skill != null) {
                // apagar apenas da lista do user

                skill.getListUsers_Skills().remove(user);
                user.getListSkills().remove(skill);

                userDao.merge(user);
                skillDao.merge(skill);

                res = true;
                LOGGER.info("Skill " + skill.getTitle() + " is removed from user's skills list, user ID: " + user.getUserId() + ". IP Address of request is " + getIPAddress());

            }
        }
        return res;
    }

    /**
     * Get list of skills whose name contains given input (str)
     * It will not show any skills that are already associated with user's account
     *
     * @param token represents session of logged user that makes the request
     * @param str   represents input that is written by user in frontend
     * @return list of Skill - DTO that contains information of given skill
     */
    public List<Skill> getSkillsList(String str, String token) {
        List<Skill> listSkillDto = new ArrayList<>();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            List<entity.Skill> list = skillDao.findSkillListContainingStr(str.toLowerCase());

            if (list != null) {
                for (entity.Skill s : list) {
                    Long count = skillDao.findRelationBetweenUserAndSkill(s.getSkillId(), user.getUserId());

                    if (count == 0) {
                        listSkillDto.add(convertToSkillDto(s));
                    }
                }
            }
        }
        return listSkillDto;
    }

    /**
     * Get list of hobbies whose name contains given input (str)
     * It will not show any hobbies that are already associated with user's account
     *
     * @param token represents session of logged user that makes the request
     * @param str   represents input that is written by user in frontend
     * @return list of Hobby - DTO that contains information of given hobby
     */
    public List<Hobby> getHobbiesList(String str, String token) {

        List<Hobby> listHobbiesDto = new ArrayList<>();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            List<entity.Hobby> list = hobbyDao.findHobbyListContainingStr(str.toLowerCase());

            if (list != null) {
                for (entity.Hobby h : list) {
                    Long count = hobbyDao.findRelationBetweenUserAndHobby(h.getHobbyId(), user.getUserId());
                    if (count == 0) {
                        listHobbiesDto.add(convertToHobbyDto(h));
                    }
                }
            }
        }
        return listHobbiesDto;
    }

    /**
     * Change user role (profile A <-> profile B) while ensuring normal functioning of the app
     * Method (endpoint) can only be called in Postman
     * Change can only occur if user's account is validated
     * Change to Profile B needs no extra validation
     * Change to profile A needs to verify several things:
     * - leave an active project (status is not cancelled or finished) if user has one (and can only happen if it is not the only project's manager)
     * - withdraw from being task owner of any tasks of active project whose status is NOT FINISHED while assigning a new task's owner. If there is no other project member to assign, it will not be possible to change user's role
     * - if indeed role can be changed to Profile A, it is necessary to refuse any pending invitations to participate in active projects
     *
     * @param role   can take value 0 to change to Profile B or value 1 to change to Profile A
     * @param userId identifies user to have its role modified
     * @return true if role is changed successfully
     */
    public boolean modifyProfileType(int role, int userId) {
        boolean res = false;

        entity.User user = userDao.findUserById(userId);
        if (user != null) {
            if (user.isValidated()) {

                if (role == 0) {
                    user.setContestManager(false);
                    userDao.merge(user);
                    res = true;
                    LOGGER.info("User whose user ID is " + user.getUserId() + " has its role modified to Profile B - normal access to app");


                } else if (role == 1) {

                    entity.ProjectMember pm = projMemberDao.findActiveProjectMemberByUserId(userId);
                    if (pm != null) {
                        System.out.println("ID proj member " + pm.getId());
                        if (projBean.hasEnoughManagers(pm.getProjectToParticipate().getId(), userId)) {
                            // projecto fica com gestor depois de pessoa sair. Senao não pode ser alterado

                            if (pm.getProjectToParticipate().getStatus() == StatusProject.CANCELLED || pm.getProjectToParticipate().getStatus() == StatusProject.FINISHED) {
                                // penso ser desnecessário, por nunca se verificar
                                user.setContestManager(true);
                                user.setOpenProfile(false);
                                userDao.merge(user);
                                res = true;
                                LOGGER.info("User whose user ID is " + user.getUserId() + " has its role modified to Profile A - contest manager");

                            } else {
                                boolean canLeave = projBean.dealWithTasksBeforeLeavingProject(userId, pm.getProjectToParticipate());

                                if (canLeave) {
                                    refusePendingInvitations(userId);
                                    user.setContestManager(true);
                                    user.setOpenProfile(false);
                                    userDao.merge(user);
                                    res = true;
                                    LOGGER.info("User whose user ID is " + user.getUserId() + " has its role modified to Profile A - contest manager");

                                }
                            }
                        }
                    } else {
                        // não tem projecto activo, pode simplesmente mudar o atributo contestManager
                        refusePendingInvitations(userId);
                        user.setContestManager(true);
                        user.setOpenProfile(false);
                        userDao.merge(user);
                        res = true;
                        LOGGER.info("User whose user ID is " + user.getUserId() + " has its role modified to Profile A - contest manager");
                    }
                }
            }
        }

        return res;
    }

    /**
     * Refuses pending invitations to participate in given project, by setting attribute answered to true
     *
     * @param userId identifies user whose pending invitations must be refused
     */
    public void refusePendingInvitations(int userId) {
        List<entity.ProjectMember> listPotentialpm = projMemberDao.findListOfPotentialMembersByUserId(userId);
        if (listPotentialpm != null) {
            for (entity.ProjectMember p : listPotentialpm) {
                p.setAnswered(true);
                projMemberDao.merge(p);
            }
        }
        //TODO
        // TODO add logger, decide if do so when accepting in other project. DELETE NOTIFICATIONS FOR SUCH PM
    }

    /**
     * Get list of all users that have a validated account
     *
     * @param token identifies session that makes the request
     * @return list of UserInfo - a DTO that displays minimum necessary information to display in frontend
     */
    public List<UserInfo> getAllUsers(String token) {

        List<UserInfo> list = new ArrayList<>();

        List<entity.User> allUsers = userDao.findAllUsersWithValidatedAccount();
        if (allUsers != null) {
            for (entity.User u : allUsers) {
                list.add(convertUserEntToMinimalDto(u));
            }
        }
        return list;
    }

    /**
     * Converts User Entity to UserInfo DTO so that information can be sent as HTTP response
     *
     * @param u     contains information of user entity
     * @return UserInfo information. Format is DTO
     */
    private UserInfo convertUserEntToMinimalDto(entity.User u) {
        UserInfo user = new UserInfo();

        user.setId(u.getUserId());
        user.setFirstName(u.getFirstName());
        user.setLastName(u.getLastName());
        user.setNickname(u.getNickname());
        user.setPhoto(u.getPhoto());
        user.setOpenProfile(u.isOpenProfile());
        return user;
    }

    /**
     * Get list of users to suggest when searching app users.
     * Users must have a validated account and name or nickname must contain input inserted by user in frontend
     * Removes from list of suggestions token's own account
     *
     * @param token identifies session that makes the request
     * @param str   represents input that is written by user in frontend
     * @return list of UserInfo - a DTO that displays minimum necessary information to display in frontend
     */
    public List<UserInfo> getUsersToSuggest(String str, String token) {

        List<UserInfo> list = new ArrayList<>();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            List<entity.User> listEnt = userDao.findUserContainingStr(str.toLowerCase());

            if (listEnt != null) {
                List<entity.User> tempList = listEnt.stream().filter(userE -> userE.getUserId() != user.getUserId()).collect(Collectors.toList());
                // remove o pp user que faz a pesquisa
                if (tempList != null) {

                    for (entity.User u : tempList) {
                        list.add(convertUserEntToMinimalDto(u));
                    }
                }
            }
        }
        return list;
    }

    /**
     * Gets profile information of another app user, so that it can be visited by token that makes the request
     *
     * @param token  identifies session that makes the request
     * @param userId identifies the user, whose profile information is being requested to be displayed in frontend
     * @return AnotherProfile DTO, that contains information of users profile, its skills and hobbies and minimum necessary information of its projects
     */
    public AnotherProfile getAnotherProfile(String token, int userId) {

        AnotherProfile profile = new AnotherProfile();
        entity.User loggedUser = tokenDao.findUserEntByToken(token);
        if (loggedUser != null) {
            entity.User user = userDao.findUserById(userId);
            if (user != null) {
                profile.setId(user.getUserId());
                profile.setEmail(user.getEmail());
                profile.setFirstName(user.getFirstName());
                profile.setLastName(user.getLastName());
                profile.setOffice(user.getOffice().getCity());
                profile.setOfficeInfo(user.getOffice().ordinal());
                // TODO testar q nulos não partem o programa - n permitir que projecto tenha office nulo
                profile.setNickname(user.getNickname());
                profile.setPhoto(user.getPhoto());
                profile.setBio(user.getBio());
                profile.setOpenProfile(user.isOpenProfile());

                if (user.getListSkills() != null) {
                    profile.setSkills(projBean.convertListSkillsDTO(user.getListSkills()));
                }

                if (user.getListHobbies() != null) {
                    profile.setHobbies(convertListHobbiesToDto(user.getListHobbies()));
                }

                if (getListOfProjectsOfGivenUser(userId)) {
                    // user tem projectos associados ao seu perfil
                    profile.setProjects(getUserProjects(userId));
                }

                LOGGER.info("User whose user ID is " + loggedUser.getUserId() + " visits account: " + user.getUserId() + ". IP Address of request is " + getIPAddress());

            }
        }
        return profile;
    }

    /**
     * Gets list of projects associated with given user (another app user)
     *
     * @param userId represents another app user whose profile page is meant to be visited
     * @return list of ProjectMinimal DTO, that contains minimum necessary information of user's projects
     */
    private List<ProjectMinimal> getUserProjects(int userId) {
        List<ProjectMinimal> list = new ArrayList<>();

        List<entity.Project> listEnt = projMemberDao.findListOfProjectsByUserId(userId);
        if (listEnt != null) {
            for (entity.Project p : listEnt) {
                ProjectMinimal proj = new ProjectMinimal();
                proj.setId(p.getId());
                proj.setTitle(p.getTitle());
                proj.setStatus(p.getStatus().getStatus());
                proj.setStatusInt(p.getStatus().ordinal());
                proj.setCreationDate(p.getCreationDate());
                list.add(proj);
            }
        }


        return list;
    }

    /** Verifies if given user (another app user) has projects associated with its account (active and not removed in projectMembers table)
     * @param userId represents another app user whose profile page is meant to be visited
     * @return true if given user has projects associated with its account
     */
    private boolean getListOfProjectsOfGivenUser(int userId) {
        boolean res = false;
        List<entity.Project> list = projMemberDao.findListOfProjectsByUserId(userId);
        if (list != null) {
            res = true;
        }
        return res;
    }

    /**
     * Converts list of hobby Entity to list of Hobby DTO
     * @param listHobbies represents list of hobbied associated with a given account
     * @return list of hobby DTO
     */
    private List<Hobby> convertListHobbiesToDto(List<entity.Hobby> listHobbies) {
        List<Hobby> listDtos = new ArrayList<>();

        for (entity.Hobby h : listHobbies) {
            listDtos.add(convertToHobbyDto(h));
        }
        return listDtos;
    }

    /**
     * Verifies if user has open profile, so that its profile can be visited by other app users
     *
     * @param id identifies the user, whose profile information is being requested to be displayed in frontend
     * @return true if profile can be viewed by other app users
     */
    public boolean checkUserHasOpenProfile(int id) {

        boolean res = false;
        entity.User user = userDao.findUserById(id);
        if (user != null) {
            if (user.isOpenProfile()) {
                res = true;
            }
        }

        return res;
    }


    /**
     * Gets Project information on current active project token might have
     *
     * @param token identifies session that makes the request
     * @return Project DTO with complete information of project and user relationship with given project (if it is a manager and/ or member)
     */
    public Project getActiveProjectInfo(String token) {

        Project dto = new Project();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {

            entity.Project proj = projMemberDao.findActiveProjectByUserId(user.getUserId());

            if (proj != null) {
                dto = projBean.convertProjEntityToDto(proj);
            }
        }
        return dto;
    }
}

