package seedu.address.logic.commands;

import seedu.address.model.Model;

import static java.util.Objects.requireNonNull;

/**
 * Finds and lists all persons in address book who are marked as high priority.
 */
public class FindMeetingCommand extends Command {

    public static final String COMMAND_WORD = "meetings";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all meetings with "
            + "the respective contacts and displays them as a list.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed contacts with meetings";

    public static final String MESSAGE_NO_CONTACTS_FOUND = "Oops! No contacts found with meetings.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(person -> !person.getMeeting().toString().equals(""));
        if (model.getFilteredPersonList().isEmpty()) {
            return new CommandResult(MESSAGE_NO_CONTACTS_FOUND);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }
}