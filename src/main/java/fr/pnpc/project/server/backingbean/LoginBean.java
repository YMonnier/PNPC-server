package fr.pnpc.project.server.backingbean;

import fr.pnpc.project.models.ejb.UserManager;
import fr.pnpc.project.models.exceptions.ObjectNotValidException;
import fr.pnpc.project.models.model.User;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
/**
 * LoginBean from backingbean package. Manage the Login view as controller.
 */
public class LoginBean implements Serializable {
    /**
     * A Logger object to log messages.
     */
    private final static Logger LOGGER = Logger.getLogger(LoginBean.class.getSimpleName());

    private static final long serialVersionUID = 1L;

    /**
     * A User object.
     */
    private User user;

    /**
     * A UserManager Object to manage user type. (CRUD).
     */
    @Inject
    UserManager userManager;

    /**
     * Default LoginBean constructor. Necessary for `JEE`.
     */
    public LoginBean() {
        this.user = new User();
    }

    /**
     * Register a new User to the system.
     *
     * @return A message
     */
    public String register() {
        try {
            userManager.register(user);
        } catch (ObjectNotValidException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage());
        }

        return "welcome";
    }

    /**
     * Login to the system by checking the nickname and the password.
     *
     * @return A message.
     */
    public String login() {
        try {
            userManager.login(user.getNickname(), user.getMdp());
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage());
        }
        return "welcome";
    }

    /**
     * Returns a user.
     * @return A User object.
     */
    public User getUser() {
        return user;
    }

    /**
     * Set a user
     * @param user, new user.
     */
    public void setUser(User user) {
        this.user = user;
    }
}
