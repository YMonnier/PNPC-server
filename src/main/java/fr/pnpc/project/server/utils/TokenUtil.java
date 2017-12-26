package fr.pnpc.project.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import fr.pnpc.project.models.model.User;

import java.io.UnsupportedEncodingException;

/**
 * Created by stephen on 14/10/17.
 */
public class TokenUtil {
    public static String generate(User user) throws UnsupportedEncodingException {
        return JWT.create()
                .withIssuer(user.getId() + user.getEmail())
                .sign(Algorithm.HMAC256(user.getPassword() + user.getId()));
    }
}
