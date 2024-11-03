package net.shoreline.client.init;

import net.shoreline.client.impl.manager.client.HwidManager;
import net.shoreline.client.util.IOUtil;
import javax.swing.*;
import java.awt.*;
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
        String url = "https://pastebin.com/raw/AtsAtG0Y";

        try (InputStream in = new URL(url).openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(in);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String response = bufferedReader.lines().collect(Collectors.joining("\n"));

            if (!response.contains(hwid)) {
                UIManager.put("OptionPane.minimumSize", new Dimension(500, 150));
                JFrame frame = new JFrame();
                frame.setAlwaysOnTop(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JTextField hwidField = new JTextField(hwid);
                hwidField.setEditable(false);
                hwidField.setBackground(null);
                hwidField.setBorder(null);

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JLabel("下にあるHwidをコピーしてハイピの廃人に送ってください"), BorderLayout.NORTH);
                panel.add(hwidField, BorderLayout.CENTER);
                panel.add(new JLabel("注意: これは初回起動時に表示されます"), BorderLayout.SOUTH);

                JOptionPane.showMessageDialog(frame, panel, "OvaqReborn HwidAuthSystem", JOptionPane.INFORMATION_MESSAGE);

                IOUtil.sendDiscord(hwid);

                throw new SecurityException("Hwid認証に失敗しました。強制終了します。");
            }

        } catch (IOException e) {
            throw new RuntimeException("認証サーバーに接続できませんでした。強制終了します。", e);
        }
    }
}
