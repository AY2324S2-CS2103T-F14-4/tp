package seedu.address.logic.commands;

import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.PersonBuilder;

class RemarkCommandTest {

    private static final String TEST_REMARK = "test remark";
    private Model actualModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    void execute_addRemarkUnfilteredList_success() {
        Person firstPersonWithoutRemark = actualModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstPersonWithRemark = new PersonBuilder(firstPersonWithoutRemark).withRemark(TEST_REMARK).build();
        RemarkCommand firstCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(firstPersonWithRemark.getRemark().value));
        String firstExpectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, firstPersonWithRemark);
        Model firstExpectedModel = new ModelManager(new AddressBook(actualModel.getAddressBook()), new UserPrefs());
        firstExpectedModel.setPerson(firstPersonWithoutRemark, firstPersonWithRemark);
        assertCommandSuccess(firstCommand, actualModel, firstExpectedMessage, firstExpectedModel);

        Person secondPersonWithoutRemark = actualModel.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondPersonWithRemark = new PersonBuilder(secondPersonWithoutRemark).withRemark(TEST_REMARK).build();
        RemarkCommand secondCommand = new RemarkCommand(INDEX_SECOND_PERSON, new Remark(secondPersonWithRemark.getRemark().value));
        String secondExpectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, secondPersonWithRemark);
        Model secondExpectedModel = new ModelManager(new AddressBook(actualModel.getAddressBook()), new UserPrefs());
        secondExpectedModel.setPerson(secondPersonWithoutRemark, secondPersonWithRemark);
        assertCommandSuccess(secondCommand, actualModel, secondExpectedMessage, secondExpectedModel);

        Person thirdPersonWithoutRemark = actualModel.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        Person thirdPersonWithRemark = new PersonBuilder(thirdPersonWithoutRemark).withRemark(TEST_REMARK).build();
        RemarkCommand thirdCommand = new RemarkCommand(INDEX_THIRD_PERSON, new Remark(thirdPersonWithRemark.getRemark().value));
        String thirdExpectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, thirdPersonWithRemark);
        Model thirdExpectedModel = new ModelManager(new AddressBook(actualModel.getAddressBook()), new UserPrefs());
        thirdExpectedModel.setPerson(thirdPersonWithoutRemark, thirdPersonWithRemark);
        assertCommandSuccess(thirdCommand, actualModel, thirdExpectedMessage, thirdExpectedModel);
    }
}
