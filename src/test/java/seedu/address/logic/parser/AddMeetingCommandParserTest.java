package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Meeting;

public class AddMeetingCommandParserTest {
    //Some test cases were generated with the help of ChatGPT 3.

    private AddMeetingCommandParser parser = new AddMeetingCommandParser();

    @Test
    public void parse_validArgs_returnsAddMeetingCommand() throws ParseException {
        String args = "John m/Meeting time/23-03-2025 1400-1500";
        AddMeetingCommand expectedCommand = new AddMeetingCommand("John",
                new Meeting("Meeting", "23-03-2025", "1400", "1500"));
        assertParseSuccess(parser, args, expectedCommand);
    }

    @Test
    public void parse_validArgs_returnsDeleteMeetingCommand() throws ParseException {
        String args = "John m/";
        AddMeetingCommand expectedCommand = new AddMeetingCommand("John",
                new Meeting("", "", "", ""));
        assertParseSuccess(parser, args, expectedCommand);
    }

    @Test
    public void parse_missingContactName_throwsParseException() {
        String args = " m/ time/23-03-2024 1400-1500";
        assertParseFailure(parser, args, AddMeetingCommandParser.MESSAGE_EMPTY_NAME);
    }

    @Test
    public void parse_missingDescription_throwsParseException() {
        String args = "John m/ time/23-03-2024 1400-1500";
        assertParseFailure(parser, args, AddMeetingCommandParser.MESSAGE_EMPTY_DESC);
    }

    @Test
    public void parse_missingTiming_throwsParseException() {
        String args = "John m/interview";
        assertParseFailure(parser, args, AddMeetingCommandParser.MESSAGE_EMPTY_TIMING);
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        String args = "John m/Meeting time/31-02-2025 1400-1500";
        assertParseFailure(parser, args, AddMeetingCommandParser.MESSAGE_INVALID_DATETIME);
    }

    @Test
    public void parse_invalidTime_throwsParseException() {
        String args = "John m/Meeting time/23-03-2025 1400-2400";
        assertParseFailure(parser, args, AddMeetingCommandParser.MESSAGE_INVALID_DATETIME);
    }

    @Test
    public void parse_invalidTimeInPast_throwsParseException() {
        String args = "John m/Meeting time/23-03-1999 1400-1500";
        assertParseFailure(parser, args, AddMeetingCommandParser.MESSAGE_TIME_IN_PAST);
    }

    @Test
    public void parse_illogicalTime_throwsParseException() {
        String args = "John m/Meeting time/23-03-2025 1900-1200";
        assertParseFailure(parser, args, AddMeetingCommandParser.MESSAGE_TIMING_BACKWARDS);
    }

}
