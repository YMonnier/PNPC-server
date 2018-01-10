package fr.pnpc.project.server.backingbean;

import fr.pnpc.project.models.ejb.UserManager;
import fr.pnpc.project.models.exceptions.*;
import fr.pnpc.project.models.model.User;


import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;

    @Inject
    UserManager userManager;

    public LoginBean(){
        this.user = new User();
    }

    public String register(){
        try {
            userManager.register(user);
        } catch (ObjectNotValidException e) {
            e.printStackTrace();
        }

        return "welcome";
    }

    public String login(){
        try {
            userManager.login(user.getEmail(), user.getMdp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "welcome";
    }

    public User getUser() {
        return user;
    }
}
