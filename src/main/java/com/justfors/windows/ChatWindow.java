package com.justfors.windows;

import com.dosse.upnp.UPnP;
import com.justfors.client.Client;
import com.justfors.network.Connection;
import com.justfors.server.Server;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class ChatWindow extends Application {

    public static String nickname;
    public static String ip;
    public static Integer port;

    private static final TextFlow text_flow = new TextFlow();
    private static Stage stage;

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
            primaryStage.setScene(connectionDialog());
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    private Scene connectionDialog(){
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(pane, 300, 275);

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
            stage.setScene(chatDialog());
        });
        return scene;
    }

    private Scene chatDialog(){
        // create text
        Text text_1 = new Text("GeeksforGeeks\n");

        // set the text color
        text_1.setFill(Color.RED);

        // set font of the text
        text_1.setFont(Font.font("Verdana", 25));

        // create text
        Text text_2 = new Text("The computer science portal for geeks");

        // set the text color
        text_2.setFill(Color.BLUE);

        // set font of the text
        text_2.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

        // add text to textflow
        text_flow.getChildren().add(text_1);
        text_flow.getChildren().add(text_2);

        // create a scene
        Scene scene = new Scene(text_flow, 400, 300);
        return scene;
    }


    private void startNetwork(){
        Connection connection = new Connection(ChatWindow.nickname);
        if(UPnP.openPortTCP(7777)) {
            new Server(7777, connection).start();
        }
        String host = ChatWindow.ip;
        Integer port = ChatWindow.port;
        new Client(host, port, connection).start();
    }
}
