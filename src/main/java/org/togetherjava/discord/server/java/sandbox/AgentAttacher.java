package org.togetherjava.discord.server.java.sandbox;

import java.nio.file.Path;
import me.ialistannen.jvmagentutils.instrumentation.JvmUtils;

public class AgentAttacher {

  private static final Path agentJar = JvmUtils.generateAgentJar(
      AgentMain.class, AgentMain.class, JshellSecurityManager.class
  );

  /**
   * Returns the command line argument that attaches the agent.
   *
   * @return the command line argument to start it
   */
  public static String getCommandLineArgument() {
    return "-javaagent:" + agentJar.toAbsolutePath();
  }
}
