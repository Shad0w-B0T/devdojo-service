package devdojo.schema.code;

import java.io.*;

public record TestError(String type,
                        String message) implements Serializable {
}
