package manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import task.Epic;
import task.SubTask;
import task.Task;

public class FileBackedTaskManager implements TaskManager {
	private File file;
	private static final String CSV_FILE = "id,type,name,status,description,startTime,duration,epicId\n";

	@Override
	public Epic getEpicById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubTask getSubtaskById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeTasks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeEpics() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSubtasks() {
		// TODO Auto-generated method stub

	}

	@Override
	public Task getTask(int taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateTask(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSubTask(SubTask subtask) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEpic(Epic epic) {
		// TODO Auto-generated method stub

	}

	@Override
	public Task removeTaskById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SubTask> getEpicSubTask(Epic epic) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> getHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> getAllTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Epic> getAllEpics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SubTask> getAllSubtasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkStatus(Epic epic) {
		// TODO Auto-generated method stub

	}

	@Override
	public int createTask(Task task) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int createEpic(Epic epic) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int createSubtask(SubTask subtask) {
		// TODO Auto-generated method stub
		return 0;
	}
}
