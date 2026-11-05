# TicTacToeJavaFX

## Run the JavaFX game

The project requires JDK 23 and Maven. From the project root, run:

```bash
cd demo
mvn clean javafx:run
```

Click an empty square to place X. The CPU responds with O, and the Restart
game button clears the board for another round.

## Run the original console game

From the `demo` directory, compile and run the console version with:

```bash
mvn compile
java -cp target/classes com.example.TicTacToeConsole
```
