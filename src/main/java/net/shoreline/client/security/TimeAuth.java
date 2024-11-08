package net.shoreline.client.security;

import net.shoreline.client.impl.manager.client.AntiDumpManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Hypinohaizin
 * @since 2024/11/08 12:29
 */
public class TimeAuth {
 public static AntiDumpManager ANTIDUMP;
 private ScheduledExecutorService scheduler;

 public TimeAuth() {
  scheduler = Executors.newSingleThreadScheduledExecutor();
  startScheduledTask();
 }

 private void startScheduledTask() {
  scheduler.scheduleAtFixedRate(new Runnable() {
   @Override
   public void run() {
    Authenticator.hwidAuth();

    ANTIDUMP = new AntiDumpManager();
    AntiDumpManager.init();
   }
  }, 0, 10, TimeUnit.MINUTES);
 }

 public void stopScheduledTask() {
  if (scheduler != null && !scheduler.isShutdown()) {
   scheduler.shutdown();
  }
 }
}
