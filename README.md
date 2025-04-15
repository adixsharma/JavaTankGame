# Tank Wars


| Student Information |                         |
|:-------------------:|-------------------------|
|  Student Name       | Aditya Sharma           |
|  Student Email      | asharma15@mail.sfsu.edu |


## Purpose of jar Folder 
The jar folder will be used to store the built jar of your term-project.

## Steps to Run the JAR File

### Using Terminal

1. **Open Terminal**: Open a terminal window on your computer.

2. **Navigate to the Directory Containing the JAR File**: Use the `cd` command to navigate to the directory where your JAR file is located. For example:
   ```sh
   cd path/to/your/jar/directory
   ```

3. **Run the JAR File**:
    - Use the `java -jar` command followed by the name of your JAR file to run it. For example, if your JAR file is named `tankgame.jar`, you would run:
      ```sh
      java -jar csc413-tankgame-adixsharma.jar
      ```
    - This command will execute the JAR file, and the application will start running.

### Example

Assuming your JAR file is named `csc413-tankgame-adixsharma.jar` and is located in the `~/projects/tankgame` directory, the commands would be:

```sh
cd ~/projects/tankgame
java -jar csc413-tankgame-adixsharma.jar
```

By following these steps, you can run your JAR file from the terminal.
# Game Information

## Version of Java Used: 22.0.1

## IDE used: IntelliJ IDEA

## Steps to Import project into IDE:
1. #### Download the Project Zip File: Go to the GitHub repository and click on the Code button, then select Download ZIP. Save the file to your local machine.  
2. #### Extract the Zip File: Navigate to the location where you saved the zip file and extract its contents to a desired directory.  
3. ####  Open IntelliJ IDEA: Launch IntelliJ IDEA on your computer.  
4. #### Open the Project: In IntelliJ IDEA, click on File -> Open.  
5. #### Navigate to the Extracted Directory: In the file chooser dialog, navigate to the directory where you extracted the zip file.  
6. #### Select the Project Folder: Select the root folder of the extracted project and click OK.  
7. #### Trust the Project: If prompted, select Trust Project to allow IntelliJ IDEA to open and configure the project.  
8. #### Wait for IntelliJ IDEA to Import the Project: IntelliJ IDEA will automatically detect the project structure and import it. This may take a few moments.  
9. #### Verify Project Structure: Ensure that the project structure is correctly recognized by IntelliJ IDEA. You should see the project files and folders in the Project view.  
10. #### Configure SDK: If necessary, configure the Java SDK by going to File -> Project Structure -> Project and selecting the appropriate SDK version.  

[//]: # (11. #### Build the Project: Once the project is imported, you can build it by clicking on Build -> Build Project or using the shortcut Cmd + F9.  )

[//]: # (12. #### Run the Project: To run the project, locate the Launcher.java class, right-click on it, and select Run 'Main.main&#40;&#41;'.)

## Steps to Build your Project:

### Using IntelliJ IDEA

1. **Open IntelliJ IDEA**: Launch IntelliJ IDEA on your computer.

2. **Open the Project**: Click on `File` -> `Open` and navigate to the root directory of your project. Select the project folder and click `OK`.

3. **Configure SDK**: Ensure that the correct Java SDK is configured:
    - Go to `File` -> `Project Structure`.
    - Under `Project Settings`, select `Project`.
    - Ensure the `Project SDK` is set to the appropriate version (e.g., 22.0.1).

4. **Build the Project**:
    - Click on `Build` -> `Build Project` or use the shortcut `Cmd + F9`.
    - Wait for the build process to complete. IntelliJ IDEA will compile the project and display any errors or warnings in the `Build` tool window.

5. **Verify Build Output**:
    - Ensure that the build was successful by checking the `Build` tool window for any errors.
    - The compiled classes will be located in the `out` directory within your project folder.

### Using Terminal

1. **Open Terminal**: Open a terminal window on your computer.

2. **Navigate to Project Directory**: Use the `cd` command to navigate to the root directory of your project. For example:
   ```sh
   cd path/to/your/project
   ```

3. **Compile the Project**:
    - Ensure you have the correct Java version installed. You can check your Java version with:
      ```sh
      java -version
      ```
    - Compile all the Java source files in your project using the `javac` command. Assuming your source files are in the `src` directory:
      ```sh
      javac -d out $(find src -name "*.java")
      ```
    - This command compiles all `.java` files in the `src` directory and places the compiled `.class` files in the `out` directory.

4. **Run the Project**:
    - After compiling, you can run the project using the `java` command. Ensure you are in the root directory of your project and run:
      ```sh
      java -cp out tankProgram.game.Launcher
      ```
    - This command runs the `Launcher` class from the `out` directory.
 
## Steps to run the Project:

### Using IntelliJ IDEA

1. **Open IntelliJ IDEA**: Launch IntelliJ IDEA on your computer.

2. **Open the Project**: Click on `File` -> `Open` and navigate to the root directory of your project. Select the project folder and click `OK`.

3. **Configure SDK**: Ensure that the correct Java SDK is configured:
    - Go to `File` -> `Project Structure`.
    - Under `Project Settings`, select `Project`.
    - Ensure the `Project SDK` is set to the appropriate version (e.g., 22.0.1).

4. **Locate the Main Class**: In the `Project` view, navigate to the `src` directory and locate the main class file (e.g., `Launcher.java`).

5. **Run the Main Class**:
    - Right-click on the main class file (`Launcher.java`).
    - Select `Run 'Launcher.main()'` from the context menu.
    - IntelliJ IDEA will compile and run the project, and the output will be displayed in the `Run` tool window.

### Using Terminal

1. **Open Terminal**: Open a terminal window on your computer.

2. **Navigate to Project Directory**: Use the `cd` command to navigate to the root directory of your project. For example:
   ```sh
   cd path/to/your/project
   ```

3. **Compile the Project**:
    - Ensure you have the correct Java version installed. You can check your Java version with:
      ```sh
      java -version
      ```
    - Compile all the Java source files in your project using the `javac` command. Assuming your source files are in the `src` directory:
      ```sh
      javac -d out $(find src -name "*.java")
      ```
    - This command compiles all `.java` files in the `src` directory and places the compiled `.class` files in the `out` directory.

4. **Run the Project**:
    - After compiling, you can run the project using the `java` command. Ensure you are in the root directory of your project and run:
      ```sh
      java -cp out tankProgram.game.Launcher
      ```
    - This command runs the `Launcher` class from the `out` directory.

## Controls to play your Game:

|               | Player 1 | Player 2 |
|---------------|----------|----------|
|  Forward      | W        | I        |
|  Backward     | S        | K        |
|  Rotate left  | A        | J        |
|  Rotate Right | D        | L        |
|  Shoot        | SPACEBAR | N        |

## Rules of the Game:

### Game Objective
The main goal is to destroy the opponent's tank by shooting bullets and strategically navigating the game world.

### Players
The game is designed for two players:
- **Player 1** controls Tank 1.
- **Player 2** controls Tank 2.

### Health and Lives
- Each tank starts with 100 health points and 3 lives.
- When a tank's health reaches 0, it loses a life and is reset to its starting position with full health.
- If a tank loses all its lives, the game ends, and the opposing player wins.

### Power-Ups
The game features power-ups/down that appear when certain walls are destroyed. These power-ups or power-down provide temporary benefits or detriments for one life:
- **Double Damage**: Increases the damage dealt by the tank's bullets.
- **Health Boost**: Increases the tank's health to 200 points.
- **Half Health**: Reduces the tank's health by half. Immediately once for that life.

### Walls
The game map contains both breakable and unbreakable walls:
- **Unbreakable Walls**: Indestructible and block the movement of tanks and bullets.
- **Breakable Walls**: Can be destroyed by shooting bullets at them. Some breakable walls may contain power-ups.

### Collision Handling
- Tanks will stop moving when colliding with walls or other tanks.
- Bullets will be destroyed upon collision with walls or tanks.
- Power-ups/power-down will be applied and then disappear from the map upon a tank colliding with the item after the breakable wall containing it has been destroyed.

### Winning the Game
- The game continues until one player's tank loses all its lives.
- The player whose tank remains with at least one life is declared the winner.

### User Interface
- The game features a split-screen view, with Player 1's view on the left and Player 2's view on the right.
- A mini-map is displayed at the top center of the screen, showing the entire game world.
- Each player's remaining lives and health are displayed at the top of the screen, with health bars and heart icons.
