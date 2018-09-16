package org.togetherjava.discord.server.java.rendering;

import java.util.Locale;
import jdk.jshell.Diag;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.togetherjava.discord.server.rendering.RenderUtils;
import org.togetherjava.discord.server.rendering.Renderer;

public class CompilationErrorRenderer implements Renderer<Diag> {

  @Override
  public boolean isApplicable(Object param) {
    return param instanceof Diag;
  }

  @Override
  public EmbedBuilder render(Diag diag, EmbedBuilder builder) {
    return builder
        .addField(
            "Error message",
            RenderUtils
                .truncateAndSanitize(diag.getMessage(Locale.ROOT), MessageEmbed.VALUE_MAX_LENGTH),
            false
        );
  }
}
