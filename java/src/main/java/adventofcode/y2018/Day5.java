package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
--- Day 5: Alchemical Reduction ---
You've managed to sneak in to the prototype suit manufacturing lab. The Elves are making decent progress, but are still struggling with the suit's size reduction capabilities.

While the very latest in 1518 alchemical technology might have solved their problem eventually, you can do better. You scan the chemical composition of the suit's material and discover that it is formed by extremely long polymers (one of which is available as your puzzle input).

The polymer is formed by smaller units which, when triggered, react with each other such that two adjacent units of the same type and opposite polarity are destroyed. Units' types are represented by letters; units' polarity is represented by capitalization. For instance, r and R are units with the same type but opposite polarity, whereas r and s are entirely different types and do not react.

For example:

In aA, a and A react, leaving nothing behind.
In abBA, bB destroys itself, leaving aA. As above, this then destroys itself, leaving nothing.
In abAB, no two adjacent units are of the same type, and so nothing happens.
In aabAAB, even though aa and AA are of the same type, their polarities match, and so nothing happens.
Now, consider a larger example, dabAcCaCBAcCcaDA:

dabAcCaCBAcCcaDA  The first 'cC' is removed.
dabAaCBAcCcaDA    This creates 'Aa', which is removed.
dabCBAcCcaDA      Either 'cC' or 'Cc' are removed (the result is the same).
dabCBAcaDA        No further actions can be taken.
After all possible reactions, the resulting polymer contains 10 units.

How many units remain after fully reacting the polymer you scanned?
 */
public class Day5 {
  @Nested
  public static class Day4Test {

    static Stream<Arguments> part1examples() {
      return Stream.of(
        arguments(asList("aA"), "0"),
        arguments(asList("abBA"), "0"),
        arguments(asList("abAB"), "4"),
        arguments(asList("aabAAB"), "6"),
        arguments(asList("dabAcCbBaCBAcCcaDA"), "10")
      );
    }

    static Stream<Arguments> part2examples() {
      return Stream.of(
        arguments(asList(""), "4455")
      );
    }

    @ParameterizedTest
    @MethodSource
    public void part1examples(final List<String> input, String expected) {
      assertThat(new Day5(input).part1()).isEqualTo(expected);
    }

    @Test
    public void part1solution() {
      String actual = new Day5(Inputs.forDay("5")).part1();
      System.out.println(actual);
      assertThat(actual).isEqualTo("11476");
    }

    @ParameterizedTest
    @MethodSource
    @Disabled
    public void part2examples(final List<String> input, String expected) {
      assertThat(new Day5(input).part2()).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void part2solution() {
      String actual = new Day5(Inputs.forDay("5")).part2();
      System.out.println(actual);
    }
  }

  private static final Pattern REGEX = Pattern.compile("(\\w)"       // match a characther
                                                         + "(?!"     // negative lookahead (do not match this regex)
                                                         + "\\1"     //   exactly same match (to avoid 'aa' and 'AA')
                                                         + ")"       // end of lookahead
                                                         + "(?i)\\1" // activate ignorecase and match previous match
  );

  private final String input;

  public Day5(final List<String> inputLines) {
    checkArgument(inputLines.size() == 1);
    input = inputLines.get(0);
  }

  private String part1() {
    String polymer = input;

    Matcher matcher = REGEX.matcher(polymer);

    int iterations = 0;

    while (matcher.find()) {
      iterations++;
      polymer = matcher.replaceAll("");
      matcher = REGEX.matcher(polymer);
    }
    //    System.out.printf("Input : [%s]%nresult: [%s]%n", input, polymer);
    System.out.printf("Size reduced to %d chars in %d iterations%n", polymer.length(), iterations);
    return String.valueOf(polymer.length());
  }

  private String part2() {
    return null;
  }

}
