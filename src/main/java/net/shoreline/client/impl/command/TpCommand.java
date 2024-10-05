package net.shoreline.client.impl.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.shoreline.client.api.command.Command;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.chat.ChatUtil;

/**
 * Command to teleport the player to specified coordinates.
 */
public class TpCommand extends Command {

    public TpCommand() {
        super("Tp", "Teleports the player to the specified coordinates", literal("tp"));
    }
// ちくわが見にくくしてるのでコードが書けてない可能性があります^^
    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(argument("x", DoubleArgumentType.doubleArg())
                        .then(argument("y", DoubleArgumentType.doubleArg())
                                .then(argument("z", DoubleArgumentType.doubleArg())
                                        .executes(c -> {
                                            double x = DoubleArgumentType.getDouble(c, "x");
                                            double y = DoubleArgumentType.getDouble(c, "y");
                                            double z = DoubleArgumentType.getDouble(c, "z");

                                            // Check if the player is in a state that allows teleportation
                                            if (mc.player.isFallFlying() || mc.player.isSleeping() || mc.player.isSpectator()) {
                                                ChatUtil.error("You cannot teleport while flying, sleeping, or spectating!");
                                                return 1;
                                            }

                                            // Validate the y-coordinate
                                            if (y < 0 || y > 256) {
                                                ChatUtil.error("Y coordinate must be between 0 and 256.");
                                                return 1;
                                            }

                                            // Teleport the player to the specified coordinates
                                            Managers.POSITION.setPosition(x, y, z);
                                            ChatUtil.clientSendMessage("Teleported to §s" + x + "§f, §s" + y + "§f, §s" + z + "§f.");
                                            return 1;
                                        })
                                )
                        )
                )
                .executes(c -> {
                    ChatUtil.error("Must provide x, y, and z coordinates!");
                    return 1;
                });
    }
}
