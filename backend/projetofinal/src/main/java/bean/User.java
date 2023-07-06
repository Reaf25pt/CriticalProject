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
import org.apache.logging.log4j.Logger;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(User.class);

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
     * @param email represents email inserted when attempting login
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
     * @param email represents email inserted when attempting login
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

    public String getIPAddress() {
        String ipAddress = req.getHeader("X-FORWARDED-FOR");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getRemoteAddr();
        }
        return ipAddress;
    }


  /*  private Profile convertLoginDto(entity.User user, Token token) {
        Login loginDto = new Login();

        loginDto.setUserId(user.getUserId());
        loginDto.setToken(token.getToken());
        loginDto.setEmail(user.getEmail());

        loginDto.setFirstName(user.getFirstName());
        loginDto.setLastName(user.getLastName());

       *//* if(user.getFirstName()!=null){  }
      if(user.getLastName()!=null){

      }*//*

        if(user.getOffice()!=null){ loginDto.setOffice(user.getOffice().getCity());}

        if(user.getNickname()!=null){
            loginDto.setNickname(user.getNickname());
        }

        if (user.getBio() != null) {
            loginDto.setBio(user.getBio());
        }

      if(user.getPhoto()!=null){

        loginDto.setPhoto(user.getPhoto());
      }

        loginDto.setContestManager(user.isContestManager());
        loginDto.setOpenProfile(user.isOpenProfile());
        loginDto.setFillInfo(user.isFillInfo());

        return loginDto;
    }*/

    // mascara a password introduzida
    private String passMask(String password) {

        return DigestUtils.md5Hex(password).toUpperCase();
    }

    // mascara o token do user
    private String tokenMask(String token) {

        return DigestUtils.md5Hex(token).toUpperCase();
    }

    /**
     * Verifies if given string has some valid information
     * @param str represents string to be evaluated
     * @return true if string has no valid information
     */
    public boolean checkStringInfo(String str) {
        // check if a string info is null or blank
        boolean res = false;

        if (str == null || str.isBlank() || str.isEmpty()) {
            res = true;
            // info is not filled in as it should
        }

        return res;
    }

    /**
     * Removes token that identifies unique session from database, when user logs out of its account
     * @param token represents token that identifies unique session
     * @return status code 200 when token is removed from database, status code 400 if no session is found for given token and status code 403 when session exists but no user is associated with it
     */
    public int validateLogout(String token) {
        // remove token from DB when logout
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


    public boolean checkDataToRegister(String email, String password) {
        // check if new regist has mandatory camps filled in from frontend
        boolean res = false;

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            res = true;

        }

        return res;
    }

    /**
     * Verifies if email inserted when creating a new account already exists in database, given that email must be a unique attribute
     * Only if email does not exist proceeds to create a new account
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
     * @param email represents email associated with new account
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

    public boolean verifyTimestampOfToken(entity.User userEnt, String token) {
        // Check if link is valid
        // o token é válido por 1hr, se tiver passado mais tempo não é possível continuar o pedido
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
     * @param tokenToRecoverPass is a unique token that represents account that asked to recover password
     * @param newPassword represents the new password that should be persisted in database, after being masked
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
     * @param token identifies session that makes the request
     */
    public void updateSessionTime(String token) {
        // updates session time for given token

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
     * @param token identifies session that makes the request
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
     * @param token identifies session that makes the request
     * @param newInfo stores information (format: dto) that is sent from frontend
     * @return Profile information updated. Format is DTO
     */
    public Profile updateProfile(String token, Profile newInfo) {
        // update profile of logged user

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
     * @param token represents session of logged user that makes the request
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


    private Hobby convertToHobbyDto(entity.Hobby hobby) {
        Hobby hobbyDto = new Hobby();

        hobbyDto.setId(hobby.getHobbyId());
        hobbyDto.setTitle(hobby.getHobbyTitle());

        return hobbyDto;
    }


    public HashMap<Integer, String> getOfficeList() {
        HashMap<Integer, String> officeList = new HashMap<>();
        Office[] list = Office.values();

        for (int i = 0; i < list.length; i++) {
            officeList.put(i, list[i].getCity());
        }
        return officeList;
    }

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

    public Skill convertToSkillDto(entity.Skill skill) {
        Skill skillDto = new Skill();


        skillDto.setId(skill.getSkillId());
        skillDto.setTitle(skill.getTitle());
        skillDto.setSkillType(skill.getType().ordinal());

        return skillDto;
    }

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
     * @param skill represents skill information to be associated with given token
     * @return true if mandatory information is not available
     */
    public boolean checkSkillInfo(Skill skill) {
        // verifica se info obrigatória vem do frontend
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
     * @param token identifies session (and user) that makes the request
     * @param id identifies hobby to be removed from list of hobbies of user
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
     * @param token identifies session (and user) that makes the request
     * @param id identifies skill to be removed from list of skills of user
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


    public List<Skill> getSkillsList(String str, String token) {
        // retrieve list of skills that contain title
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

    public List<Hobby> getHobbiesList(String str, String token) {
        // retrieves list of hobbies that match string used to search DB

        List<Hobby> listHobbiesDto = new ArrayList<>();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            List<entity.Hobby> list = hobbyDao.findHobbyListContainingStr(str.toLowerCase());

            if (list != null) {
                for (entity.Hobby h : list) {
                    Long count = hobbyDao.findRelationBetweenUserAndHobby(h.getHobbyId(), user.getUserId());
                    if (count == 0) {
                        // significa que não tem relação com hobby h podendo ser sugerido no frontend
                        listHobbiesDto.add(convertToHobbyDto(h));
                    }
                }
            }
        }
        return listHobbiesDto;
    }

    public boolean modifyProfileType(int role, int userId) {
        // método administrativo para postman. Altera perfil do user: 1 - gestor de concurso (perfil A) / 0 - user normal (perfil B)

        boolean res = false;

        entity.User user = userDao.findUserById(userId);
        if (user != null) {
            if (user.isValidated()) {
                // só altera se a conta estiver validada

                if (role == 0) {
                    //altera para perfil B - user normal. Não precisa de validar nada, apenas mudar o atributo contestManager


                    user.setContestManager(false);
                    userDao.merge(user);
                    res = true;

                } else if (role == 1) {
                    // altera para perfil A - gestor de concursos. Precisa de várias verificações:
                    // sair de projecto activo, se o tiver. Se n for o único gestor
                    // sair de tarefas onde seja responsável e atribuir outro membro como responsável da tarefa. Se não houver outro membro nunca poderá ter perfil alterado
                    //TODO  retirar notificações que digam respeito a coisas do projecto e que precisem de input ou retirar a necessidade de input
                    // colocar perfil a privado

                    entity.ProjectMember pm = projMemberDao.findActiveProjectMemberByUserId(userId);
                    if (pm != null) {
                        // lida com as pendências do projecto activo
                        System.out.println("ID proj member " + pm.getId());
                        if (projBean.hasEnoughManagers(pm.getProjectToParticipate().getId(), userId)) {
                            // projecto fica com gestor depois de pessoa sair. Senao não pode ser alterado

                  // se projecto estiver no estado finished ou cancelled pode sair sem problema, senão tem de verificar tarefas
                            if (pm.getProjectToParticipate().getStatus() == StatusProject.CANCELLED || pm.getProjectToParticipate().getStatus() == StatusProject.FINISHED) {
                                // TODO se estiver cancelado pode ter tarefas em seu nome, conforme tenha ou n concurso.
                                // TODO se proj cancelado for reinsituído, terá de ter atenção a membros do projecto que tenham outro proj activo
                                user.setContestManager(true);
                                    userDao.merge(user);
                                    res = true;
                            } else {
                                // preciso verificar se user tem tarefas à sua responsabilidade cujo estado n seja finished antes de sair. Se tiver é preciso mudar essa info
                               boolean canLeave= projBean.dealWithTasksBeforeLeavingProject(userId, pm.getProjectToParticipate());

                               if(canLeave){
                                refusePendingInvitations(userId);
                                   // tem de retirar convites pendentes para participar noutros projectos, se os houver

                                   user.setContestManager(true);
                                   userDao.merge(user);
                                   res = true;
                               }
                            }
                        }
                    }   else {
                        // não tem projecto activo, pode simplesmente mudar o atributo contestManager
                        refusePendingInvitations(userId); // recusa convites pendentes para outros projectos
                        user.setContestManager(true);
                        userDao.merge(user);
                        res = true;
                    }
                }
            }
        }

        return res;
    }

    private void refusePendingInvitations(int userId) {
        // recusa convites pendentes para participar em projectos
        List<entity.ProjectMember> listPotentialpm = projMemberDao.findListOfPotentialMembersByUserId(userId);
        if (listPotentialpm != null) {
            for (entity.ProjectMember p : listPotentialpm) {
                p.setAnswered(true);
                projMemberDao.merge(p);
            }}
    }

    /**
     * Get list of all users that have a validated account
     * @param token identifies session that makes the request
     * @return list of UserInfo - a DTO that displays minimum necessary information to display in frontend
     */
    public List<UserInfo> getAllUsers(String token) {
        // lista de todos os users com conta válida na app

        List<UserInfo> list = new ArrayList<>();

        List<entity.User> allUsers = userDao.findAllUsersWithValidatedAccount();
        if (allUsers!=null){
            for (entity.User u : allUsers){
               list.add( convertUserEntToMinimalDto(u));

            }
        }

        return list;
    }

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
     * Get list of users to suggest when searching app users
     * Users must have a validated account and name or nickname must contain input inserted by user in frontend
     * Removes from list of suggestions token's own account
     * @param token identifies session that makes the request
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
     * @param token identifies session that makes the request
     * @param userId identifies the user, whose profile information is being requested to be displayed in frontend
     * @return AnotherProfile DTO, that contains information of users profile, its skills and hobbies and minimum necessary information of its projects
     */
    public AnotherProfile getAnotherProfile(String token, int userId) {

        AnotherProfile profile = new AnotherProfile();
        entity.User user = userDao.findUserById(userId);
        if(user!=null) {
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

            if(user.getListSkills()!=null){
                profile.setSkills(projBean.convertListSkillsDTO(user.getListSkills()));
            }

            if(user.getListHobbies()!=null){
                profile.setHobbies(convertListHobbiesToDto(user.getListHobbies()));
            }

            if(getListOfProjectsOfGivenUser(userId)){
                // user tem projectos associados ao seu perfil
            profile.setProjects(getUserProjects(userId));
            }
        }
    return profile;
    }

    private List<ProjectMinimal> getUserProjects(int userId) {
        // obtém lista de projectos de dado user
List<ProjectMinimal> list = new ArrayList<>();

        List<entity.Project> listEnt = projMemberDao.findListOfProjectsByUserId(userId);
if(listEnt!=null){
    for (entity.Project p : listEnt){
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

    private boolean getListOfProjectsOfGivenUser(int userId) {
        // verifica se user tem lista de projectos (active and not removed)
     boolean res=false;
        List<entity.Project> list = projMemberDao.findListOfProjectsByUserId(userId);
   if(list!=null){
       res=true;
   }
   return res;
    }

    private List<Hobby> convertListHobbiesToDto(List<entity.Hobby> listHobbies) {
List<Hobby> listDtos = new ArrayList<>();

for (entity.Hobby h : listHobbies){
    listDtos.add(convertToHobbyDto(h));
}
return listDtos;
    }

    /**
     * Verifies if user has open profile, so that its profile can be visited by other app users
     * @param id identifies the user, whose profile information is being requested to be displayed in frontend
     * @return true if profile can be viewed by other app users
     */
    public boolean checkUserHasOpenProfile(int id) {
        //verifica se userId tem perfil aberto e a sua conta está validada, para que possa ser visitado por outros users

        boolean res= false;
        entity.User user = userDao.findUserById(id);
        if(user!=null){
            if(user.isOpenProfile()){
            res=true;
        }}

        return res;
    }
/*
    public ActiveProjectToken getActiveProjectInfo(String token) {
        // permite saber se token / use logado tem projecto activo e qual o seu Id para renderizar botões no frontend em função desta informação

        ActiveProjectToken dto = new ActiveProjectToken();

        entity.User user = tokenDao.findUserEntByToken(token);
        if(user!=null){

            entity.Project proj = projMemberDao.findActiveProjectByUserId(user.getUserId());

            if(proj!=null){
                dto.setHasActiveProject(true);
                dto.setActiveProjectId(proj.getId());
            } else {
                dto.setHasActiveProject(false);
                dto.setActiveProjectId(0);
            }
        }

        return dto;
    }
*/

    /**
     * Gets information on current active project token might have
     * @param token identifies session that makes the request
     * @return Project DTO with complete information of project and user relationship with given project (if it is a manager and/ or member)
     */
    public Project getActiveProjectInfo(String token) {

        Project dto = new Project();

        entity.User user = tokenDao.findUserEntByToken(token);
        if(user!=null){

            entity.Project proj = projMemberDao.findActiveProjectByUserId(user.getUserId());

            if(proj!=null){
               dto=projBean.convertProjEntityToDto(proj);
            }
        }

        return dto;
    }
}

