package adventofcode.y2018;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.common.collect.HashMultiset;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

/*
--- Day 3: No Matter How You Slice It ---
The Elves managed to locate the chimney-squeeze prototype fabric for Santa's suit (thanks to someone who helpfully wrote its box IDs on the wall of the warehouse in the middle of the night). Unfortunately, anomalies are still affecting them - nobody can even agree on how to cut the fabric.

The whole piece of fabric they're working on is a very large square - at least 1000 inches on each side.

Each Elf has made a claim about which area of fabric would be ideal for Santa's suit. All claims have an ID and consist of a single rectangle with edges parallel to the edges of the fabric. Each claim's rectangle is defined as follows:

The number of inches between the left edge of the fabric and the left edge of the rectangle.
The number of inches between the top edge of the fabric and the top edge of the rectangle.
The width of the rectangle in inches.
The height of the rectangle in inches.
A claim like #123 @ 3,2: 5x4 means that claim ID 123 specifies a rectangle 3 inches from the left edge, 2 inches from the top edge, 5 inches wide, and 4 inches tall. Visually, it claims the square inches of fabric represented by # (and ignores the square inches of fabric represented by .) in the diagram below:

...........
...........
...#####...
...#####...
...#####...
...#####...
...........
...........
...........
The problem is that many of the claims overlap, causing two or more claims to cover part of the same areas. For example, consider the following claims:

#1 @ 1,3: 4x4
#2 @ 3,1: 4x4
#3 @ 5,5: 2x2
Visually, these claim the following areas:

........
...2222.
...2222.
.11XX22.
.11XX22.
.111133.
.111133.
........
The four square inches marked with X are claimed by both 1 and 2. (Claim 3, while adjacent to the others, does not overlap either of them.)

If the Elves all proceed with their own plans, none of them will have enough fabric. How many square inches of fabric are within two or more claims?
 */
public class Day3Part1 extends Day3 {

  @Nested
  public static class Day3Part1Test {

    static Stream<Arguments> examples() {
      return Stream.of(
        arguments(asList("#1 @ 1,3: 4x4",
                         "#2 @ 3,1: 4x4",
                         "#3 @ 5,5: 2x2"), "4"),

        /**
         22222
         .111.
         .111.
         */
        arguments(asList("#1 @ 1,1: 3x2",
                         "#2 @ 0,0: 5x1"), "0"),

        /**
         2....
         2111.
         2111.
         2....
         */
        arguments(asList("#1 @ 1,1: 3x4",
                         "#2 @ 0,0: 1x4"), "0"),

        /**
         .....
         .111.
         .111.
         22222
         */
        arguments(asList("#1 @ 1,1: 3x4",
                         "#2 @ 3,0: 5x1"), "0"),

        /**
         ....2
         .1112
         .1112
         ....2
         */
        arguments(asList("#1 @ 1,1: 3x4",
                         "#2 @ 0,4: 1x4"), "0"),

        /**
         11X22
         11X22
         XXXXX
         33X44
         33X44
         */
        arguments(asList("#1 @ 0,0: 3x3",
                         "#2 @ 0,2: 3x3",
                         "#3 @ 2,0: 3x3",
                         "#4 @ 2,2: 3x3"), "9")

      );
    }

    @ParameterizedTest
    @MethodSource
    public void examples(final List<String> input, String expected) {
      assertThat(new Day3Part1(input).solve()).isEqualTo(expected);
    }

    @Test
    public void solution() {
      String actual = new Day3Part1(Inputs.forDay("3")).solve();
      System.out.println(actual);
      assertThat(actual).isEqualTo("118539");
    }

  }

  public Day3Part1(final List<String> inputLines) {
    super(inputLines);
  }

  /**
   * Simply collect all possible points into a Multiset.
   * Count how many have duplicate values.
   */
  @Override
  public String solve() {
    List<Point> allPoints = getSquares().flatMap(Square::allPoints)
                                        .collect(toList());

    long result = HashMultiset.create(allPoints)
                              .entrySet().stream()
                              .filter(it -> it.getCount() > 1)
                              .count();

    return String.valueOf(result);
  }

}
