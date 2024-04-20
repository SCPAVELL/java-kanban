package manager;

//выводить сообщение об ошибке
public class ManagerException extends RuntimeException {

	public ManagerException(String message) {
		super(message);
	}

}
