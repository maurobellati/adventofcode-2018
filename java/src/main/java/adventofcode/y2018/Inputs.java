package adventofcode.y2018;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.readLines;

import java.io.IOException;
import java.util.List;

public final class Inputs {
  static List<String> forDay(final String day) {
    try {
      return readLines(getResource(Base.class, String.format("day-%s-input.txt", day)), UTF_8);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to read the input", e);
    }
  }
}
