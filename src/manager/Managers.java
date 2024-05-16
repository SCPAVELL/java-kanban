package manager;

import java.io.IOException;

import http.HTTPTaskManager;
import http.KVServer;

public class Managers {

	public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
		return new InMemoryTaskManager(historyManager);
	}

	public static HistoryManager getDefaultHistory() {
		return new InMemoryHistoryManager();
	}

	public static HTTPTaskManager getDefault(HistoryManager historyManager) throws IOException, InterruptedException {
		return new HTTPTaskManager(historyManager, "http://localhost:" + KVServer.PORT);
	}
}
