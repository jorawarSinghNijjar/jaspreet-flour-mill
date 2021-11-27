package com.jaspreetFlourMill.accountManagement;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class AppUI extends Application{
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage stage) throws Exception {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void init() throws Exception {
        applicationContext = new SpringApplicationBuilder(AccountManagementApplication.class).run();
    }

    @Override
    public void stop() throws Exception {
        applicationContext.close();
        Platform.exit();
    }

//    static class StageReadyEvent extends ApplicationEvent {
//        public StageReadyEvent(Stage stage) {
//            super(stage);
//        }
//
//        public Stage getStage(){
//            return ((Stage) getSource());
//        }
//    }
}
