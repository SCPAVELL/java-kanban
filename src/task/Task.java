package task;

import java.time.Instant;
import java.util.Objects;

public class Task {

	private String name; // Название
	private String description; // Описание
	private int id; // Уникальный идентификационный номер задачи
	private TaskStatus status; // Статус
	private Instant startTime; // Время старта выполнения задачи
	private long duration; // Продолжительность задачи

	public Task(String description, String name, TaskStatus status) {
		this.description = description;
		this.name = name;
		this.status = status;
	}

	public Task(String description, String name, TaskStatus status, Instant startTime, long duration) {
		this.description = description;
		this.name = name;
		this.status = status;
		this.startTime = startTime;
		this.duration = duration;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Instant getEndTime() {
		long secondsInMinute = 60L;
		return startTime.plusSeconds(duration * secondsInMinute);
	}

	@Override
	public String toString() {
		return "Task{" + "description='" + description + '\'' + ", id=" + id + ", name='" + name + '\'' + ", status="
				+ status + '\'' + ", startTime='" + startTime.toEpochMilli() + '\'' + ", endTime='"
				+ getEndTime().toEpochMilli() + '\'' + ", duration='" + duration + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Task task = (Task) o;
		return id == task.id && Objects.equals(description, task.description) && Objects.equals(name, task.name)
				&& status == task.status && Objects.equals(startTime, task.startTime)
				&& Objects.equals(duration, task.duration);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, name, status, startTime, duration);
	}

}
