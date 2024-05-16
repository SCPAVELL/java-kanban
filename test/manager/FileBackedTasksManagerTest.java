package manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;
import task.TaskStatus;

public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

	public static final Path path = Path.of("data.test.csv");
	File file = new File(String.valueOf(path));

	@BeforeEach
	public void beforeEach() {
		manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
	}

	@AfterEach
	public void afterEach() {
		try {
			Files.delete(path);
		} catch (IOException exception) {
			System.out.println(exception.getMessage());
		}
	}

	@Test
	public void shouldCorrectlySaveAndLoad() {
		Task task = new Task("Description", "Title", TaskStatus.NEW, Instant.now(), 0);
		manager.createTask(task);
		Epic epic = new Epic("Description", "Title", TaskStatus.NEW, Instant.now(), 0);
		manager.createEpic(epic);
		FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
		assertEquals(List.of(task), manager.getAllTasks());
		assertEquals(List.of(epic), manager.getAllEpics());
	}

	@Test
	public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
		FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
		fileManager.save();
		assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
		assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
		assertEquals(Collections.EMPTY_LIST, manager.getAllSubtasks());
	}

	@Test
	public void shouldSaveAndLoadEmptyHistory() {
		FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
		fileManager.save();
		assertEquals(Collections.EMPTY_LIST, manager.getHistory());
	}
}