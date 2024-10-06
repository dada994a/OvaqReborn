package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.util.chat.ChatUtil;

import java.util.Random;

/**
 * @author linus
 * @since 1.0
 */
public class SpammerModule extends ToggleModule {

    private Config<Boolean> useRandomMessageConfig = new BooleanConfig("Random", "Appends a random message to your spam", true);
    private Config<String> spamMessageConfig = new StringConfig("SpamMessage", "The message to spam", "OvaqReborn On Top!");
    private Config<Float> spamspeedConfig = new NumberConfig<>("SpamSpeed", "Speed of spamming in seconds", 0.0f, 5.0f, 10.0f);

    private long lastMessageTime = 0;
    private final Random random = new Random();

    public SpammerModule() {
        super("Spammer", "Spams messages in the chat", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        lastMessageTime = System.currentTimeMillis();
        startSpamming();
    }

    private void startSpamming() {
        new Thread(() -> {
            while (isEnabled()) {
                long currentTime = System.currentTimeMillis();
                int intervalMillis = (int) (spamspeedConfig.getValue() * 1000);

                if (currentTime - lastMessageTime >= intervalMillis) {
                    String message = spamMessageConfig.getValue();

                    if (useRandomMessageConfig.getValue()) {
                        message += " [" + generateRandomMessage() + "]";
                    }

                    ChatUtil.serverSendMessage(message);

                    lastMessageTime = currentTime;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }


    private String generateRandomMessage() {
        StringBuilder randomMessage = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            char randomChar = (char) ('a' + random.nextInt(26));
            randomMessage.append(randomChar);
        }
        return randomMessage.toString();
    }
}
