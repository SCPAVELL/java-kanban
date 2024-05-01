package task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

	private final List<Integer> subtaskIds = new ArrayList<>();
	private Instant endTime;

	public Epic(String description, String name, TaskStatus status) {
		super(description, name, status);
	}

	public Epic(String description, String name, TaskStatus status, Instant startTime, long duration) {
		super(description, name, status, startTime, duration);
		this.endTime = super.getEndTime();

	}

	public List<Integer> getSubtaskIds() {
		return subtaskIds;
	}

	public void setSubtaskIds(int id) {
		subtaskIds.add(id);
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		Epic epic = (Epic) o;
		return Objects.equals(subtaskIds, epic.subtaskIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), subtaskIds);
	}

	@Override
	public String toString() {
		return "Epic{" + "subtaskIds=" + subtaskIds + ", description='" + getDescription() + '\'' + ", id=" + getId()
				+ ", name='" + getName() + '\'' + ", status=" + getStatus() + '\'' + ", startTime='"
				+ getStartTime().toEpochMilli() + '\'' + ", endTime='" + getEndTime().toEpochMilli() + '\''
				+ ", duration='" + getDuration() + '}';

	}
}
