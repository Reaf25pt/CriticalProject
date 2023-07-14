package bean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dto.Profile;
import entity.Token;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {
    User userBean;
    dao.User userDao;
    dao.Token tokenDao;
    Project projBean;

    @BeforeEach
    void setup() {
        userBean = new User();
        userDao = mock(dao.User.class);
        tokenDao = mock(dao.Token.class);
        userBean.setUserDao(userDao);
        userBean.setTokenDao(tokenDao);
        projBean=new Project();
    }

    @AfterEach
    void clean() {
        userBean = null;
        userDao = null;
        tokenDao = null;
        projBean=null;
    }

    @Test
    public void testValidateLoginInfoSuccess() {
        String email = "aor@aor.pt";
        String password = "25D55AD283AA400AF464C76D713C07AD";
        assertTrue(userBean.validateLoginInfo(email, password));
    }

    @Test
    public void testValidateLoginInfoFail() {
        String email = "";
        String password = "25D55AD283AA400AF464C76D713C07AD";
        assertFalse(userBean.validateLoginInfo(email, password));
    }

    @Test
    public void testCheckStringInfoSuccess(){
        String str= "test";
        assertFalse(userBean.checkStringInfo(str));

    }

    @Test
    public void testCheckStringInfoFail(){
        String str= "";
        assertTrue(userBean.checkStringInfo(str));

    }
    @Test
    public void testCheckEmailInDatabaseSuccessForNull() {
        String email = "aor@aor.pt";
        entity.User user = null;
        userDao.persist(user);
        int result = userBean.checkEmailInDatabase(email);
        assertEquals(100, result);
    }




}
