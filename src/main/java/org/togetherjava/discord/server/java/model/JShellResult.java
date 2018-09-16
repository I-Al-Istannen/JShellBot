package org.togetherjava.discord.server.java.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import jdk.jshell.Diag;
import jdk.jshell.Snippet.Status;
import jdk.jshell.SnippetEvent;
import org.togetherjava.discord.server.model.ExecutionResult;

/**
 * Wraps the result of executing JShell.
 */
public class JShellResult implements ExecutionResult {

  private List<SnippetResult> snippetResults;
  private List<Diagnostic> diagnostics;
  private String stdOut;

  public JShellResult(List<SnippetEvent> events, List<Diag> diagnostics, String stdOut) {
    this.snippetResults = events.stream().map(SnippetResult::new).collect(Collectors.toList());
    this.diagnostics = diagnostics.stream().map(Diagnostic::new).collect(Collectors.toList());
    this.stdOut = stdOut == null ? "" : stdOut;
  }

  public List<SnippetEvent> getEvents() {
    return snippetResults.stream().map(SnippetResult::getRaw).collect(Collectors.toList());
  }

  @Override
  public String getStandardOut() {
    return stdOut;
  }

  @Override
  public Optional<String> getIdentifier() {
    return snippetResults.isEmpty()
        ? Optional.empty()
        : Optional.of(snippetResults.get(0).getId());
  }

  @Override
  public List<Part> getParts() {
    List<Part> parts = new ArrayList<>(snippetResults);
    parts.addAll(diagnostics);
    return parts;
  }

  public static class SnippetResult implements ExecutionResult.Part {

    private SnippetEvent event;

    SnippetResult(SnippetEvent event) {
      this.event = event;
    }

    @Override
    public Optional<? extends Throwable> getException() {
      return Optional.ofNullable(event.exception());
    }

    @Override
    public boolean isValid() {
      return event.status() == Status.VALID;
    }

    @Override
    public Object getValue() {
      return event.value();
    }

    String getId() {
      return event.snippet().id();
    }

    SnippetEvent getRaw() {
      return event;
    }
  }

  public static class Diagnostic implements ExecutionResult.Part {

    private Diag diagnostic;

    Diagnostic(Diag diagnostic) {
      this.diagnostic = diagnostic;
    }

    @Override
    public Optional<? extends Throwable> getException() {
      return Optional.empty();
    }

    @Override
    public boolean isValid() {
      return !diagnostic.isError();
    }

    @Override
    public Object getValue() {
      return diagnostic.getMessage(Locale.ROOT);
    }

    /**
     * @return the id for this snippet
     */
    public String getId() {
      return null;
    }

    Diag getRaw() {
      return diagnostic;
    }
  }
}
