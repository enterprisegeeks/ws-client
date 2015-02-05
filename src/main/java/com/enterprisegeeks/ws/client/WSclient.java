package com.enterprisegeeks.ws.client;

import com.enterprisegeeks.ws.data.Message;
import com.enterprisegeeks.ws.data.MessageDecoder;
import java.awt.TrayIcon;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 * WebSocket クライアント
 */
@ClientEndpoint(decoders = MessageDecoder.class)
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
    public void onMessage(Message message, Session ses)  {
        System.out.println("recieved:" + message.message);
        // trayにメッセージを表示。
        tray.displayMessage("From [" + message.name +"]",
                message.message, TrayIcon.MessageType.INFO);
    }
    
    @OnMessage
    public void onBinary(ByteBuffer buf, Session ses)  {
        System.out.println("recieved:binary");
        // trayにメッセージを表示。
        String home = System.getProperty("user.home");
        File output = new File(home, System.currentTimeMillis()+"");
        File result;
        try(FileOutputStream os = new FileOutputStream(output);
                FileChannel oc = os.getChannel()){
            oc.write(buf);
            result = addImageSuffix(output);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        output.renameTo(result);
        tray.displayMessage("画像ファイルを受信", result.getAbsolutePath(), TrayIcon.MessageType.INFO);
    }
    
    private File addImageSuffix(File file) throws IOException{
       try(ImageInputStream iis = ImageIO.createImageInputStream(file)){
           Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
           String suffix = readers.next().getOriginatingProvider().getFileSuffixes()[0];
           File rename = new File(file.getAbsolutePath() + "." + suffix);
           return rename;
       }
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
