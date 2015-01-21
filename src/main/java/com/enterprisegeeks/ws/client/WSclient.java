package com.enterprisegeeks.ws.client;

import java.awt.TrayIcon;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 * WebSocket クライアント
 */
@ClientEndpoint
public class WSclient  {

    /** タスクトレイ */
    final private TrayIcon tray;
    
    /** WebSocketセッション */
    private Session mySession;
    
    /**
     * コンストラクタ
     * @param tray サーバーからのイベント受信にて、メッセージ表示を行う
     */
    public WSclient(TrayIcon tray) {
        this.tray = tray;
    }

    /** メッセージ送信
     * @param message メッセージ
     */
    public void sendMessage(String message) {
        try {
            mySession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /** クライアントからの切断 */
    public void close() {
        try {
            mySession.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    // 以下は、サーバーからの通知受信のためのコールバック
    
    @OnOpen
    public void open(Session session) {
        System.out.println(session.getId() + " was opened.");
        mySession = session;
    }
    
    @OnMessage
    public void onMessage(String message, Session ses)  {
        System.out.println("recieved:" + message);
        // trayにメッセージを表示。
        tray.displayMessage("受信", message, TrayIcon.MessageType.INFO);
    }
    
    @OnError
    public void error(Session session, Throwable e) {
        System.out.println(session.getId() + " was error.");
        e.printStackTrace();
        // trayにメッセージを表示。
        tray.displayMessage("エラー", e.getMessage(), TrayIcon.MessageType.ERROR);
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException ex) {
            }
        }
    }
    
    @OnClose
    public void close(Session session) {
        System.out.println(session.getId() + " was closed.");
    }
    
}
