package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;


public class EditCommandParserTest {

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingName_throwsParseException() {
        String input = "n/John Smith p/98765432 e/john@example.com a/311, Clementi Ave 2 t/friends";
        assertThrows(ParseException.class, () -> parser.parse(input));
    }
}





