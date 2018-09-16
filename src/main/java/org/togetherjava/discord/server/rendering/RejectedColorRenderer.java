package org.togetherjava.discord.server.rendering;

import net.dv8tion.jda.core.EmbedBuilder;
import org.togetherjava.discord.server.model.ExecutionResult;

public class RejectedColorRenderer implements Renderer<ExecutionResult> {

  @Override
  public boolean isApplicable(Object param) {
    return param instanceof ExecutionResult;
  }

  @Override
  public EmbedBuilder render(ExecutionResult result, EmbedBuilder builder) {
    if (!result.isValid()) {
      RenderUtils.applyFailColor(builder);
    }

    return builder;
  }
}
