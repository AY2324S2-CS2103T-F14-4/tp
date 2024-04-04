package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Company;
import seedu.address.model.person.Person;


/**
 * Changes the company of an existing person in the address book.
 */
public class CompanyCommand extends Command {

    public static final String COMMAND_WORD = "co";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a company to the person identified by the contact name "
            + "Existing company will be overwritten by the input.\n"
            + "c/ [COMPANY_NAME]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "c/ Friends";

    public static final String MESSAGE_ADD_COMPANY_SUCCESS = "Tagged %1$s's company as %2$s";
    public static final String MESSAGE_DELETE_COMPANY_SUCCESS = "Removed the company tag from %1$s's contact";
    public static final String MESSAGE_ADD_COMPANY_WARN = "Changed the existing company tag for %1$s's contact\n"
            + "Previous company tag: %3$s\n" + "Updated company tag: %2$s";
    public static final String MESSAGE_PERSON_NOT_FOUND = "Oops, %1$s's contact does not exist.\nUnable to add "
            + "company tag";
    public static final String MESSAGE_EMPTY_NAME = "Oops, please state the name of the contact";
    public static final String MESSAGE_DELETE_COMPANY_FAILURE =
            "Error! %1$s's contact does not have a company tag to remove.";

    private final String name;
    private final Company company;
    private String message;

    /**
     * @param name  of the person in the filtered person list to edit the company
     * @param company of the person to be updated to
     */
    public CompanyCommand(String name, Company company) {
        requireAllNonNull(name, company);

        this.name = name;
        this.company = company;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (name.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_EMPTY_NAME, name));
        }
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
        String currentCompany = personToEdit.getCompany().value;
        if (!company.hasCompany()) {
            if (personToEdit.getCompany().hasCompany()) {
                message = MESSAGE_DELETE_COMPANY_SUCCESS;
            } else {
                throw new CommandException(String.format(MESSAGE_DELETE_COMPANY_FAILURE, name));
            }
        } else {
            message = MESSAGE_ADD_COMPANY_SUCCESS;
        }

        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), company, personToEdit.getMeeting(), personToEdit.getPriority(),
                personToEdit.isStarred(), personToEdit.getRemark(), personToEdit.getTags());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson, currentCompany));
    }

    /**
     * Generates a command execution success message based on whether
     * the company is added to or removed from
     * {@code personToEdit}.
     */

    private String generateSuccessMessage(Person personToEdit, String prevCo) {
        String message = !company.value.isEmpty() ? (prevCo.isEmpty() ? MESSAGE_ADD_COMPANY_SUCCESS
                : MESSAGE_ADD_COMPANY_WARN) : MESSAGE_DELETE_COMPANY_SUCCESS;
        return String.format(message, personToEdit.getName(), company, prevCo);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CompanyCommand)) {
            return false;
        }

        CompanyCommand e = (CompanyCommand) other;
        return name.equals(e.name)
                && company.equals(e.company);
    }
}
