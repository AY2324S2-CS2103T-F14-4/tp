package seedu.address.logic.parser;

import org.junit.jupiter.api.Test;
import seedu.address.logic.parser.exceptions.ParseException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EditCommandParserTest {

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingName_throwsParseException() {
        // Input without the name field
        String input = "n/John Smith p/98765432 e/john@example.com a/311, Clementi Ave 2 t/friends";
        assertThrows(ParseException.class, () -> parser.parse(input));
    }
}





