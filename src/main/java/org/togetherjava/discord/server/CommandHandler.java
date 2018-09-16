package org.togetherjava.discord.server;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.togetherjava.discord.server.execution.CodeRunner;
import org.togetherjava.discord.server.io.input.InputSanitizerManager;
import org.togetherjava.discord.server.model.ExecutionResult;
import org.togetherjava.discord.server.rendering.RendererManager;

public class CommandHandler extends ListenerAdapter {

  private static final Pattern CODE_BLOCK_EXTRACTOR_PATTERN = Pattern
      .compile("```(java)?\\s*([\\w\\W]+)```");

  private CodeRunner codeRunner;
  private final String botPrefix;
  private RendererManager rendererManager;
  private boolean autoDeleteMessages;
  private Duration autoDeleteMessageDuration;
  private InputSanitizerManager inputSanitizerManager;

  @SuppressWarnings("WeakerAccess")
  public CommandHandler(Config config, CodeRunner codeRunner) {
    this.codeRunner = codeRunner;
    this.botPrefix = config.getString("prefix");
    this.rendererManager = new RendererManager();
    this.autoDeleteMessages = config.getBoolean("messages.auto_delete");
    this.autoDeleteMessageDuration = config.getDuration("messages.auto_delete.duration");
    this.inputSanitizerManager = new InputSanitizerManager();
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    String message = event.getMessage().getContentRaw();

    if (message.startsWith(botPrefix)) {
      String command = parseCommandFromMessage(message);

      executeCommand(event.getAuthor(), command, event.getTextChannel());
    }
  }

  private String parseCommandFromMessage(String messageContent) {
    String withoutPrefix = messageContent.substring(botPrefix.length());

    Matcher codeBlockMatcher = CODE_BLOCK_EXTRACTOR_PATTERN.matcher(withoutPrefix);

    if (codeBlockMatcher.find()) {
      return codeBlockMatcher.group(2);
    }

    return inputSanitizerManager.sanitize(withoutPrefix);
  }

  private void executeCommand(User user, String command, MessageChannel channel) {
    MessageBuilder messageBuilder = new MessageBuilder();
    EmbedBuilder embedBuilder = buildCommonEmbed(user);

    try {
      ExecutionResult result = codeRunner.runCode(command, user);

      rendererManager.renderResult(embedBuilder, result);
    } catch (Exception e) {
      rendererManager.renderObject(embedBuilder, e);
    }

    messageBuilder.setEmbed(embedBuilder.build());
    messageBuilder.sendTo(channel).submit().thenAccept(message -> {
      if (autoDeleteMessages) {
        message.delete().queueAfter(autoDeleteMessageDuration.toMillis(), TimeUnit.MILLISECONDS);
      }
    });
  }

  private EmbedBuilder buildCommonEmbed(User user) {
    return new EmbedBuilder()
        .setTitle(user.getName() + "'s Result");
  }
}
