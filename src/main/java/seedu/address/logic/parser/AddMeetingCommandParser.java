package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEETING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import seedu.address.logic.commands.AddMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Meeting;

/**
 * Parses input arguments and creates a new {@code AddMeetingCommand} object
 */
public class AddMeetingCommandParser implements Parser<AddMeetingCommand> {

    private static String MESSAGE_PARSE_ERROR = "Oops, please input the meeting command in the following format:\n"
            + "mtg <contact_name> m/<mtg_description> time/dd-MM-YYYY HHmm-HHmm\n"
            + "Example: mtg alex m/interview time/23-03-2024 1400-1500";

    private static String MESSAGE_EMPTY_DESC = "Oops, please input the description of your meeting.\n"
            + "Example: mtg alex m/interview time/23-03-2024 1400-1500";

    private static String MESSAGE_EMPTY_TIME = "Oops, please input the timing as well, in the format:\n"
            + "time/dd-MM-YYYY HHmm-HHmm\n"
            + "Example: mtg alex m/interview time/23-03-2024 1400-1500";

    /**
     * Parses the given {@code String} of arguments in the context of the AddMeetingCommand
     * and returns a AddMeetingCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddMeetingCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_MEETING, PREFIX_TIME);

        String contactName;
        contactName = argMultimap.getPreamble();

        String meeting = argMultimap.getValue(PREFIX_MEETING).orElse("");
        String time = argMultimap.getValue(PREFIX_TIME).orElse("");
        if (meeting.isEmpty() && !time.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_DESC);
        } else if (meeting.isEmpty()) {
            return new AddMeetingCommand(contactName, new Meeting("", "", "", ""));
        } else if (time.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_TIME);
        }
        try {
            String timing = time.trim();
            String[] dateTime = timing.split(" ");
            String date = dateTime[0].trim();
            String[] startEnd = dateTime[1].split("-");
            String start = startEnd[0].trim();
            String end = startEnd[1].trim();

            return new AddMeetingCommand(contactName, new Meeting(meeting, date, start, end));
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            throw new ParseException(MESSAGE_PARSE_ERROR);
        }
    }
    /**
     * Parses the given {@code meeting} string of the Meeting.toString() output format
     * and returns a string array of the parsed details to create a Meeting object.
     * @throws ParseException if the user input does not conform the expected format
     */
    public static String[] parseDetails(String meeting) {
        if (meeting.isEmpty()) {
            String[] details = {"", "", "", ""};
            return details;
        }
        String[] parts = meeting.toString().split(": ");
        String desc = parts[0];
        int index = parts[1].indexOf("(");
        String dateString = parts[1].substring(0, index).trim();
        LocalDate conviDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("d MMMM uuuu")
                .withResolverStyle(ResolverStyle.STRICT));
        String date = conviDate.format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String timeString = parts[1].substring(index).trim();
        String[] startEnd = timeString.substring(1, timeString.length() - 1).split(" - ");
        String start = startEnd[0].trim();
        String end = startEnd[1].trim();

        String[] details = {desc, date, start, end};
        return details;
    }

}
