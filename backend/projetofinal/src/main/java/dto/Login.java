package dto;

import entity.Office;

public class Login {
    private int userId;
// identifies the session
    private String token;

    private String email;
    private String firstName;
    private String lastName;
    private Office office;
    private String nickname;
    private String photo;
    private String bio;
    private boolean contestManager;
    private boolean openProfile=false;

    public Login(){

    }
}
