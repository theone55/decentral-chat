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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.HashSet;

public class ChatWindow extends Application {

    private Connection connection;
    private static HashSet<Text> onlineMember = new HashSet<>();
    private static VBox chatBox;

    public static String nickname;
    public static String ip;
    public static Integer port;

    public static Double WIDTH = 300.0;
    public static Double HEIGHT = 400.0;

    public static final TextFlow textFlow = new TextFlow();
    public static TextFlow onlineList = new TextFlow();
    public static Stage stage;
    public static ScrollPane scrollPane = new ScrollPane();
    public static Button sendMessage = new Button("Send message");
    public static TextArea textArea = new TextArea();
    public static HBox hBox = new HBox(textArea,sendMessage);
    public static Scene currentScene;

    public static String userName = null;

    {
        scrollPane.setFitToWidth(true);
        textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        textArea.setWrapText(true);
        textArea.setMaxHeight(HEIGHT);
        textArea.setMaxWidth(WIDTH);
        textArea.setMinHeight(HEIGHT);
        textArea.setMinWidth(WIDTH);
        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setMinWidth(WIDTH);
        stage.setMinHeight(HEIGHT);
        stage.setTitle("JFDC");
        Button btn = new Button();

        btn.setText("Connect");
        btn.setOnAction(event -> {
            stage.setScene(connectionDialog());
        });
        sendMessage.setOnAction(event -> {
            sendMessage();
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.show();
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

    public static void sendMessage(String writer, String message){
        Text wrtr = new Text(writer + ":");
        wrtr.setFill(Color.GREEN);
        wrtr.setFont(Font.font("Verdana", 15));
        Text msg = new Text(message + "\n");
        msg.setFill(Color.GREEN);
        msg.setFont(Font.font("Helvetica", 15));

        textFlow.getChildren().add(wrtr);
        textFlow.getChildren().add(msg);

        scrollPane.setContent(textFlow);
        chatBox = new VBox(scrollPane,hBox);

        refreshScene();
    }

    private Scene getChat(){
        scrollPane.setContent(textFlow);
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

    private void sendMessage(){
        String message = textArea.getText();
        textArea.clear();
        connection.sendAll(message);
        sendMessage(userName, message);
    }

    public static void login(String userData){
        Text member = new Text(userData + "\n");
        onlineMember.add(member);
        onlineList.getChildren().add(member);
        refreshScene();
    }

    public static void logout(String userData){
        for (Text text : onlineMember) {
            if (text.getText().equals(userData + "\n")) {
                onlineList.getChildren().remove(text);
                onlineMember.remove(text);
                break;
            }
        }
        refreshScene();
    }

    public static void refreshScene(){
        HBox hBox = new HBox(onlineList, chatBox);
        stage.setScene(new Scene(hBox, WIDTH, HEIGHT));
    }

}
