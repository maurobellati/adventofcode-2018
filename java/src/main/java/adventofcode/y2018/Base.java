package adventofcode.y2018;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Splitter;

import java.util.List;

public abstract class Base {

  private static final Splitter CSV_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

  public static List<String> parseCsv(final String input) {
    return CSV_SPLITTER.splitToList(input);
  }

  protected List<String> inputLines;

  public Base(final List<String> inputLines) {
    this.inputLines = inputLines;
  }

  public abstract String solve();

  protected List<Integer> asInts(final List<String> strings) {
    return strings.stream().map(Integer::parseInt).collect(toList());
  }

}
