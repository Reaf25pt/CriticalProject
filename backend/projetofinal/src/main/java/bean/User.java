package bean;

import dto.Login;
import entity.Token;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import org.apache.commons.codec.digest.DigestUtils;

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
}

