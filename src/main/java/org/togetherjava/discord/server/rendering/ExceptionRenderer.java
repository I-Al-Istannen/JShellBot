package org.togetherjava.discord.server.rendering;

import jdk.jshell.EvalException;
import net.dv8tion.jda.core.EmbedBuilder;

public class ExceptionRenderer implements Renderer<Throwable> {

  @Override
  public boolean isApplicable(Object param) {
    return param instanceof Throwable;
  }

  @Override
  public EmbedBuilder render(Throwable throwable, EmbedBuilder builder) {
    RenderUtils.applyFailColor(builder);

    builder
        .addField("Exception type", throwable.getClass().getSimpleName(), true)
        .addField("Message", throwable.getMessage(), false);

    if (throwable.getCause() != null) {
      renderCause(1, throwable, builder);
    }

    if (throwable instanceof EvalException) {
      EvalException exception = (EvalException) throwable;
      builder.addField("Wraps", exception.getExceptionClassName(), true);
    }

    return builder;
  }

  private void renderCause(int index, Throwable throwable, EmbedBuilder builder) {
    builder
        .addField("Cause " + index + " type", throwable.getClass().getSimpleName(), false)
        .addField("Message", throwable.getMessage(), true);

    if (throwable.getCause() != null) {
      renderCause(index + 1, throwable.getCause(), builder);
    }
  }
}
