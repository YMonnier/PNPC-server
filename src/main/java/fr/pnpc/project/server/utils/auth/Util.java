package fr.pnpc.project.server.utils.auth;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Util {
    /**
     * Defines a custom format for the stack trace as String.
     */
    public static String stackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
