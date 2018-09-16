package org.togetherjava.discord.server.rendering;

import net.dv8tion.jda.core.EmbedBuilder;
import org.togetherjava.discord.server.model.ExecutionResult;

public class ResultIdRenderer implements Renderer<ExecutionResult> {

  @Override
  public boolean isApplicable(Object param) {
    return param instanceof ExecutionResult;
  }

  @Override
  public EmbedBuilder render(ExecutionResult object, EmbedBuilder builder) {
    object.getIdentifier()
        .ifPresent(id -> builder.addField("Snippet-ID", "$" + id, true));

    return builder;
  }
}
