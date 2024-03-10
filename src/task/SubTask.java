package task;

import java.time.Instant;

public class SubTask extends Task {

	private final int epicId;

	public SubTask(String title, String description, TaskStatus status, TaskType type, int epicId) {
		super(title, description, status, type);
		this.epicId = epicId;
	}

	public int getEpicId() {
		return epicId;
	}

	public SubTask(String description, String name, TaskStatus status, int epicId, Instant startTime, long duration) {
		super(description, name, status, startTime, duration);
		this.epicId = epicId;
	}

	@Override
	public String toString() {
		return "Subtask [getId()=" + getId() + ", getType()=" + getType() + ", getTitle()=" + getTitle()
				+ ", getStatus()=" + getStatus() + ", getDescription()=" + getDescription() + ", epicId=" + epicId
				+ "]\n";

	}

}
