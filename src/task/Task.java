package task;

import java.time.Instant;
import java.util.Objects;

public class Task {

	private final String title; // Название
	private final String description; // Описание
	private int id; // Уникальный идентификационный номер задачи
	private TaskStatus status; // Статус
	private final TaskType type; // Подзадачи
	private Instant startTime;
	private long duration;

	public Task(String title, String description, TaskStatus status, TaskType type) {
		this.title = title;
		this.description = description;
		this.status = status;
		this.type = type;
	}

	public Task(String title, String description, TaskStatus status, Instant startTime, long duration) {
		this.title = title;
		this.description = description;
		this.status = status;
		this.startTime = startTime;
		this.duration = duration;
		this.type = null;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TaskType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Task [title=" + title + ", description=" + description + ", id=" + id + ", status=" + status + ", type="
				+ type + "]\n";
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, status, title, type);
	}

	public Instant getEndTime() {
		long SecondsInMinune = 60L;
		return startTime.plusSeconds(duration * SecondsInMinune);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		return Objects.equals(description, other.description) && id == other.id && status == other.status
				&& Objects.equals(title, other.title) && type == other.type;
	}

}
