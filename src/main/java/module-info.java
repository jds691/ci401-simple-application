module com.neo.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.neo.twig;
    requires json.simple;

    opens com.neo.game to javafx.fxml, com.neo.twig;
    opens com.neo.game.audio to com.neo.twig;
    opens com.neo.game.input to com.neo.twig;
    opens com.neo.game.title to com.neo.twig;
    opens com.neo.game.ui to com.neo.twig;
    opens com.neo.game.settings to com.neo.twig;

    exports com.neo.game;
}