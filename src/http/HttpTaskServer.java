package http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import com.sun.net.httpserver.HttpServer;
import http.handlers.EpicHandler;
import http.handlers.HistoryHandler;
import http.handlers.SubtaskByEpicHandler;
import http.handlers.SubtaskHandler;
import http.handlers.TaskHandler;
import http.handlers.TasksHandler;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;

public class HttpTaskServer {

	private final HttpServer httpServer;
	private static final int PORT = 8080;
	private static final Logger logger = Logger.getLogger(HttpTaskServer.class.getName());

	public HttpTaskServer() throws IOException, InterruptedException {
		HistoryManager historyManager = Managers.getDefaultHistory();
		TaskManager taskManager = Managers.getDefault(historyManager);
		this.httpServer = HttpServer.create();
		httpServer.bind(new InetSocketAddress(PORT), 0);
		httpServer.createContext("/tasks/task/", new TaskHandler(taskManager));
		httpServer.createContext("/tasks/epic/", new EpicHandler(taskManager));
		httpServer.createContext("/tasks/subtask/", new SubtaskHandler(taskManager));
		httpServer.createContext("/tasks/subtask/epic/", new SubtaskByEpicHandler(taskManager));
		httpServer.createContext("/tasks/history/", new HistoryHandler(taskManager));
		httpServer.createContext("/tasks/", new TasksHandler(taskManager));

		logger.info("HTTP server initialized on port " + PORT);
	}

	public void start() {
		httpServer.start();
		logger.info("HTTP server started");
	}

	public void stop() {
		httpServer.stop(1);
		logger.info("HTTP server stopped");
	}

}
