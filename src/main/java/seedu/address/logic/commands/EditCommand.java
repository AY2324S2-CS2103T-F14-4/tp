package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Company;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Priority;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the contact name. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: CONTACT_NAME "
            + "[" + PREFIX_NAME + "NAME]\n"
            + "Example: " + COMMAND_WORD + " Alex Tan " + PREFIX_NAME + "Samuel";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited %1$s's contact details.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    private final String contactName;

    public static class EditPersonDescriptor {
        private Name name;

        public EditPersonDescriptor() {}

        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }
    }

    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * Constructor for EditCommand.
     * @param contactName The name of the contact to be edited.
     * @param editPersonDescriptor The descriptor containing the edited fields.
     */
    public EditCommand(String contactName, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(contactName);
        requireNonNull(editPersonDescriptor);

        this.contactName = contactName;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    private Optional<Person> findPersonByName(Model model, String name) {
        return model.getFilteredPersonList().stream()
                .filter(person -> person.getName().fullName.equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Person> personOptional = findPersonByName(model, contactName);
        if (personOptional.isEmpty()) {
            throw new CommandException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, contactName));
        }

        Person personToEdit = personOptional.get();
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, contactName));
    }


    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;
      
        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Company updatedCompany = personToEdit.getCompany();
        Priority updatedPriority = personToEdit.getPriority();
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        return new Person(updatedName, updatedPhone, updatedEmail,
                updatedAddress, updatedCompany, updatedPriority, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return contactName.equals(otherEditCommand.contactName)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }
}
