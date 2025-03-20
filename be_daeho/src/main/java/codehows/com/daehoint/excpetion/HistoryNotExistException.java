package codehows.com.daehoint.excpetion;

public class HistoryNotExistException extends RuntimeException{
	public HistoryNotExistException(String log){
		super(log);
	}

	public HistoryNotExistException(){
	}
}
