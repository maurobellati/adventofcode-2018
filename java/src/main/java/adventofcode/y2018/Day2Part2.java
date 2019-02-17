package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

/*
--- Part Two ---
 Confident that your list of box IDs is complete, you're ready to find the boxes full of prototype fabric.

 The boxes will have IDs which differ by exactly one character at the same position in both strings. For example, given the following box IDs:

 abcde
 fghij
 klmno
 pqrst
 fguij
 axcye
 wvxyz
 The IDs abcde and axcye are close, but they differ by two characters (the second and fourth).
 However, the IDs fghij and fguij differ by exactly one character, the third (h and u).
 Those must be the correct boxes.

 What letters are common between the two correct box IDs?
 (In the example above, this is found by removing the differing character from either ID, producing fgij.)
 */
public class Day2Part2 extends Base {

  @Nested
  public static class Day2Part2Test {

    static Stream<Arguments> examples() {
      return Stream.of(
        arguments(asList("abcde",
                         "fghij",
                         "klmno",
                         "pqrst",
                         "fguij",
                         "axcye",
                         "wvxyz"), "fgij")
      );
    }

    static Stream<Arguments> getCommonSubstringCharByCharTest() {
      return Stream.of(
        arguments("abcde", "abcde", "abcde"),
        arguments("abcde", "axcye", "ace"),
        arguments("abcde", "xxxxx", "")
      );
    }

    @ParameterizedTest
    @MethodSource
    public void examples(final List<String> input, String expected) {
      assertThat(new Day2Part2(input).solve()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource
    public void getCommonSubstringCharByCharTest(final String x, final String y, final String expected) {
      assertThat(getCommonSubstringCharByChar(x, y)).isEqualTo(expected);
    }

    @Test
    public void solution() {
      System.out.println(new Day2Part2(Inputs.forDay("2")).solve());
    }

  }

  public Day2Part2(final List<String> inputLines) {
    super(inputLines);
  }

  static String getCommonSubstringCharByChar(final String x, final String y) {
    checkArgument(x.length() == y.length(), "String [%s] and [%s] do not have the same length", x, y);
    StringBuilder result = new StringBuilder();
    range(0, x.length())
      .forEach(i -> {
        if (x.charAt(i) == y.charAt(i)) {
          result.append(x.charAt(i));
        }
      });
    return result.toString();
  }

  public String solve() {
    String maxCommonSubstring = "";

    for (int i = 0; i < inputLines.size() - 1; i++) {
      for (int j = i + 1; j < inputLines.size(); j++) {
        String candidate = getCommonSubstringCharByChar(inputLines.get(i), inputLines.get(j));
        if (candidate.length() > maxCommonSubstring.length()) {
          maxCommonSubstring = candidate;
        }
      }

    }
    return maxCommonSubstring;
  }

}
