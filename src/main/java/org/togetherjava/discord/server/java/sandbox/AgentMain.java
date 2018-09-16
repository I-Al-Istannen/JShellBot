package org.togetherjava.discord.server.java.sandbox;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * An agent that sets the security manager JShell uses.
 */
public class AgentMain implements ClassFileTransformer {

  public static void premain(String args, Instrumentation inst) {
    System.setSecurityManager(new JshellSecurityManager());
  }
}
