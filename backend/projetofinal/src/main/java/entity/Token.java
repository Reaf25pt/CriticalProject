package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name="Token")
@NamedQuery(name = "Token.findTokenEntByToken", query = "SELECT t FROM Token t WHERE t.token = :token")
@NamedQuery(name = "Token.findUserEntByToken", query = "SELECT tokenOwner FROM Token t WHERE t.token = :token")
@NamedQuery(name = "Token.findTokenListByUserId", query = "SELECT t.token FROM Token t WHERE t.tokenOwner.userId = :userId")

public class Token implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="tokenId", nullable = false, unique = true, updatable = false)
    private int tokenId;

    @Column(name="token", nullable=true, unique = true, updatable = false)
    private String token;

    @Column (name="timeOut", nullable=true, unique = false, updatable = true )
    private long timeOut;

    @ManyToOne
    private User tokenOwner;

    public Token(){}

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public User getTokenOwner() {
        return tokenOwner;
    }

    public void setTokenOwner(User tokenOwner) {
        this.tokenOwner = tokenOwner;
    }


    // CRIA TOKEN
    public String createToken(String email) {

        long token = System.currentTimeMillis();
        String tokenString = email + token;
        this.token = tokenString;
        return this.token;
    }

    // ASSOCIA TIME OUT PARA A SESSAO CRIADA DE 5 / 50MIN
    public long createTimeOutTimeStamp() {
        final int timeOutSession = 1800000; // 30min
        //final int timeOutSession = 300000; // 5min

        long timestamp = Calendar.getInstance().getTimeInMillis() + timeOutSession;

        return timestamp;
    }

    // mascara o token do user
    public String tokenMask(String token) {

        return DigestUtils.md5Hex(token).toUpperCase();
    }


}
