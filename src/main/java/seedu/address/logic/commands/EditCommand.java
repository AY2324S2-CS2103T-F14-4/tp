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
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Priority;
import seedu.address.model.person.Company;
import seedu.address.model.person.Meeting;
import seedu.address.model.person.Remark;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the name used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: " + PREFIX_NAME + "NAME "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " Alex Tan "
            + PREFIX_NAME + "Alex";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Name name;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param name of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Name name, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(name);
        requireNonNull(editPersonDescriptor);

        this.name = name;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Optional<Person> personOptional = lastShownList.stream()
                .filter(person -> person.getName().equals(name))
                .findFirst();

        if (personOptional.isEmpty()) {
            throw new CommandException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, name));
        }

        Person personToEdit = personOptional.get();
        Person editedPerson = editPersonDescriptor.createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson));
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Priority priority;
        private Boolean star;
        private Set<Tag> tags;
        private Meeting meeting;
        private Remark remark;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setPriority(toCopy.priority);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public Optional<Priority> getPriority() {
            return Optional.ofNullable(priority);
        }

        public void setStar(Boolean star) {
            this.star = star;
        }

        public Optional<Boolean> isStarred() {
            return Optional.ofNullable(star);
        }

        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public Optional<Meeting> getMeeting() {
            return Optional.ofNullable(meeting);
        }

        private void setMeeting(Meeting meeting) {
            this.meeting = meeting;
        }

        private Optional<Remark> getRemark() {
            return Optional.ofNullable(remark);
        }

        private void setRemark(Remark remark) {
            this.remark = remark;
        }

        /**
         * Creates and returns a {@code Person} with the details of {@code personToEdit}
         * edited with {@code editPersonDescriptor}.
         */
        public static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
            assert personToEdit != null;

            Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
            Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
            Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
            Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
            Company updatedCompany = personToEdit.getCompany();
            Priority updatedPriority = personToEdit.getPriority();
            Boolean updatedStar = personToEdit.isStarred();
            Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
            Meeting updatedMeeting = personToEdit.getMeeting();
            Remark updatedRemark = personToEdit.getRemark();

            return new Person(updatedName, updatedPhone, updatedEmail,
                    updatedAddress, updatedCompany, updatedMeeting,
                    updatedPriority, updatedStar, updatedRemark, updatedTags);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherDescriptor = (EditPersonDescriptor) other;
            return getName().equals(otherDescriptor.getName())
                    && getPhone().equals(otherDescriptor.getPhone())
                    && getEmail().equals(otherDescriptor.getEmail())
                    && getAddress().equals(otherDescriptor.getAddress())
                    && getPriority().equals(otherDescriptor.getPriority())
                    && getTags().equals(otherDescriptor.getTags());
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("priority", priority)
                    .add("star", star)
                    .add("tags", tags)
                    .add("meeting", meeting)
                    .add("remark", remark)
                    .toString();
        }
    }
}
