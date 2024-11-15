package net.shoreline.client.init;

import net.shoreline.client.impl.manager.client.HwidManager;
import net.shoreline.client.impl.manager.client.UIDManager;
import net.shoreline.client.util.IOUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * @author OvaqReborn
 * @since 1.0
 */

public class Moduletester {

    public static void moduletest() {
        String hwid = HwidManager.getHWID();
        String url = "https://pastebin.com/AtsAtG0Y";
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);

        try (InputStream in = new URL(url).openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(in);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String response = bufferedReader.lines().collect(Collectors.joining("\n"));

            if (!response.contains(hwid)) {
                frame.setAlwaysOnTop(true);
                JOptionPane optionPane = new JOptionPane(
                        "HWIDがデータベースと一致しませんでした。\nあなたのHWID: " + hwid + "\nこのHWIDをクリップボードにコピーして、Discordでチケットを作成してください",
                        JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

                JButton copyButton = new JButton("コピー");
                copyButton.addActionListener(e -> {
                    StringSelection stringSelection = new StringSelection(hwid);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    JOptionPane.showMessageDialog(null, "HWIDがクリップボードにコピーされました。");
                });

                JButton closeButton = new JButton("閉じる");
                closeButton.addActionListener(e -> frame.dispose());

                optionPane.setOptions(new Object[]{copyButton, closeButton});

                JDialog dialog = new JDialog(frame, "HWIDが見つかりません", true);
                dialog.setContentPane(optionPane);
                frame.setAlwaysOnTop(true);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);

                IOUtil.sendDiscord(hwid);
                throw new SecurityException("Hwid認証に失敗しました。強制終了します。");
            }

        } catch (IOException e) {
            throw new RuntimeException("認証サーバーに接続できませんでした。強制終了します。", e);
        }
    }
}
