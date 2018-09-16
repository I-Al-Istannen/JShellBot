package org.togetherjava.discord.server.model;

import java.util.List;
import java.util.Optional;

public interface ExecutionResult {

  /**
   * Whether the result is valid.
   *
   * @return true if this result is valid
   * @implNote the default implementation returns true if all  parts are valid
   */
  default boolean isValid() {
    return getParts().stream().allMatch(Part::isValid);
  }

  /**
   * @return the captured standard output
   */
  String getStandardOut();

  /**
   * @return a human readable id for the value of this result (e.g. a created variable)
   */
  Optional<String> getIdentifier();

  /**
   * Returns all parts of this result.
   *
   * @return all parts
   */
  List<Part> getParts();

  /**
   * A part in a result. Will be used if one execution has had multiple subparts.
   */
  interface Part {

    /**
     * @return the exception associated with this result if any
     */
    Optional<? extends Throwable> getException();

    /**
     * Whether the result is valid.
     *
     * @return true if this result is valid
     */
    boolean isValid();

    /**
     * @return the value for this part
     */
    Object getValue();
  }
}
