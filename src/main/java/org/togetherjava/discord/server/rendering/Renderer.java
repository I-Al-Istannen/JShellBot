package org.togetherjava.discord.server.rendering;

import net.dv8tion.jda.core.EmbedBuilder;

public interface Renderer<T> {

  /**
   * Checks if this renderer can render the given object.
   *
   * @param param the object to check
   * @return true if this renderer can handle the passed object
   */
  boolean isApplicable(Object param);

  /**
   * Renders the given object to the {@link EmbedBuilder}.
   *
   * @param object the object to render
   * @param builder the {@link EmbedBuilder} to modify
   * @return the rendered {@link EmbedBuilder}
   */
  EmbedBuilder render(T object, EmbedBuilder builder);
}
