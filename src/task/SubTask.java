package task;

public class SubTask extends Task {

	private final int epicId;

	public SubTask(String title, String description, TaskStatus status, TaskType type, int epicId) {
		super(title, description, status, type);
		this.epicId = epicId;
	}

	public int getEpicId() {
		return epicId;
	}

	@Override
	public String toString() {
		return "Subtask [getId()=" + getId() + ", getType()=" + getType() + ", getTitle()=" + getTitle()
				+ ", getStatus()=" + getStatus() + ", getDescription()=" + getDescription() + ", epicId=" + epicId
				+ "]\n";

	}

}
