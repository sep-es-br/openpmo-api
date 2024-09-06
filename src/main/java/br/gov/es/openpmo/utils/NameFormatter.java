package br.gov.es.openpmo.utils;

import static io.jsonwebtoken.lang.Strings.capitalize;

/**
 * This class is responsible for formatting names.
 *
 * @since 1.0.0
 * @version 1.0.0
 * @see io.jsonwebtoken.lang.Strings#capitalize(String)
 */
public class NameFormatter {

    /**
     * Format the name.
     * @param name The name to be formatted.
     * @return The formatted name.
     */
    public static String format(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        // Split the name by whitespaces
        String[] parts = name.split("\\s+");

        if (parts.length == 1) {
            return capitalize(parts[0]);
        }

        String firstName = capitalize(parts[0]);
        String lastName = capitalize(parts[parts.length - 1]);

        return firstName + " " + lastName;
    }
}
