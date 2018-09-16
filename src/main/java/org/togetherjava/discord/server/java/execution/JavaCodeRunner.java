package org.togetherjava.discord.server.java.execution;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.core.entities.User;
import org.togetherjava.discord.server.Config;
import org.togetherjava.discord.server.execution.CodeRunner;
import org.togetherjava.discord.server.execution.TimeWatchdog;
import org.togetherjava.discord.server.model.ExecutionResult;

public class JavaCodeRunner implements CodeRunner {

  private final ScheduledExecutorService watchdogThreadPool;
  private JShellSessionManager jShellSessionManager;

  public JavaCodeRunner(Config config) {
    this.watchdogThreadPool = Executors.newSingleThreadScheduledExecutor();

    Duration maxComputationTime = config.getDuration("computation.allotted_time");

    this.jShellSessionManager = new JShellSessionManager(
        config,
        () -> new TimeWatchdog(watchdogThreadPool, maxComputationTime)
    );
  }

  @Override
  public ExecutionResult runCode(String code, User user) {
    JShellWrapper jshell = jShellSessionManager.getSessionOrCreate(user.getId());

    return jshell.eval(code);
  }

  @Override
  public void shutdown() {
    jShellSessionManager.shutdown();
  }
}
