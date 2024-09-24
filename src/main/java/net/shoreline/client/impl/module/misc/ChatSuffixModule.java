package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.gui.chat.ChatMessageEvent;
import net.shoreline.client.util.chat.ChatUtil;

public class ChatSuffixModule extends ToggleModule {
    private static final String SUFFIX = " ｜ OvaqReborn";

    public ChatSuffixModule() {
        super("ChatSuffix", "Appends Suffix to all sent messages", ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onChatMessage(ChatMessageEvent.Client event) {
        // オリジナルのメッセージを取得
        String originalMessage = event.getMessage();

        // メッセージに / または . が含まれているかチェック
        if (originalMessage.contains("/") || originalMessage.contains(".")) {
            return; // サフィックスを追加せず、メッセージをそのまま送信
        }

        // サフィックスを追加
        String newMessage = originalMessage + SUFFIX;

        // 新しいメッセージをサーバーに送信
        ChatUtil.serverSendMessage(newMessage);

        // オリジナルのメッセージ送信をキャンセル
        event.cancel();
    }
}
