package bean;

import ENUM.Office;
import ENUM.SkillType;
import dto.*;
import dto.Project;
import entity.ProjectMember;
import entity.Token;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import mail.AskRecoverPassword;
import mail.ValidateNewAccount;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jaxb.core.v2.TODO;
import org.jetbrains.annotations.NotNull;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

    public Login validateLogin(String email, String password) {
        // creates session for respective email if data matches some regist in User table

        Login user = null;

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
                    user = convertLoginDto(userEnt, tokenEnt);

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

    private Login convertLoginDto(entity.User user, Token token) {
        Login loginDto = new Login();

        loginDto.setUserId(user.getUserId());
        loginDto.setToken(token.getToken());
        loginDto.setEmail(user.getEmail());

        loginDto.setFirstName(user.getFirstName());
        loginDto.setLastName(user.getLastName());

       /* if(user.getFirstName()!=null){  }
      if(user.getLastName()!=null){

      }*/

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
    }

    // mascara a password introduzida
    private String passMask(String password) {

        return DigestUtils.md5Hex(password).toUpperCase();
    }

    // mascara o token do user
    private String tokenMask(String token) {

        return DigestUtils.md5Hex(token).toUpperCase();
    }

    public boolean checkStringInfo(String str) {
        // check if a string info is null or blank
        boolean res = false;

        if(str == null || str.isBlank() || str.isEmpty()){
            res=true;
            // info is not filled in as it should
        }

        return res;
    }

    public int validateLogout(String token) {
        // remove token from DB when logout
        int res;

        Token tokenEnt = tokenDao.findTokenEntByToken(token);

        if (tokenEnt != null) {
            entity.User userEnt = tokenEnt.getTokenOwner();

            if (userEnt != null) {

                tokenDao.remove(tokenEnt);
                //               logger.info("UserId " + userEnt.getUserId() + " logs out of its account");
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





    public int checkEmailInDatabase(String email) {
        // check if email used to create new account is already in Database - Email must be unique
        // if it is in Database, check if account is validated (could be that user forgot previous regist and never validated account
        int res = 100;

        if (email != null || !email.isBlank()) {
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
        } else {
            res = 401;
        }
        return res;
    }

    public boolean createNewAccount(String email, String password){
        //Create new account and send email to ask for account validation
        boolean res = false;
        entity.User newUser = new entity.User();
        newUser.setEmail(email);
        newUser.setPassword(passMask(password));
        /*newUser.setFirstName("nd");
        newUser.setLastName("nd");
        newUser.setOffice(Office.COIMBRA);
        // TODO  colocar assim para nomes e office ou permitir q seja nulo*/
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

/*
    public boolean createNewAccount(NewAccount account, String password) {
        //Create new account and send email to ask for account validation

        boolean res = false;

        if (account != null) {
            entity.User newUser = convertToUserEntity(account);
            newUser.setPassword(passMask(password));
            newUser.setContestManager(false);
            newUser.setOpenProfile(false);
            newUser.setValidated(false);
            newUser.setToken(newUser.createTokenForActivation());
            newUser.setTimestampForToken(newUser.createTimeoutTimeStamp());

            userDao.persist(newUser);
            ValidateNewAccount.main(newUser.getEmail(), newUser.getToken());
            res = true;
            LOGGER.info("A new account is created for email " + newUser.getEmail() + " User ID is " + newUser.getUserId() + " . IP Address of request is " + getIPAddress());

        }
        return res;
    }
*/
 /*   private entity.User convertToUserEntity(NewAccount account) {
        entity.User newUser = new entity.User();

        newUser.setEmail(account.getEmail());
        newUser.setFirstName(account.getFirstName());
        newUser.setLastName(account.getLastName());
        newUser.setOffice(account.getOffice());
        //TODO pode ser necessário mudar para ficar registo de nome de cidade e não de valor numérico
        if (account.getNickname() != null) {
            newUser.setNickname(account.getNickname());
        }
        if (account.getPhoto() != null) {
            newUser.setPhoto(account.getPhoto());
        }

        return newUser;
    }*/

    public int validateNewAccount(String tokenForValidation) {
        // user clicks in button to confirm / validate email used for new regist
        // needs to check if token exists for a given user and if link is still valid  (assuming 1hr)

        int res;

        entity.User userEnt = userDao.findUserByTokenForActivationOrRecoverPass(tokenForValidation);

        if (userEnt != null) {

            if (verifyTimestampOfToken(userEnt, tokenForValidation)) {

                userEnt.setValidated(true);
                userEnt.setToken(null);

                long OL = 0;
                userEnt.setTimestampForToken(OL);

                userDao.merge(userEnt);
                // logger.info("Account of userId " + uEnt.getUserId() + " is activated");
//TODO use 200 ou 202 - accepted
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


    public void reNewTokenToValidateAccount(entity.User user) {
        // token is no longer valid. Send a new email with a new token to validate account
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


    public boolean askToRecoverPassword(String email) {
        // Ask to recover password and send result to email if it exists in DB and account is valid
//TODO should we try to retrieve from DB user with given email AND validated=true ?
        boolean res = false;

        entity.User userEnt = userDao.findUserByEmail(email);

        if (userEnt != null) {
            if (userEnt.isValidated()) { // só altera password se conta estiver válida
                String token = userEnt.createTokenToRecoverPassword();
                userEnt.setToken(token);
                userEnt.setTimestampForToken(userEnt.createTimeoutTimeStamp());
                AskRecoverPassword.main(userEnt.getEmail(), token);

                userDao.merge(userEnt);
                //logger.info("Email to recover password of userId " + uEnt.getUserId() + " is sent to email " + email);

                res = true;
                LOGGER.info("User ID " + userEnt.getUserId() + " ask to recover password. IP Address of request is " + getIPAddress());

            }
        }

        return res;

    }

    public int newPasswordViaLink(String tokenToRecoverPass, String newPassword) {
        // change password through link. If token is no longer valid send a new link by email

        int res;

        entity.User userEnt = userDao.findUserByTokenForActivationOrRecoverPass(tokenToRecoverPass);

        if (userEnt != null) {

            if (verifyTimestampOfToken(userEnt, tokenToRecoverPass)) {

                userEnt.setPassword(passMask(newPassword));
                userEnt.setToken(null);

                long OL = 0;
                userEnt.setTimestampForToken(OL);

                userDao.merge(userEnt);
                // logger.info("Password of userId " + uEnt.getUserId() + " is modified through recover password email");

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


    public boolean checkUserPermission(String token) {
        // check if token has a valid session and user account is valid (check in another method)

        boolean res = false;

        Token tokenEnt = tokenDao.findTokenEntByToken(token);

        if (tokenEnt != null) {
            if (checkUserAccount(tokenEnt.getTokenOwner())) {
                res = true;
            }
        }

        return res;
    }

    private boolean checkUserAccount(entity.User user) {
        // check if user has a valid account
        boolean res = false;

        if (user != null) {
            if (user.isValidated()) {
                res = true;
            }
        }

        return res;
    }

    public void updateSessionTime(String token) {
        // updates session time for given token

        Token tokenEnt = tokenDao.findTokenEntByToken(token);

        if (tokenEnt != null) {
            long newSessionTimeOut = tokenEnt.createTimeOutTimeStamp();
            tokenEnt.setTimeOut(newSessionTimeOut);
            tokenDao.merge(tokenEnt);
        }
    }

    public EditProfile addMandatoryInfo(String token, EditProfile newInfo){
        // adicionar dados obrigatórios sem a qual n pode avançar na app
        EditProfile updatedUser = null;

        if (newInfo!=null){
            entity.User user = tokenDao.findUserEntByToken(token);

            if (user!= null){
                user.setFirstName(newInfo.getFirstName());
                user.setLastName(newInfo.getLastName());


                if (newInfo.getPhoto() != null) {
                    user.setPhoto(newInfo.getPhoto());
                }
                // TODO add bio, nickname ?!
                // TODO verify if link is image

                int office = newInfo.getOfficeInfo();
                //TODO is it correct?

                switch (office) {
                    case 0:
                        user.setOffice(Office.LISBOA);
                        break;
                    case 1:
                        user.setOffice(Office.COIMBRA);
                        break;
                    case 2:
                        user.setOffice(Office.PORTO);
                        break;
                    case 3:
                        user.setOffice(Office.TOMAR);
                        break;
                    case 4:
                        user.setOffice(Office.VISEU);
                        break;
                    case 5:
                        user.setOffice(Office.VILAREAL);
                        break;
                }

                user.setFillInfo(true);
            }

            userDao.merge(user);
            //TODO faz sentido ir buscar novamente à DB o user ou converter directamente o userEnt?

            LOGGER.info("User ID " + user.getUserId() + " updates its profile. IP Address of request is " + getIPAddress());

            updatedUser = convertToEditProfile(user, token);
        }

        return updatedUser;
    }



    public EditProfile updateProfile(String token, EditProfile newInfo) {
        // update profile of logged user

        EditProfile updatedUser = null;

        if (newInfo != null) {   // TODO ainda que esta verificação tenha sido feita anteriormente, no endpoint, é preciso repetir?
           /* Token tokenEnt = tokenDao.findTokenEntByToken(token);

            if (tokenEnt != null) {
                entity.User userEnt = tokenEnt.getTokenOwner();*/

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
                    //TODO se editar noutro lado, colocar noutro lado / mudar DTO
                }
                System.out.println(newInfo.isOpenProfile());

                userEnt.setOpenProfile(newInfo.isOpenProfile());

                int office = newInfo.getOfficeInfo();
                //TODO is it correct?

                switch (office) {
                    case 0:
                        userEnt.setOffice(Office.LISBOA);
                        break;
                    case 1:
                        userEnt.setOffice(Office.COIMBRA);
                        break;
                    case 2:
                        userEnt.setOffice(Office.PORTO);
                        break;
                    case 3:
                        userEnt.setOffice(Office.TOMAR);
                        break;
                    case 4:
                        userEnt.setOffice(Office.VISEU);
                        break;
                    case 5:
                        userEnt.setOffice(Office.VILAREAL);
                        break;
                }
            }
            userDao.merge(userEnt);
            //TODO faz sentido ir buscar novamente à DB o user ou converter directamente o userEnt?

            LOGGER.info("User ID " + userEnt.getUserId() + " updates its profile. IP Address of request is " + getIPAddress());

            updatedUser = convertToEditProfile(userEnt, token);
        }

        return updatedUser;
    }

    public EditProfile convertToEditProfile(entity.User user, String token) {
        EditProfile userDto = new EditProfile();

        userDto.setId(user.getUserId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());

        userDto.setToken(token);
        userDto.setEmail(user.getEmail());

        if(user.getOffice()!=null){
           userDto.setOffice( user.getOffice().getCity());
        }

      //  userDto.setOffice(user.getOffice());
        userDto.setNickname(user.getNickname());
        userDto.setPhoto(user.getPhoto());
        userDto.setBio(user.getBio());
        userDto.setOpenProfile(user.isOpenProfile());
        userDto.setFillInfo(user.isFillInfo());

        return userDto;
    }

    public int changeOwnPassword(String token, String oldPassword, String newPassword) {
        // change own password (logged user)

        int res;
// TODO validar no frontend apenas a força da password ou no backend tb?
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


 /*   public List<Project> getOwnProjectsList(String token) {
        // get list of projects where user of given token participates or participated (not removed!)

        List<Project> projectsList = new ArrayList<Project>();

        List<ProjectMember> allProjectMembersInfo = projMemberDao.findAll();

        if (allProjectMembersInfo!= null){
            entity.User user = tokenDao.findUserEntByToken(token);

            if (user!=null){
                for (ProjectMember p : allProjectMembersInfo){
                    if(p.getUserInvited().getUserId() == user.getUserId()){
                        if (p.isAccepted() && !p.isRemoved()){
                            projectsList.add( projBean.convertProjEntityToDto(p.getProjectToParticipate()));
                        }
                    }
                }
            }
        }

return projectsList;
    }*/

    public List<Project> getOwnProjectsList(String token) {

        List<Project> projectsList = new ArrayList<>();
        entity.User user = tokenDao.findUserEntByToken(token);

        List<entity.Project> list = projMemberDao.findListOfProjectsByUserId(user.getUserId());

        if(list!=null){
        for (entity.Project p : list) {
            projectsList.add(projBean.convertProjEntityToDto(p));

        }}
//TODO confirmar que está certo, e proteger de nulos !!!
        return projectsList;
    }

    public List<Hobby> getOwnHobbiesList(String token) {
List<Hobby> hobbiesList = new ArrayList<>();
        entity.User user = tokenDao.findUserEntByToken(token);

        List<entity.Hobby> list = hobbyDao.findListOfHobbiesByUserId(user.getUserId());

        for (entity.Hobby h : list) {
            hobbiesList.add(convertToHobbyDto(h));

        }
//TODO confirmar que está certo, e proteger de nulos !!!
        return hobbiesList;
    }

    public Hobby addHobby(String token, String title) {
        // adicionar hobby a DB, se não existir, e à lista de hobbies do token

        Hobby hobbyDto = new Hobby();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Hobby hobby = hobbyDao.findHobbyByTitle(title.trim());
            if (hobby != null) {
                // significa que hobby já está na DB, 1º verificar se já existe relação hobby-user para, n havendo, adicionar a lista de user

                Long res=hobbyDao.findRelationBetweenUserAndHobby(hobby.getHobbyId(), user.getUserId());
                System.out.println(res);
                if(res==0) {
                    user.getListHobbies().add(hobby);
                    hobby.getListUsers_Hobbies().add(user);

                    userDao.merge(user);
                    hobbyDao.merge(hobby);

                    hobbyDto = convertToHobbyDto(hobby);
                    LOGGER.info("Hobby " + hobby.getHobbyId() + " is associated with user, user ID: " + user.getUserId() + ". IP Address of request is " + getIPAddress());
                } else {
                    // TODO n faz nada, mas deveria avisar ou passa assim ?!
hobbyDto=null;
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

for (int i = 0; i< list.length; i++){
    officeList.put(i, list[i].getCity());
}
        return officeList;
    }

    public HashMap<Integer, String> getSkillTypesList() {
        HashMap<Integer, String> skillTypesList = new HashMap<>();
        SkillType[] list = SkillType.values();

        for (int i = 0; i< list.length; i++){
            skillTypesList.put(i, list[i].getType());
        }
        return skillTypesList;
    }

    public Skill addSkillToOwnProfile(String token, Skill skill) {
        // adicionar skill ao perfil do token,se n houver já relação user-skill. Se n existir deve-se 1º persistir na DB

        Skill skillDto = new Skill();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Skill skillInDB = skillDao.findSkillByTitle(skill.getTitle().trim());
            if (skillInDB != null) {
                // significa que skill já está na DB, 1º verificar se já existe relação skill-user para, n havendo, adicionar a lista de user

                Long res=skillDao.findRelationBetweenUserAndSkill(skillInDB.getSkillId(), user.getUserId());

                // TODO verificar situação do enum que vem do frontend ou assumir q será o q ja está na DB ?
                System.out.println(skillInDB.getSkillId() +"   " + user.getUserId());
                if(res==0) {

                    user.getListSkills().add(skillInDB);
                    skillInDB.getListUsers_Skills().add(user);

                    userDao.merge(user);
                    skillDao.merge(skillInDB);

                    skillDto = convertToSkillDto(skillInDB);
                    LOGGER.info("Skill " + skillInDB.getTitle() + " is associated with user, user ID: " + user.getUserId() + ". IP Address of request is " + getIPAddress());
                } else {
                    // TODO n faz nada, mas deveria avisar ou passa assim ?!
                    skillDto=null;
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

    }}

    public boolean checkSkillInfo(Skill skill) {
        // verifica se info obrigatória vem do frontend
        boolean res = false;

        if (skill==null){
            res=true;
        } else {
            if(checkStringInfo(skill.getTitle())){
                // TODO falta validar info do skillType ENUM que tem de vir preenchido
                // falta info obrigatória
                res=true;
            }
        }

     return res;
    }


    public List<Skill> getOwnSkillsList(String token) {

        List<Skill> skillsList = new ArrayList<>();
        entity.User user = tokenDao.findUserEntByToken(token);

        List<entity.Skill> list = skillDao.findListOfSkillsByUserId(user.getUserId());
if(list!=null){
        for (entity.Skill s : list) {
            skillsList.add(convertToSkillDto(s));
        }
        }
//TODO confirmar que está certo, e proteger de nulos !!!
        return skillsList;
    }


    public boolean deleteHobby (String token, int id){
        // apaga hobby da lista de hobbies do user se existir na sua lista
        boolean res=false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Hobby hobby = hobbyDao.findHobbyOfUserById(user.getUserId(), id);

            if(hobby!=null){
                // apagar apenas da lista do user

            hobby.getListUsers_Hobbies().remove(user);
             user.getListHobbies().remove(hobby);

                userDao.merge(user);
                hobbyDao.merge(hobby);

                res=true;
            }
        }
        return res;
    }

    public boolean deleteSkill (String token, int id){
        // apaga skill da lista de skills do user se existir na sua lista
        boolean res=false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Skill skill = skillDao.findSkillOfUserById(user.getUserId(), id);

            if(skill!=null){
                // apagar apenas da lista do user

                skill.getListUsers_Skills().remove(user);
                user.getListSkills().remove(skill);

                userDao.merge(user);
                skillDao.merge(skill);

                res=true;
            }
        }
        return res;
    }


    public List<Skill> getSkillsList(String str, String token) {
        // retrieve list of skills that contain title
        List<Skill> listSkillDto= new ArrayList<>();

        entity.User user=tokenDao.findUserEntByToken(token);
        if(user!=null){
        List<entity.Skill> list = skillDao.findSkillListContainingStr(str.toLowerCase());

        if(list!=null){
            for (entity.Skill s : list) {
                Long count = skillDao.findRelationBetweenUserAndSkill(s.getSkillId(), user.getUserId());

                if(count==0) {
                    listSkillDto.add(convertToSkillDto(s));
                }}}}return listSkillDto;
    }

    public List<Hobby> getHobbiesList(String str, String token) {
        // retrieves list of hobbies that match string used to search DB

        List<Hobby> listHobbiesDto = new ArrayList<>();

        entity.User user=tokenDao.findUserEntByToken(token);
        if(user!=null){
        List<entity.Hobby> list = hobbyDao.findHobbyListContainingStr(str.toLowerCase());

        if(list!=null){
            for(entity.Hobby h:list){
                Long count = hobbyDao.findRelationBetweenUserAndHobby(h.getHobbyId(), user.getUserId());
                if (count== 0){
                    // significa que não tem relação com hobby h podendo ser sugerido no frontend
                listHobbiesDto.add(convertToHobbyDto(h));
            }}
        }}
return listHobbiesDto;
    }
}

