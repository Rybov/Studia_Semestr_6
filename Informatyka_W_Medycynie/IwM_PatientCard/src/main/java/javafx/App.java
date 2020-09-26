package javafx;

import Database.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    static Database database;
    static Scene scene;
    static Stage stage;
    @Override
    public void start(Stage primaryStage){
        stage=primaryStage;
        database = new Database();
        try
        {
            Parent root = getFXMl("menu");
            scene = new Scene(root);
        }catch (IOException e){e.printStackTrace();}
        stage.setScene(scene);
        stage.show();

    }
    public static Parent getFXMl(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource(fxml+".fxml"));
        return fxmlLoader.load();
    }


    public static void main(String[] args) {
        launch(args);

    }
}