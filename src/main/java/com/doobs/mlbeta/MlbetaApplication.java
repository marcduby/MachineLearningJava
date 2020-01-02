package com.doobs.mlbeta;

import com.doobs.mlbeta.service.dl4j.SimpleNetworkService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;


@SpringBootApplication
public class MlbetaApplication {

	@Autowired
	private SimpleNetworkService simpleNetworkService;

	public static void main(String[] args) {
		SpringApplication.run(MlbetaApplication.class, args);

		System.out.println("Yo ML dude!!");

		// run the ML example

		System.out.println("Yo ML dude!!");


//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				JFrame jFrame = new JFrame("Test ML");
//				jFrame.setSize(500, 400);
//				jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				jFrame.setVisible(true);
//			}
//		});
	}

//	public static void main(String[] args) {
//		Application.launch(args);
//	}
//
//	@Override
//	public void start(Stage stage) {
//		stage.setTitle("Test ML Application");
//		Group newGroup = new Group();
//		Scene scene = new Scene(newGroup, 300, 250);
//
//		// create the button
//		Button button = new Button();
//		button.setLayoutX(100);
//		button.setLayoutY(80);
//		button.setText("Click Me");
//
//		// handler
//		button.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				System.out.println("Button clicked!!!");
//			}
//		});
//
//		// set the tree
//		newGroup.getChildren().add(button);
//		stage.setScene(scene);
//		stage.show();
//	}
}
