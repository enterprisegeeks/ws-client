package com.enterprisegeeks.ws.client;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

/**
 * WebScoketからの通知を受け取るタスクトレイ
 */
public class WebSocketNotifierTray {

    /**
     * コンストラクタ 各種設定を行う
     * @param url WS URL
     */
    public WebSocketNotifierTray(String url) throws IOException, AWTException, 
            DeploymentException, URISyntaxException {

        
        // イアイコン
        Image icon = ImageIO.read(getClass().getResourceAsStream("/icon.png"));
        final TrayIcon tray = new TrayIcon(icon);
        
        // WebSocketクライアント
        final WSclient client = new WSclient(tray);
        // サーバーへ接続
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(client, new URI(url));
        
        // Pingメニュー:クライアントから、サーバーへの　"ping"メッセージ送信
        MenuItem ping = new MenuItem("ping");
        ping.addActionListener(e -> client.sendMessage("ping"));
        
        // 終了メニュー
        MenuItem exit = new MenuItem("exit");
        exit.addActionListener(e -> {
            client.close();
            System.exit(0);
        });
        
        // ポップアップメニュー追加
        PopupMenu menu = new PopupMenu();
        menu.add(ping);
        menu.add(exit);
        
        tray.setPopupMenu(menu);
        
        // タスクトレイ格納
        SystemTray.getSystemTray().add(tray);
    }

    /** 
     * 起動 WebScoket接続URLはプログラム引数で与える
     * 
     * @param args 0は接続先のURL。無い場合デフォルト設定
     */
    public static void main(String[] args) throws Exception {
        
        String defaultUrl = "ws://java-ee-example.herokuapp.com/java_ee_example/pingpong";
        
        String url = args.length == 0 ? defaultUrl : args[0];
        
        new WebSocketNotifierTray(url);
    }
}