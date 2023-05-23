package bean;

import dto.Login;
import dto.EditProfile;
import dto.NewAccount;
import entity.Token;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jaxb.core.v2.TODO;

import java.io.Serializable;

@RequestScoped
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @EJB
    dao.User userDao;
    @EJB
    dao.Token tokenDao;

    public User(){}


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

    public Login validateLogin(String email, String password){
        // creates session for respective email if data matches some regist in User table

        Login user = null;

        entity.User userEnt = userDao.findUserByEmail(email);

        if (userEnt != null){
            if (userEnt.isValidated()){
                String maskPasswordFromLogin = passMask(password);
                if(userEnt.getPassword().equals(maskPasswordFromLogin)){

                    Token tokenEnt = new Token();
                    String maskToken = tokenMask(tokenEnt.createToken(email));
                    tokenEnt.setToken(maskToken);
                    tokenEnt.setTokenOwner(userEnt);
                    tokenEnt.setTimeOut(tokenEnt.createTimeOutTimeStamp());

                    userDao.merge(userEnt);
                    tokenDao.merge(tokenEnt);
                    user = convertLoginDto(userEnt, tokenEnt);
                }
            }
        }

       return user;
    }


    private Login convertLoginDto(entity.User user, Token token){
        Login loginDto = new Login();

        loginDto.setUserId(user.getUserId());
        loginDto.setToken(token.getToken());
        loginDto.setEmail(user.getEmail());
        loginDto.setFirstName(user.getFirstName());
        loginDto.setLastName(user.getLastName());
        loginDto.setOffice(user.getOffice());
        loginDto.setNickname(user.getNickname());
        loginDto.setPhoto(user.getPhoto());
        loginDto.setBio(user.getBio());
        loginDto.setContestManager(user.isContestManager());
        loginDto.setOpenProfile(user.isOpenProfile());

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

    public int validateLogout(String token) {
        // remove token from DB when logout
        int res;

        Token tokenEnt = tokenDao.findTokenEntByToken(token);

        if (tokenEnt != null) {
            entity.User userEnt = tokenEnt.getTokenOwner();

            if (userEnt != null) {

                tokenDao.remove(tokenEnt);
 //               logger.info("UserId " + userEnt.getUserId() + " logs out of its account");

                res = 200;
            } else
                res = 400; // sem sessão iniciada
        } else
            res = 403; // sem user associado ao token

        return res;
    }


    public boolean checkDataToRegister(NewAccount user, String password) {
        // check if new regist has mandatory camps filled in from frontend
        boolean res = false;

        if (user.getEmail() == null || password == null || user.getFirstName()==null || user.getLastName() == null || user.getOffice() == null) {
            res = true;

        } else if (user.getEmail().isBlank() || password.isBlank() || user.getFirstName().isBlank() || user.getLastName().isBlank()) {
            res = true;
        }

        return res;
    }

    public int checkEmailInDatabase(String email) {
        // check if email used to create new account is already in Database - Email must be unique
        // if it is in Database, check if account is validated (could be that user forgot previous regist and never validated account
        int res=401;

        if (email != null || !email.isBlank()){
            entity.User user = userDao.findUserByEmail(email);

            if (user!= null){
                if (user.isValidated()){
                    // email is registered in DB and account is validated
                    res=400;
                }
               else if (!user.isValidated()){
                    // email is registered in DB but account is not validated
                    // TODO send new email to remind validation account, with new token and timestamp
                    res=409;
                }
            }
        }
return res;
    }


    public boolean createNewAccount(NewAccount account, String password) {
        //Create new account and send email to ask for account validation

        boolean res=false;

        if(account!= null ) {
            entity.User newUser = convertToUserEntity(account);
            newUser.setPassword(passMask(password));
            newUser.setContestManager(false);
            newUser.setOpenProfile(false);
            newUser.setValidated(false);
            newUser.setToken(newUser.createTokenForActivationOrRecoverPassword());
            newUser.setTimestampForToken(newUser.createTimeoutTimeStamp());

            userDao.persist(newUser);
            res=true;
        }
        return res;
    }

    private entity.User convertToUserEntity(NewAccount account) {
        entity.User newUser = new entity.User();
        newUser.setEmail(account.getEmail());
        newUser.setFirstName(account.getFirstName());
        newUser.setLastName(account.getLastName());
        newUser.setOffice(account.getOffice());
        if (account.getNickname()!=null){
            newUser.setNickname(account.getNickname());
        }
        if(account.getPhoto()!=null){
            newUser.setPhoto(account.getPhoto());
        }
        return newUser;
    }
}

