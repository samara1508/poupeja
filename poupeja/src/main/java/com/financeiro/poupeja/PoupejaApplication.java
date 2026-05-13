package com.financeiro.poupeja;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import com.financeiro.poupeja.util.SpringFXMLLoader;

@SpringBootApplication
public class PoupejaApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private static Stage primaryStage;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);
        this.applicationContext = new SpringApplicationBuilder()
                .sources(PoupejaApplication.class)
                .run(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        PoupejaApplication.primaryStage = stage;
        SpringFXMLLoader loader = applicationContext.getBean(SpringFXMLLoader.class);
        
        Parent root = loader.load("/fxml/login.fxml");
        stage.setTitle("PoupeJá! - Login");
        stage.setScene(new Scene(root, 400, 500));
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        Application.launch(PoupejaApplication.class, args);
    }

}
