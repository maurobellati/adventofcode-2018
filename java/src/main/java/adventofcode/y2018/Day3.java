package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import lombok.Value;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

--- Part Two ---
Amidst the chaos, you notice that exactly one claim doesn't overlap by even a single square inch of fabric with any other claim. If you can somehow draw attention to it, maybe the Elves will be able to make Santa's suit after all!

For example, in the claims above, only claim 3 is intact after all claims are made.

What is the ID of the only claim that doesn't overlap?

 */
public class Day3 {

  @Nested
  public static class Part1Test {

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
      assertThat(new Day3(input).part1()).isEqualTo(expected);
    }

    @Test
    public void solution() {
      String actual = new Day3(Inputs.forDay("3")).part1();
      System.out.println(actual);
      assertThat(actual).isEqualTo("118539");
    }

  }

  @Nested
  public static class Part2Test {

    static Stream<Arguments> examples() {
      return Stream.of(
        arguments(asList("#1 @ 1,3: 4x4",
                         "#2 @ 3,1: 4x4",
                         "#3 @ 5,5: 2x2"), "3")

      );
    }

    @ParameterizedTest
    @MethodSource
    public void examples(final List<String> input, String expected) {
      assertThat(new Day3(input).part2()).isEqualTo(expected);
    }

    @Test
    public void solution() {
      String actual = new Day3(Inputs.forDay("3")).part2();
      System.out.println(actual);
    }

  }

  @Value
  static class Point {
    private int x;
    private int y;
  }

  @Value
  static class Square {
    private int height;
    private int id;
    private int left;
    private int top;
    private int width;

    Square(final int id, final int left, final int top, final int width, final int height) {
      this.id = id;
      this.left = left;
      this.top = top;
      this.width = width;
      this.height = height;
    }

    Stream<Point> allPoints() {
      return rangeClosed(getTop(), getBottom()).boxed()
                                               .flatMap(row ->
                                                          rangeClosed(getLeft(), getRight())
                                                            .mapToObj(column -> new Point(row, column))
                                               );
    }

    int getBottom() {
      return top + height - 1;
    }

    int getRight() {
      return left + width - 1;
    }

  }

  private static final Pattern SQUARE_FORMAT = Pattern.compile("#(\\d+)\\s@\\s(\\d+),(\\d+):\\s(\\d+)x(\\d+)");
  private final List<String> inputLines;

  public Day3(final List<String> inputLines) {
    this.inputLines = inputLines;
  }

  /**
   * Simply collect all possible points into a Multiset.
   * Count how many have duplicate values.
   */
  public String part1() {
    List<Point> allPoints = getSquares().flatMap(Square::allPoints)
                                        .collect(toList());

    long result = HashMultiset.create(allPoints)
                              .entrySet().stream()
                              .filter(it -> it.getCount() > 1)
                              .count();

    return String.valueOf(result);
  }

  /**
   * Almost same idea as Part1.
   * Use a multimap to map point->square
   * Collect overlapping squares
   * Diff from all squares to find the non-overlapping one.
   */
  public String part2() {
    Multimap<Point, Square> pointToSquares = HashMultimap.create();

    Set<Square> allSquares = getSquares().collect(toSet());

    allSquares.forEach(square ->
                         square.allPoints()
                               .forEach(point ->
                                          pointToSquares.put(point, square)));

    Set<Square> overlappingSquares = pointToSquares.asMap()
                                                   .entrySet().stream()
                                                   .filter(entry -> entry.getValue().size() > 1)
                                                   .flatMap(entry -> entry.getValue().stream())
                                                   .collect(toSet());

    Set<Square> nonOverlappingSquares = Sets.difference(allSquares, overlappingSquares);

    checkState(nonOverlappingSquares.size() == 1, "Found %s non overlapping squares", overlappingSquares);

    return String.valueOf(nonOverlappingSquares.stream().findFirst()
                                               .orElseThrow(IllegalStateException::new)
                                               .getId());
  }

  private Stream<Square> getSquares() {
    return inputLines.stream()
                     .map(this::parse);
  }

  private Square parse(final String line) {
    Matcher matcher = SQUARE_FORMAT.matcher(line);
    checkArgument(matcher.find(),
                  "Input [%s] does not match required pattern (%s)", line, SQUARE_FORMAT.pattern());
    return new Square(parseInt(matcher.group(1)),
                      parseInt(matcher.group(2)),
                      parseInt(matcher.group(3)),
                      parseInt(matcher.group(4)),
                      parseInt(matcher.group(5)));
  }
}
