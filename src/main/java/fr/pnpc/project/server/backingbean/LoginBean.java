package fr.pnpc.project.server.backingbean;

import fr.pnpc.project.models.ejb.UserManager;
import fr.pnpc.project.models.exceptions.ObjectNotValidException;
import fr.pnpc.project.models.model.User;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

@Named
@ViewScoped
public class LoginBean implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(LoginBean.class.getSimpleName());

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
            LOGGER.info(e.getLocalizedMessage());
        }

        return "welcome";
    }

    public String login(){
        try {
            userManager.login(user.getNickname(), user.getMdp());
        } catch (Exception e) {
            LOGGER.info(e.getLocalizedMessage());
        }
        return "welcome";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
