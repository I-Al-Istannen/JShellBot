package org.togetherjava.discord.server.rendering;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.togetherjava.discord.server.model.ExecutionResult;

public class StandardOutputRenderer implements Renderer<ExecutionResult> {

  @Override
  public boolean isApplicable(Object param) {
    return param instanceof ExecutionResult;
  }

  @Override
  public EmbedBuilder render(ExecutionResult result, EmbedBuilder builder) {
    if (result.getStandardOut().isEmpty()) {
      return builder;
    }
    return builder
        .addField(
            "Output",
            RenderUtils.truncateAndSanitize(result.getStandardOut(), MessageEmbed.VALUE_MAX_LENGTH),
            true
        );
  }
}
