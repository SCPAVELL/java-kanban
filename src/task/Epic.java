package task;

import java.util.ArrayList;

public class Epic extends Task {

	private final ArrayList<SubTask> subTasks;

	public Epic(String title, String description, TaskStatus status, TaskType type) {
		super(title, description, status, type);
		subTasks = new ArrayList<>();

	}

	public void putSubTask(SubTask task) {
		subTasks.add(task);

	}

	public ArrayList<SubTask> getSubTasks() {
		return subTasks;
	}

	public void removeSubtask(SubTask subTask) {
		subTasks.remove(subTask);

	}

}
