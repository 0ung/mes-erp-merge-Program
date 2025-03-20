package codehows.com.daehoint.excpetion;

public class NotFoundItemException extends RuntimeException{
	public NotFoundItemException(){

	}
	public NotFoundItemException(String message){
		super(message);
	}
}
