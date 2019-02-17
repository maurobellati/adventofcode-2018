package adventofcode.y2018;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/*
 --- Part Two ---
 You notice that the device repeats the same frequency change list over and over. To calibrate the device, you need to find the first frequency it reaches twice.

 For example, using the same list of changes above, the device would loop as follows:

 Current frequency  0, change of +1; resulting frequency  1.
 Current frequency  1, change of -2; resulting frequency -1.
 Current frequency -1, change of +3; resulting frequency  2.
 Current frequency  2, change of +1; resulting frequency  3.
 (At this point, the device continues from the start of the list.)
 Current frequency  3, change of +1; resulting frequency  4.
 Current frequency  4, change of -2; resulting frequency  2, which has already been seen.
 In this example, the first frequency reached twice is 2. Note that your device might need to repeat its list of frequency changes many times before a duplicate frequency is found, and that duplicates might be found while in the middle of processing the list.

 Here are other examples:

 +1, -1 first reaches 0 twice.
 +3, +3, +4, -2, -4 first reaches 10 twice.
 -6, +3, +8, +5, -6 first reaches 5 twice.
 +7, +7, -2, -7, -4 first reaches 14 twice.
 What is the first frequency your device reaches twice?
 */
public class Day1Part2 extends Base {

  @Nested
  public static class Day1Part2Test {

    static Stream<Arguments> examples() {
      return Stream.of(
        arguments("+1, -1", "0"),
        arguments("+3, +3, +4, -2, -4", "10"),
        arguments("-6, +3, +8, +5, -6", "5"),
        arguments("+7, +7, -2, -7, -4", "14")
      );
    }

    @Test
    public void solution() {
      System.out.println(new Day1Part2(Inputs.forDay("1")).solve());
    }

    @ParameterizedTest
    @MethodSource
    public void examples(final String input, String expected) {
      assertThat(new Day1Part2(parseCsv(input)).solve()).isEqualTo(expected);
    }

  }

  Day1Part2(final List<String> inputLines) {
    super(inputLines);
  }

  public String solve() {
    List<Integer> ints = asInts(inputLines);
    int i = 0;
    int sum = 0;
    Set<Integer> sums = newHashSet(sum);
    while (true) {
      sum += ints.get(i % ints.size());
      if (sums.contains(sum)) {
        return String.valueOf(sum);
      }
      sums.add(sum);
      i++;
    }
  }

}
