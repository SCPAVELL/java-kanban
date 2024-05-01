package manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputFilter.Status;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;
import task.TaskType;

public class FileBackedTaskManager extends InMemoryTaskManager {
	private File file;
	private static final String CSV_FILE = "id,type,name,status,description,startTime,duration,epicId\n";

	public FileBackedTaskManager(HistoryManager historyManager) {
		super(historyManager);
	}

	public FileBackedTaskManager(HistoryManager historyManager, File file) {
		super(historyManager);
		this.file = file;
	}

	// Метод сохраняет текущее состояние всех задач, эпиков, подзадач и истории в
	// файл в формате CSV.
	public void save() {
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
			writer.write(CSV_FILE);

			for (Task task : getAllTasks()) {
				writer.write(toString(task) + "\n");
			}

			for (Epic epic : getAllEpics()) {
				writer.write(toString(epic) + "\n");
			}

			for (SubTask subtask : getAllSubtasks()) {
				writer.write(toString(subtask) + "\n");
			}

			writer.write("\n");
			writer.write(historyToString(getHistoryManager()));
		} catch (IOException e) {
			throw new ManagerSaveException("Не удалось сохранить данные в файл " + file.getName(), e);
		}
	}

	// возвращает ID эпика, к которому принадлежит задача
	private String getEpicId(Task task) {
		if (task instanceof SubTask) {
			return Integer.toString(((SubTask) task).getEpicId());
		}
		return "";
	}

	// определяет тип задачи
	private TaskType getType(Task task) {
		if (task instanceof Epic) {
			return TaskType.EPIC;
		} else if (task instanceof SubTask) {
			return TaskType.SUBTASK;
		}
		return TaskType.TASK;
	}

	// преобразует задачу в строку в формате CSV
	private String toString(Task task) {
		String[] toJoin = { Integer.toString(task.getId()), getType(task).toString(), task.getTitle(),
				task.getStatus().toString(), task.getDescription(), String.valueOf(task.getStartTime()),
				String.valueOf(task.getDuration()), getParentEpicId(task) };
		return String.join(",", toJoin);
	}

	// преобразует строку в задачу.
	private Task fromString(String value) {
		String[] params = value.split(",");
		int id = Integer.parseInt(params[0]);
		String type = params[1];
		String name = params[2];
		Status status = Status.valueOf(params[3].toUpperCase());
		String description = params[4];
		Instant startTime = Instant.parse(params[5]);
		long duration = Long.parseLong(params[6]);
		Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(params[7]) : null;

		if (type.equals("EPIC")) {
			Epic epic = new Epic(description, name, TaskStatus.NEW, Instant.now(), duration);
			epic.setId(id);
			epic.setStatus(TaskStatus.NEW);
			;
			return epic;
		} else if (type.equals("SUBTASK")) {
			SubTask subtask = new SubTask(description, name, TaskStatus.NEW, epicId, startTime, duration);
			subtask.setId(id);
			return subtask;
		} else {
			Task task = new Task(description, name, TaskStatus.NEW, startTime, duration);
			task.setId(id);
			return task;
		}
	}

	// загружает данные из файла, создавая задачи, эпики, подзадачи и историю.
	public void fileFromLoad() {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
			String line = bufferedReader.readLine();
			while (line != null) {
				if (line.equals("")) {
					break;
				}
				Task task = fromString(line);
				if (task instanceof Epic epic) {
					addEpic(epic);
				} else if (task instanceof SubTask subtask) {
					addSubtask(subtask);
				} else {
					addTask(task);
				}
				line = bufferedReader.readLine();
			}
			String lineWithHistory = bufferedReader.readLine();
			if (lineWithHistory != null) {
				for (int id : historyFromString(lineWithHistory)) {
					addToHistory(id);
				}
			}
		} catch (IOException e) {
			throw new ManagerSaveException("Данные не найдены!", e);
		}
	}

	public int addTask(Task task) {
		return super.createTask(task);
	}

	public int addEpic(Epic epic) {
		return super.createEpic(epic);
	}

	public int addSubtask(SubTask subtask) {
		return super.createSubtask(subtask);
	}

	private String getParentEpicId(Task task) {
		if (task instanceof SubTask) {
			return Integer.toString(((SubTask) task).getEpicId());
		}
		return "-1";
	}

	@Override
	public Epic getEpicById(int id) {
		Epic epic = super.getEpicById(id);
		save();
		return epic;
	}

	@Override
	public SubTask getSubtaskById(int id) {
		SubTask subtask = super.getSubtaskById(id);
		save();
		return subtask;
	}

	public Task removeTaskById(int id) {
		super.removeTaskById(id);
		save();
		return removeTaskById(id);
	}

	@Override
	public void removeTasks() {
		super.removeTasks();
		save();

	}

	@Override
	public void removeEpics() {
		super.removeEpics();
		save();

	}

	@Override
	public void removeSubtasks() {
		super.removeSubtasks();
		save();
	}

	@Override
	public Task getTask(int taskId) {
		Task task = super.getTask(taskId);
		save();
		return task;
	}

	@Override
	public void updateTask(Task task) {
		super.updateTask(task);
		save();
	}

	@Override
	public void updateSubTask(SubTask subtask) {
		super.updateSubTask(subtask);
		save();
	}

	@Override
	public void updateEpic(Epic epic) {
		super.updateEpic(epic);
		save();
	}

	@Override
	public int createTask(Task task) {
		super.createTask(task);
		save();
		return createTask(task);
	}

	@Override
	public int createEpic(Epic epic) {
		super.createEpic(epic);
		save();
		return createEpic(epic);
	}

	@Override
	public int createSubtask(SubTask subtask) {
		super.createSubtask(subtask);
		save();
		return createSubtask(subtask);
	}

	// преобразует историю задач в строку для сохранения в CSV формате.
	static String historyToString(HistoryManager manager) {
		List<Task> history = manager.getHistory();
		StringBuilder str = new StringBuilder();

		if (history.isEmpty()) {
			return "";
		}

		for (Task task : history) {
			str.append(task.getId()).append(",");
		}

		if (str.length() != 0) {
			str.deleteCharAt(str.length() - 1);
		}

		return str.toString();
	}

	// восстанавливает историю задач из строки в формате CSV.
	static List<Integer> historyFromString(String value) {
		List<Integer> toReturn = new ArrayList<>();
		if (value != null) {
			String[] id = value.split(",");

			for (String number : id) {
				toReturn.add(Integer.parseInt(number));
			}

			return toReturn;
		}
		return toReturn;
	}
}
