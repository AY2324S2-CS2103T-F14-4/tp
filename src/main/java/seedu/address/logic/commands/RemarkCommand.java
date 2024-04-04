package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;


/**
 * Changes the remark of an existing person in the address book.
 */
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the remark of the person identified "
            + "by the contact name used in Connectify. "
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: <contact name> \n"
            + "r/ [REMARK]\n"
            + "Example: " + COMMAND_WORD + " Alex Yeoh "
            + "r/ Likes to swim.";

    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to %1$s's contact";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from %1$s's contact";
    public static final String MESSAGE_DELETE_REMARK_FAILURE =
            "Error! %1$s's contact does not have a remark to remove.";
    public static final String MESSAGE_PERSON_NOT_FOUND = "Oops, %1$s's contact does not exist. Unable to add "
            + "remark";
    private final String name;
    private final Remark remark;
    private String message;

    /**
     * @param name of the person in the filtered person list to edit the remark
     * @param remark of the person to be updated to
     */
    public RemarkCommand(String name, Remark remark) {
        requireAllNonNull(name, remark);

        this.name = name;
        this.remark = remark;
    }
    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        List<Person> contactList = model.getFilteredPersonList();
        Person personToEdit = null;
        for (Person person : contactList) {
            if (person.getName().fullName.equalsIgnoreCase(name)) {
                personToEdit = person;
                break;
            }
        }
        if (personToEdit == null) {
            throw new CommandException(String.format(MESSAGE_PERSON_NOT_FOUND, name));
        }
        if (!remark.hasRemark()) {
            if (personToEdit.getRemark().hasRemark()) {
                message = MESSAGE_DELETE_REMARK_SUCCESS;
            } else {
                throw new CommandException(String.format(MESSAGE_DELETE_REMARK_FAILURE, name));
            }
        } else {
            message = MESSAGE_ADD_REMARK_SUCCESS;
        }

        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(),
                personToEdit.getEmail(), personToEdit.getAddress(),
                personToEdit.getCompany(), personToEdit.getMeeting(),
                personToEdit.getPriority(), personToEdit.isStarred(),
                remark, personToEdit.getTags());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether
     * the remark is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        String message = !remark.value.isEmpty() ? MESSAGE_ADD_REMARK_SUCCESS : MESSAGE_DELETE_REMARK_SUCCESS;
        return String.format(message, personToEdit.getName());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        RemarkCommand e = (RemarkCommand) other;
        return name.equals(e.name)
                && remark.equals(e.remark);
    }
}
