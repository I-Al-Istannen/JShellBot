package org.togetherjava.discord.server.rendering;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import org.togetherjava.discord.server.java.rendering.CompilationErrorRenderer;
import org.togetherjava.discord.server.model.ExecutionResult;
import org.togetherjava.discord.server.model.ExecutionResult.Part;

public class RendererManager {

  private List<Renderer> rendererList;
  private Renderer catchAll;

  public RendererManager() {
    this.rendererList = new ArrayList<>();
    this.catchAll = new StringCatchallRenderer();

    addRenderer(new CompilationErrorRenderer());

    addRenderer(new ExceptionRenderer());
    addRenderer(new StandardOutputRenderer());
    addRenderer(new RejectedColorRenderer());
    addRenderer(new ResultIdRenderer());
  }

  /**
   * Adds the given renderer to this manager.
   *
   * @param renderer the renderer to add
   */
  private void addRenderer(Renderer renderer) {
    rendererList.add(renderer);
  }

  /**
   * Renders a given result to the passed {@link EmbedBuilder}.
   *
   * @param builder the builder to render to
   * @param result the {@link ExecutionResult} to render
   */
  public void renderResult(EmbedBuilder builder, ExecutionResult result) {
    RenderUtils.applySuccessColor(builder);

    renderObject(builder, result);

    for (Part part : result.getParts()) {
      part.getException()
          .ifPresent(e -> renderObject(builder, e));

      renderObject(builder, part.getValue());
    }
  }

  /**
   * Renders an object to a builder.
   *
   * @param builder the builder to render to
   * @param object the object to render
   */
  public void renderObject(EmbedBuilder builder, Object object) {
    if (object == null) {
      return;
    }

    boolean rendered = false;
    for (Renderer renderer : rendererList) {
      if (renderer.isApplicable(object)) {
        rendered = true;
        // unchecked, but isApplicable should take care of it
        renderUnchecked(renderer, builder, object);
      }
    }

    if (!rendered && catchAll.isApplicable(object)) {
      // unchecked, but isApplicable should take care of it
      renderUnchecked(catchAll, builder, object);
    }
  }

  @SuppressWarnings("unchecked")
  private void renderUnchecked(Renderer renderer, EmbedBuilder embedBuilder, Object object) {
    renderer.render(object, embedBuilder);
  }
}
