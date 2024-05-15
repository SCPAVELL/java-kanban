package http;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import manager.TaskManagerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.io.IOException;

class HTTPTaskManagerTest<T extends TaskManagerTest<HTTPTaskManager>> {
	private KVServer server;
	private TaskManager manager;

	@BeforeEach
	public void createManager() {
		try {
			server = new KVServer();
			server.start();
			HistoryManager historyManager = Managers.getDefaultHistory();
			manager = Managers.getDefault(historyManager);
		} catch (IOException | InterruptedException e) {
			System.out.println("Ошибка при создании менеджера");
		}
	}

	@AfterEach
	public void stopServer() {
		server.stop();
	}

}