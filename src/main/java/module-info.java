module com.neo.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.neo.twig;
    requires json.simple;
    requires org.xerial.sqlitejdbc;
    requires java.sql;
    requires jdk.jshell;
    requires java.desktop;

    opens com.neo.game to javafx.fxml, com.neo.twig;
    opens com.neo.game.audio to com.neo.twig;
    opens com.neo.game.input to com.neo.twig;
    opens com.neo.game.title to com.neo.twig;
    opens com.neo.game.leaderboard to com.neo.twig, javafx.base;
    opens com.neo.game.message to com.neo.twig;
    opens com.neo.game.ui to com.neo.twig;
    opens com.neo.game.settings to com.neo.twig;

    exports com.neo.game;
}