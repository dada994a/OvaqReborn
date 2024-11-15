package net.shoreline.client.impl.event.gui.hud;

import net.minecraft.text.Text;
import net.shoreline.client.api.event.Cancelable;
import net.shoreline.client.api.event.Event;

/**
 * @author Hypinohaizin
 * @since 2024/11/15 19:45
 */

@Cancelable
public class ChatTextEvent extends Event {
 private Text text;

 public ChatTextEvent(Text text) {
  this.text = text;
 }

 public void setText(Text text) {
  this.text = text;
 }

 public Text getText() {
  return this.text;
 }
}
