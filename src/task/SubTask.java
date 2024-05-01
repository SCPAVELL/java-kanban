package task;

import java.time.Instant;
import java.util.Objects;

public class SubTask extends Task {

	private final int epicId;

	public SubTask(String description, String name, TaskStatus status, int epicId) {
		super(description, name, status);
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
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		SubTask subtask = (SubTask) o;
		return epicId == subtask.epicId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), epicId);
	}

	@Override
	public String toString() {
		return "Subtask{" + "epicId=" + getEpicId() + ", description='" + getDescription() + '\'' + ", id=" + getId()
				+ ", name='" + getName() + '\'' + ", status=" + getStatus() + '\'' + ", startTime='"
				+ getStartTime().toEpochMilli() + '\'' + ", endTime='" + getEndTime().toEpochMilli() + '\''
				+ ", duration='" + getDuration() + '}';
	}

}
