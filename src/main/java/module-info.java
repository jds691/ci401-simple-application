module com.neo.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.neo.twig;

    opens com.neo.game to javafx.fxml, com.neo.twig;
    opens com.neo.game.audio to com.neo.twig;
    opens com.neo.game.title to com.neo.twig;
    opens com.neo.game.testing to com.neo.twig;

    exports com.neo.game;
}