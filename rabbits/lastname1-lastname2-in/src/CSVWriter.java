import java.io.IOException;
import java.io.Writer;
import java.util.List;
/**
*
* From: https://mkyong.com/java/how-to-export-data-to-csv-file-java/?fbclid=IwAR1sT9Ciq7Na3V6ngI8VVg6GNcJS5JlbsAYn_Xe9strahM8Ow4rwAYxlGyw.
*
*/


public class CSVWriter {

    private static final char DEFAULT_SEPARATOR = ',';

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    // https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;

        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }

        return result;
    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();

        for (String value : values) {

            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }

        sb.append("\n");
        w.append(sb.toString());
    }

}
