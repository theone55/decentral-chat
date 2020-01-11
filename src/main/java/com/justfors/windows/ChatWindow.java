package com.justfors.windows;

import com.dosse.upnp.UPnP;
import com.justfors.client.Client;
import com.justfors.network.Connection;
import com.justfors.server.Server;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class ChatWindow extends Application {

    private Connection connection;

    public static String nickname;
    public static String ip;
    public static Integer port;

    public static Double WIDTH = 300.0;
    public static Double HEIGHT = 400.0;

    public static final TextFlow text_flow = new TextFlow();
    public static Stage stage;
    public static ScrollPane scrollPane = new ScrollPane();
    public static Button sendMessage = new Button("Send message");
    public static TextArea textArea = new TextArea();
    public static HBox hBox = new HBox(textArea,sendMessage);

    public static String userName = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle("JFDC");
        Button btn = new Button();

        btn.setText("Connect");
        btn.setOnAction(event -> {
            stage.setScene(connectionDialog());
        });
        sendMessage.setOnAction(event -> {
            String message = textArea.getText();
            textArea.clear();
            connection.sendAll(message);
            stage.setScene(sendMessage(userName, message));
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.show();
    }

    private Scene connectionDialog(){
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(pane, WIDTH, HEIGHT);

        Text sceneTitle = new Text("Connection form");
        sceneTitle.setFont(Font.font("Arial", FontWeight.NORMAL,20));
        pane.add(sceneTitle, 0, 0, 2, 1);
        Label total = new Label("Nickname:");
        pane.add(total, 0, 1);
        final TextField nickname = new TextField();
        pane.add(nickname, 1, 1);
        Label percent = new Label("IP:PORT :");
        pane.add(percent,0,2);
        final TextField ipAndPort = new TextField();
        pane.add(ipAndPort, 1, 2);

        Button calculateButton = new Button("Connect");
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(calculateButton);
        pane.add(hbox, 1, 4);

        final Text taxMessage = new Text();
        pane.add(taxMessage, 1, 6);

        calculateButton.setOnAction(t -> {
            ChatWindow.nickname = nickname.getText();
            String[] connectionData = ipAndPort.getText().split(":");
            ChatWindow.ip = connectionData[0];
            ChatWindow.port = Integer.valueOf(connectionData[1]);
            startNetwork();
            stage.setScene(getChat());
        });
        return scene;
    }

    public static Scene sendMessage(String writer, String message){
        Text wrtr = new Text(writer + ":");
        wrtr.setFill(Color.GREEN);
        wrtr.setFont(Font.font("Verdana", 15));
        Text msg = new Text(message + "\n");
        msg.setFill(Color.GREEN);
        msg.setFont(Font.font("Helvetica", 15));

        text_flow.getChildren().add(wrtr);
        text_flow.getChildren().add(msg);

        scrollPane.setContent(text_flow);
        VBox vBox = new VBox(scrollPane,hBox);

        return new Scene(vBox, WIDTH, HEIGHT);
    }

    private Scene getChat(){
        scrollPane.setContent(text_flow);
        VBox vBox = new VBox(scrollPane,hBox);
        return new Scene(vBox, WIDTH, HEIGHT);
    }

    private void startNetwork(){
        connection = new Connection(ChatWindow.nickname);
        if(UPnP.openPortTCP(7777)) {
            new Server(7777, connection).start();
        }
        String host = ChatWindow.ip;
        Integer port = ChatWindow.port;
        new Client(host, port, connection).start();
        userName = ChatWindow.nickname + ":" + Server.getCurrentIP();
    }
}
