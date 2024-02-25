package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

	private final List<SubTask> subTasks;
	private final List<Integer> subTaskIds = new ArrayList<>();
	
	public Epic(String title, String description, TaskStatus status, TaskType type) {
		super(title, description, status, type);
		subTasks = new ArrayList<>();

	}

	public void putSubTask(SubTask task) {
		subTasks.add(task);

	}

	public ArrayList<SubTask> getSubTasks() {
		return (ArrayList<SubTask>) subTasks;
	}

	public void removeSubtask(SubTask subTask) {
		subTasks.remove(subTask);

	}
	
	public void setSubtaskId(int id) {
		subTaskIds.add(id);
    }

}
