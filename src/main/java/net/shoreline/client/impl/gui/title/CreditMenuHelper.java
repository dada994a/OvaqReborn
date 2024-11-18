package net.shoreline.client.impl.gui.title;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shoreline.client.api.render.RenderManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class CreditMenuHelper extends Screen {
    private static final List<AWA> water = new ArrayList<>();
    private static final List<Snowflake> snowflakes = new ArrayList<>();
    private static final List<Meteor> meteors = new ArrayList<>();
    private static final Random random = new Random();

    public CreditMenuHelper() {
        super(Text.of("Credit Menu For Ovaq Reborn Beta"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderManager.rect(context.getMatrices(), 0, 0, 960, 496, new Color(23, 23, 23).getRGB());
        float dumb1 = (float) (Math.sin(System.currentTimeMillis() / 500) * 10 + 10);
        long dumb2 = System.currentTimeMillis() / 50;

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
                    context.getMatrices(), snowflake.x, snowflake.y, 0, 0, 0, 2, new Color(255, 255, 255, 255), 255, false, false, false, false
            );
        }

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

        for (Meteor meteor : meteors) {
            meteor.update();
            RenderManager.glowRoundRect(
                    context.getMatrices(), meteor.x, meteor.y, 0, 0, 0, 3, new Color(255, 255, 255, 255), 255, false, false, false, false
            );
        }

        for (AWA bubble : water) {
            float x = (bubble.x2 + dumb2 * bubble.x) % 1500 - 300;
            float y = (bubble.y2 + dumb2 * bubble.y) % 1000 - 300;
            float size = 200 + (dumb1 * (float) Math.sin(bubble.y));

            RenderManager.glowRoundRect(
                    context.getMatrices(), x, y, 0, 0, 0, size, new Color(49, 71, 158, 255), 40, false, false, false, false
            );
        }
        List<CreditEntry> credits = new ArrayList<>();
        credits.add(new CreditEntry("Credits", null, null));
        credits.add(new CreditEntry("Hypinohaizin", "icon/credit/hypinohaizin.png", "Lead Developer"));
        credits.add(new CreditEntry("Rom", "icon/credit/rom.png", "Developer"));
        credits.add(new CreditEntry("Naa_Naa", "icon/credit/naanaa.png", "Developer"));
        credits.add(new CreditEntry("DachoEnaga", "icon/credit/dacho.png", "Mental Health"));

        int creditWidth = 200;
        int creditHeight = 180;
        int creditX = (960 - creditWidth) / 2;
        int creditY = (496 - creditHeight) / 2;

        RenderManager.glowRoundRect(
                context.getMatrices(), creditX, creditY, creditWidth, creditHeight, 10, 10, new Color(42, 19, 155, 255), 80, false, false, false, false
        );

        int textY = creditY + 20;
        int iconTotalWidth = 0;
        int iconTotalHeight = 0;
        int iconTotalX = creditX + creditWidth + 10 - 2;
        int iconTotalY = creditY + 20 - 2;

        for (int i = 0; i < credits.size(); i++) {
            CreditEntry credit = credits.get(i);
            RenderManager.tf.drawCenteredString(context.getMatrices(), credit.name, creditX + creditWidth / 2, textY, -1);
            textY += 20;

            if (credit.detail != null) {
                RenderManager.tf.drawString(context.getMatrices(), credit.detail, creditX + creditWidth + 40, textY - 20, -1);
                textY += 20;
            }

            if (credit.iconPath != null) {
                int iconSize = 20;
                int iconX = creditX + creditWidth + 10;
                int iconY = textY - 40;
                RenderManager.drawTexture(context, new Identifier("ovaqreborn", credit.iconPath), iconX, iconY, iconSize, iconSize);
                iconTotalWidth += iconSize;
                iconTotalHeight = Math.max(iconTotalHeight, iconSize);
            }
        }

        RenderManager.glowRoundRect(
                context.getMatrices(), iconTotalX, iconTotalY, iconTotalWidth + 10, iconTotalHeight + 150, 3, 3, new Color(42, 19, 155, 255), 80, false, false, false, false
        );
    }

    record AWA(
            int x,
            int y,
            int x2,
            int y2,
            Color color
    ) {}

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

    static class CreditEntry {
        String name;
        String iconPath;
        String detail;

        CreditEntry(String name, String iconPath, String detail) {
            this.name = name;
            this.iconPath = iconPath;
            this.detail = detail;
        }
    }
}
