package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.common.primitives.Chars;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
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

 --- Part Two ---
 Time to improve the polymer.

 One of the unit types is causing problems; it's preventing the polymer from collapsing as much as it should. Your goal is to figure out which unit type is causing the most problems, remove all instances of it (regardless of polarity), fully react the remaining polymer, and measure its length.

 For example, again using the polymer dabAcCaCBAcCcaDA from above:

 Removing all A/a units produces dbcCCBcCcD. Fully reacting this polymer produces dbCBcD, which has length 6.
 Removing all B/b units produces daAcCaCAcCcaDA. Fully reacting this polymer produces daCAcaDA, which has length 8.
 Removing all C/c units produces dabAaBAaDA. Fully reacting this polymer produces daDA, which has length 4.
 Removing all D/d units produces abAcCaCBAcCcaA. Fully reacting this polymer produces abCBAc, which has length 6.
 In this example, removing all C/c units was best, producing the answer 4.

 What is the length of the shortest polymer you can produce by removing all units of exactly one type and fully reacting the result?

 */
public class Day5 {
  @Nested
  public static class Day5Test {

    static Stream<Arguments> part1examples() {
      return Stream.of(
        arguments(asList("aA"), "0"),
        arguments(asList("abBA"), "0"),
        arguments(asList("abAB"), "4"),
        arguments(asList("aabAAB"), "6"),
        arguments(asList("dabAcCaCBAcCcaDA"), "10")
      );
    }

    static Stream<Arguments> part2examples() {
      return Stream.of(
        arguments(asList("dabAcCaCBAcCcaDA"), "4")
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
    public void part2examples(final List<String> input, String expected) {
      assertThat(new Day5(input).part2()).isEqualTo(expected);
    }

    @Test
    public void part2solution() {
      String actual = new Day5(Inputs.forDay("5")).part2();
      System.out.println(actual);
    }

    @ParameterizedTest
    @CsvSource({
                 "aA, a, ''",
                 "aAbcAAaa, a, bc",
                 "dabAcCaCBAcCcaDA, a, dbcCCBcCcD",
                 "dabAcCaCBAcCcaDA, A, dbcCCBcCcD",
                 "dabAcCaCBAcCcaDA, c, dabAaBAaDA",
                 "dabAcCaCBAcCcaDA, C, dabAaBAaDA",
               })
    public void removeIgnorecaseTest(final String input, final char character, final String expected) {
      assertThat(removeIgnorecase(input, character)).isEqualTo(expected);
    }
  }

  private static final Pattern REGEX = Pattern.compile("(\\w)"       // match a characther
                                                         + "(?!"     // negative lookahead (do not match this regex)
                                                         + "\\1"     //   exactly same match (to avoid 'aa' and 'AA')
                                                         + ")"       // end of lookahead
                                                         + "(?i)\\1" // activate ignorecase and match previous match
  );

  private final String polymer;

  public Day5(final List<String> inputLines) {
    checkArgument(inputLines.size() == 1);
    polymer = inputLines.get(0);
  }

  private static String removeIgnorecase(final String input, final Character character) {
    String pattern = "(?i)" + character;
    return input.replaceAll(pattern, "");
  }

  private String part1() {
    String reduced = reducePolymer(polymer);
    System.out.printf("Size reduced to %d chars%n", reduced.length());
    return String.valueOf(reduced.length());
  }

  private String part2() {
    char[] alphabeth = polymer.toLowerCase().toCharArray();
    int minPolymerLenght = Chars.asList(alphabeth)
                                .stream()
                                .distinct()
                                .map(c -> {
                                  System.out.printf("removing %s: ", c);
                                  return removeIgnorecase(polymer, c);
                                })
                                .map(this::reducePolymer)
                                .mapToInt(String::length)
                                .peek(System.out::println)
                                .min().orElseThrow(IllegalStateException::new);
    System.out.println("Min polymer length: " + minPolymerLenght);
    return String.valueOf(minPolymerLenght);
  }

  private String reducePolymer(final String input) {
    Matcher matcher = REGEX.matcher(input);
    String result = input;

    while (matcher.find()) {
      result = matcher.replaceAll("");
      matcher = REGEX.matcher(result);
    }
    return result;
  }

}
