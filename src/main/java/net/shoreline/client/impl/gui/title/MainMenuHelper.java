package net.shoreline.client.impl.gui.title;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shoreline.client.OvaqRebornMod;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.util.button.AbstractButton;
import net.shoreline.client.util.button.components.CustomTitleButton;
import net.shoreline.client.util.Globals;
import net.shoreline.client.util.changelogs.ChangeLog;
import net.shoreline.client.util.changelogs.ChangeLogEntry;
import net.shoreline.client.util.math.MathUtil;
import ru.vidtu.ias.screen.AccountScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class MainMenuHelper extends Screen implements Globals {
    private static final List<AWA> water = new ArrayList<>();
    private static final List<Snowflake> snowflakes = new ArrayList<>();
    private static final List<Meteor> meteors = new ArrayList<>();
    private static final Random random = new Random();

    private final List<AbstractButton> buttons = new ArrayList<>();

    private final AbstractButton singleplayer = new CustomTitleButton("Singleplayer",
            () -> mc.setScreen(new SelectWorldScreen(this)));
    private final AbstractButton multiplayer = new CustomTitleButton("Multiplayer",
            () -> mc.setScreen(new MultiplayerScreen(this)));
    private final AbstractButton accounts = new CustomTitleButton("Accounts",
            () -> mc.setScreen(new AccountScreen(this)));
    private final AbstractButton options = new CustomTitleButton("Options",
            () -> mc.setScreen(new OptionsScreen(this, mc.options)));
    private final AbstractButton exit = new CustomTitleButton("Exit",
            mc::scheduleStop);

    public MainMenuHelper() {
        super(Text.of("OvaqReborn New MainMenu"));
        buttons.addAll(Arrays.asList(
                singleplayer,
                multiplayer,
                accounts,
                options,
                exit)
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderManager.rect(context.getMatrices(), 0, 0, 960, 496, new Color(23, 23, 23).getRGB());
        float dumb1 = (float) (Math.sin(System.currentTimeMillis() / 500) * 10 + 10);
        long dumb2 = System.currentTimeMillis() / 50;

        // 水追加
        if (water.isEmpty()) {
            for (int i = 0; i < 200; i++) {
                Color color = new Color(
                        random.nextInt(50) + 100,
                        random.nextInt(100),
                        random.nextInt(100)
                );
                water.add(
                        new AWA(
                                random.nextInt(10),
                                random.nextInt(10),
                                random.nextInt(4000),
                                random.nextInt(4000),
                                color
                        )
                );
            }
        }

        // 雪
        if (snowflakes.isEmpty()) {
            for (int i = 0; i < 100; i++) {
                snowflakes.add(new Snowflake(
                        random.nextInt(960),
                        random.nextInt(496),
                        random.nextFloat() * 2 + 1
                ));
            }
        }

        for (Snowflake snowflake : snowflakes) {
            snowflake.update();
            RenderManager.glowRoundRect(
                    context.getMatrices(), snowflake.x, snowflake.y, 0, 0, 0, 2, new Color(255, 255, 255, 100), 40, false, false, false, false
            );
        }

        // 流星
        if (meteors.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                meteors.add(new Meteor(
                        random.nextInt(960),
                        random.nextInt(496),
                        random.nextFloat() * 5 + 2,
                        random.nextFloat() * 5 + 2
                ));
            }
        }

        // 流星
        for (Meteor meteor : meteors) {
            meteor.update();
            RenderManager.glowRoundRect(
                    context.getMatrices(), meteor.x, meteor.y, 0, 0, 0, 3, new Color(255, 255, 255, 200), 40, false, false, false, false
            );
        }

        // 背景アニメーション
        for (AWA bubble : water) {
            float x = (bubble.x2 + dumb2 * bubble.x) % 1500 - 300;
            float y = (bubble.y2 + dumb2 * bubble.y) % 1000 - 300;
            float size = 200 + (dumb1 * (float) Math.sin(bubble.y));

            RenderManager.glowRoundRect(
                    context.getMatrices(), x, y, 0, 0, 0, size, new Color(49, 71, 158, 255), 40, false, false, false, false
            );
        }

        // 文字
        RenderManager.tf.drawStringShadow(context.getMatrices(), OvaqRebornMod.MOD_NAME + " " + OvaqRebornMod.MOD_VER + " Made by hypinohaizin, Rom, Naa_Naa", 2F, (float) (7.5 + mc.textRenderer.fontHeight + 4), -1);

        // components
        int wHeight = mc.getWindow().getHeight() / 4;
        int wWidth = mc.getWindow().getScaledWidth() / 2;

        //Changelog
        RenderManager.drawTexture(context, "icon/about.png", 2, 50, 140, 184);
        RenderManager.tf.drawString(context.getMatrices(), "Changelogs for " + OvaqRebornMod.MOD_VER, 10, 50, -1);
        List<ChangeLogEntry> changeLogs = ChangeLog.getChangeLogs();
        int x = 10;
        int y = 65;
        int offsetY = 12;

        for (ChangeLogEntry log : changeLogs) {
            String icon = switch (log.type) {
                case ADD -> Formatting.GREEN + "[+]" + Formatting.RESET + " ";
                case REMOVE -> Formatting.RED + "[-]" + Formatting.RESET + " ";
                case FIX -> Formatting.YELLOW + "[!]" + Formatting.RESET + " ";
                case IMPROVE -> Formatting.LIGHT_PURPLE + "[*]" + Formatting.RESET + " ";
            };
            RenderManager.tf.drawString(context.getMatrices(), icon + log.description, x, y, -1);
            y += offsetY;
        }

        //Creditに行くようにする物
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        int logoWidth = 100;
        int logoHeight = 100;
        int logoX = (screenWidth - logoWidth) / 2;
        int logoY = (screenHeight - logoHeight) / 4;
        boolean isHovered = MathUtil.isHovered(mouseX, mouseY, logoX, logoY, logoWidth, logoHeight);
        float targetScale = isHovered ? 1.2f : 1.0f;
        float currentScale = 1.0f;
        float maxScale = 1.2f;
        float minScale = 1.0f;
        float scaleSpeed = 8.0f;
        currentScale += (targetScale - currentScale) * scaleSpeed * delta;
        if (currentScale > maxScale) {
            currentScale = maxScale;
        } else if (currentScale < minScale) {
            currentScale = minScale;
        }
        context.getMatrices().push();
        context.getMatrices().translate(logoX + logoWidth / 2, logoY + logoHeight / 2, 0);
        context.getMatrices().scale(currentScale, currentScale, 1);
        context.getMatrices().translate(-logoWidth / 2, -logoHeight / 2, 0);
        RenderManager.drawTexture(context, "icon/star.png", 0, 0, logoWidth, logoHeight);
        context.getMatrices().pop();

        singleplayer.position(wWidth - 80, wHeight - 28)
                .size(160, 27);

        multiplayer.position(wWidth - 80, wHeight + 4)
                .size(160, 27);

        accounts.position(wWidth - 80, wHeight + 36)
                .size(160, 27);

        options.position(wWidth - 80, wHeight + 82)
                .size(78, 27);

        exit.position(wWidth - 80 + 82, wHeight + 82)
                .size(78, 27);

        buttons.forEach(buttons -> buttons.render(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        buttons.forEach(buttons -> buttons.mouseClicked(mouseX, mouseY, button));
        if (MathUtil.isHovered(mouseX, mouseY, (mc.getWindow().getScaledWidth() - 100) / 2, (mc.getWindow().getScaledHeight() - 100) / 4, 100, 100)) {
            mc.setScreen(new CreditMenuHelper());
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    record AWA(
            int x,
            int y,
            int x2,
            int y2,
            Color color
    ) {}

    // Core for Particles. made by rom
    static class Snowflake {
        float x, y;
        float speed;

        Snowflake(float x, float y, float speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }

        void update() {
            y += speed;
            if (y > 496) {
                y = -10;
                x = random.nextInt(960);
            }
        }
    }

    static class Meteor {
        float x, y;
        float speedX, speedY;

        Meteor(float x, float y, float speedX, float speedY) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
        }

        void update() {
            x += speedX;
            y += speedY;
            if (x > 960 || y > 496) {
                x = random.nextInt(960);
                y = random.nextInt(496);
                speedX = random.nextFloat() * 5 + 2;
                speedY = random.nextFloat() * 5 + 2;
            }
        }
    }
}