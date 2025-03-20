package codehows.com.daehoint.excpetion;

public class DuplicateException extends RuntimeException{
    public DuplicateException(String log){
        super(log);
    }

    public DuplicateException(){
    }
}
