package org.togetherjava.discord.server.execution;

import net.dv8tion.jda.core.entities.User;
import org.togetherjava.discord.server.model.ExecutionResult;

public interface CodeRunner {

  /**
   * Runs the given code snippet and returns the result.
   *
   * @param code the code to run
   * @param user the user to run it for
   * @return the result of running the code
   * @throws ExecutionException if the computation experiences some other error
   */
  ExecutionResult runCode(String code, User user);

  /**
   * Shuts this runner down and releases all held resources.
   */
  void shutdown();
}
