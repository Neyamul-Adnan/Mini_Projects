import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage mainStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main_view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            mainStage.setTitle("Student Record Management System");
            mainStage.setScene(scene);
            mainStage.show();

        } catch (IOException e) {
            System.out.println("FXML File not found");
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}