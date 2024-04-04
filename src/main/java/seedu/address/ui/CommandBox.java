package seedu.address.ui;

import java.util.Stack;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;

    @FXML
    private TextField commandTextField;

    private Stack<String> pastCommands;
    private Stack<String> temp;

    private boolean preIsUp;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.pastCommands = new Stack<>();
        this.temp = new Stack<>();
        this.preIsUp = true;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }
        while (!temp.isEmpty()) {
            pastCommands.push(temp.pop());
        }
        pastCommands.push(commandText);
        preIsUp = true;

        try {
            commandExecutor.execute(commandText);
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        } finally {
            commandTextField.setText("");
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }
    /**
     * Allow User to go to Previous Command by pressing the up arrow
     */
    public void handleUp() {
        if (this.pastCommands.empty()) {
            return;
        }
        String toDisplay = pastCommands.pop();
        temp.push(toDisplay);
        commandTextField.setText(toDisplay);
        if (!preIsUp) {
            preIsUp = true;
            handleUp();
        }
        preIsUp = true;
    }

    /**
     * Allow User to go to Previous Command by pressing the down arrow
     */

    public void handleDown() {
        if (this.temp.empty()) {
            return;
        }
        String toDisplay = temp.pop();
        pastCommands.push(toDisplay);
        commandTextField.setText(toDisplay);
        if (preIsUp) {
            preIsUp = false;
            handleDown();
        }
        preIsUp = false;
    }

}
