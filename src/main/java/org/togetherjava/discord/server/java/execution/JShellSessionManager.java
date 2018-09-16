package org.togetherjava.discord.server.java.execution;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togetherjava.discord.server.Config;
import org.togetherjava.discord.server.execution.TimeWatchdog;

class JShellSessionManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(JShellSessionManager.class);

  private ConcurrentHashMap<String, SessionEntry> sessionMap;
  private Duration timeToLive;
  private Config config;
  private Thread ticker;
  private Supplier<LocalDateTime> timeProvider;
  private Supplier<TimeWatchdog> watchdogSupplier;

  /**
   * Creates a new {@link JShellSessionManager} with the specified values.
   *
   * @param config the config to use for creating the {@link JShellWrapper}s
   * @param watchdogSupplier the supplier to get {@link TimeWatchdog}s from
   */
  JShellSessionManager(Config config, Supplier<TimeWatchdog> watchdogSupplier) {
    this.config = config;
    this.timeProvider = LocalDateTime::now;
    this.watchdogSupplier = watchdogSupplier;
    this.sessionMap = new ConcurrentHashMap<>();

    this.timeToLive = Objects.requireNonNull(
        config.getDuration("session.ttl"), "'session.ttl' not set"
    );

    this.ticker = new Thread(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        purgeOld();

        try {
          Thread.sleep(timeToLive.dividedBy(2).toMillis());
        } catch (InterruptedException e) {
          LOGGER.warn("Session housekeeper was interrupted", e);
          break;
        }
      }
    }, "JShellSessionManager housekeeper");

    this.ticker.start();
  }

  /**
   * Returns the {@link JShellWrapper} for the user or creates a new.
   *
   * @param userId the id of the user
   * @return the {@link JShellWrapper} to use
   * @throws IllegalStateException if this manager was already shutdown via {@link #shutdown()}
   */
  JShellWrapper getSessionOrCreate(String userId) {
    if (ticker == null) {
      throw new IllegalStateException("This manager was shutdown already.");
    }
    SessionEntry sessionEntry = sessionMap.computeIfAbsent(
        userId,
        id -> new SessionEntry(new JShellWrapper(config, watchdogSupplier.get()), id)
    );

    return sessionEntry.getJShell();
  }

  /**
   * Stops all activity of this manager (running thready, etx.) and frees its resources. You will no
   * longer be able to get a {@link jdk.jshell.JShell} from this manager.
   * <p>
   * Should be called when the system is shut down.
   */
  void shutdown() {
    ticker.interrupt();
    ticker = null;
    sessionMap.values().forEach(sessionEntry -> sessionEntry.getJShell().close());
  }

  /**
   * Purges sessions that were inactive for longer than the specified threshold.
   */
  void purgeOld() {
    LOGGER.debug("Starting purge");
    LocalDateTime now = timeProvider.get();

    // A session could potentially be marked for removal, then another threads retrieves it and updates its
    // last accessed state, leading to an unnecessary deletion. This should not have any impact on the caller
    // though.
    sessionMap.values().removeIf(sessionEntry -> {
      Duration delta = Duration.between(now, sessionEntry.getLastAccess()).abs();

      boolean tooOld = delta.compareTo(timeToLive) > 0;

      if (tooOld) {
        sessionEntry.getJShell().close();

        LOGGER.debug(
            "Removed session for '{}', difference was '{}'",
            sessionEntry.getUserId(), delta
        );
      }

      return tooOld;
    });
  }

  /**
   * Sets the used time provider. Useful for testing only.
   *
   * @param timeProvider the provider to use
   */
  void setTimeProvider(Supplier<LocalDateTime> timeProvider) {
    this.timeProvider = timeProvider;
  }

  private static class SessionEntry {

    private JShellWrapper jshell;
    private String userId;
    private LocalDateTime lastAccess;

    SessionEntry(JShellWrapper jshell, String userId) {
      this.jshell = jshell;
      this.userId = userId;
      this.lastAccess = LocalDateTime.now();
    }

    /**
     * Returns the {@link JShellWrapper} and sets the {@link #getLastAccess()} to now.
     *
     * @return the associated {@link JShellWrapper}
     */
    JShellWrapper getJShell() {
      lastAccess = LocalDateTime.now();
      return jshell;
    }

    LocalDateTime getLastAccess() {
      return lastAccess;
    }

    String getUserId() {
      return userId;
    }
  }
}
