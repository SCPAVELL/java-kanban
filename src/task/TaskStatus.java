package task;

public enum TaskStatus {
	NEW("new"), IN_PROGRESS("inProgress"), DONE("done");

	private String translation;

	TaskStatus() {
	}

	TaskStatus(String translation) {
		this.translation = translation;
	}

	public String getTranslation() {
		return translation;
	}

	public String toString() {
		return translation;
	}
}
