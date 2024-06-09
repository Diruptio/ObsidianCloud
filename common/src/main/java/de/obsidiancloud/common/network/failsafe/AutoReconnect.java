package de.obsidiancloud.common.network.failsafe;

import de.obsidiancloud.common.network.pipeline.ConnectionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;

/**
 * @author Miles
 * @since 08.06.2024
 */
@AllArgsConstructor
public class AutoReconnect {

    private static final ScheduledExecutorService EXECUTOR_SERVICE =
            new ScheduledThreadPoolExecutor(1);

    private ConnectionHandler connection;
    private final String host;
    private final Consumer<ConnectionHandler> consumer;

    public void listen() {
        AutoReconnectTask task = new AutoReconnectTask(connection, host);
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> this.run(task), 5, 5, TimeUnit.SECONDS);
    }

    private void run(AutoReconnectTask task) {
        task.check()
                .thenAccept(
                        newConnection -> {
                            if (newConnection != null) {
                                connection = newConnection;
                                consumer.accept(newConnection);
                            }
                        });
    }
}
