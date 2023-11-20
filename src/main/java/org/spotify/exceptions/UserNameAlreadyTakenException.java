package org.spotify.exceptions;

public class UserNameAlreadyTakenException extends Throwable {
    public UserNameAlreadyTakenException(String message) {
        super(message);
    }
}
