package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

public class EditCommandTest {

    @Test
    public void execute_editName_success() {
        Person originalPerson = new PersonBuilder().build();
        Person updatedPerson = new PersonBuilder(originalPerson)
                .withName("New Name")
                .build();
        EditCommand.EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("New Name")
                .build();
        EditCommand.EditPersonDescriptor expectedDescriptor = new EditCommand.EditPersonDescriptor(descriptor);
        expectedDescriptor.setName(new Name("New Name"));

        assertEquals(expectedDescriptor, descriptor);
    }
}
