package org.togetherjava.discord.server.rendering;


import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;

public class RenderUtils {

  static int NEWLINE_MAXIMUM = 10;

  private static final Color ERROR_COLOR = new Color(255, 99, 71);
  private static final Color SUCCESS_COLOR = new Color(118, 255, 0);

  /**
   * Truncates the String to the max length and sanitizes it a bit.
   *
   * @param input the input string
   * @param maxLength the maximum length it can have
   * @return the processed string
   */
  public static String truncateAndSanitize(String input, int maxLength) {
    StringBuilder result = new StringBuilder();

    int newLineCount = 0;
    for (int codePoint : input.codePoints().toArray()) {
      if (codePoint == '\n') {
        newLineCount++;
      }

      if (codePoint == '\n' && newLineCount > NEWLINE_MAXIMUM) {
        result.append("⏎");
      } else {
        result.append(Character.toChars(codePoint));
      }
    }

    return truncate(result.toString(), maxLength);
  }

  private static String truncate(String input, int maxLength) {
    if (input.length() <= maxLength) {
      return input;
    }
    return input.substring(0, maxLength);
  }

  public static void applyFailColor(EmbedBuilder builder) {
    builder.setColor(ERROR_COLOR);
  }

  public static void applySuccessColor(EmbedBuilder builder) {
    builder.setColor(SUCCESS_COLOR);
  }
}
