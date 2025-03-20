package codehows.com.daehoint.excpetion;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException(String message) {
        super(message);
    }
}
