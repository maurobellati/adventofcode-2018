package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.util.Arrays.asList;
import static java.util.Collections.max;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Range;
import lombok.Value;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 --- Day 6: Chronal Coordinates ---
 The device on your wrist beeps several times, and once again you feel like you're falling.

 "Situation critical," the device announces. "Destination indeterminate. Chronal interference detected. Please specify new target coordinates."

 The device then produces a list of coordinates (your puzzle input). Are they places it thinks are safe or dangerous? It recommends you check manual page 729.
 The Elves did not give you a manual.

 If they're dangerous, maybe you can minimize the danger by finding the coordinate that gives the largest distance from the other points.

 Using only the Manhattan distance, determine the area around each coordinate by counting the number of integer X,Y locations that are closest to that coordinate
 (and aren't tied in distance to any other coordinate).

 Your goal is to find the size of the largest area that isn't infinite. For example, consider the following list of coordinates:

 A: 1, 1
 B: 1, 6
 C: 8, 3
 D: 3, 4
 E: 5, 5
 F: 8, 9
 If we name these coordinates A through F, we can draw them on a grid, putting 0,0 at the top left:

 .........
 .A.......
 .........
 ........C
 ...D.....
 .....E...
 .B.......
 .........
 .........
 ........F
 This view is partial - the actual grid extends infinitely in all directions.
 Using the Manhattan distance, each location's closest coordinate can be determined, shown here in lowercase:

 aaaaa.cccc
 aAaaa.cccc
 aaaddecccc
 aadddeccCc
 ..dDdeeccc
 bb.deEeecc
 bBb.eeee..
 bbb.eeefff
 bbb.eeffff
 bbb.ffffFf
 Locations shown as . are equally far from two or more coordinates, and so they don't count as being closest to any.

 In this example, the areas of coordinates A, B, C, and F are infinite - while not shown here, their areas extend forever outside the visible grid.
 However, the areas of coordinates D and E are finite: D is closest to 9 locations, and E is closest to 17 (both including the coordinate's location itself).
 Therefore, in this example, the size of the largest area is 17.

 What is the size of the largest area that isn't infinite?

 --- Part Two ---
 On the other hand, if the coordinates are safe, maybe the best you can do is try to find a region near as many coordinates as possible.

 For example, suppose you want the sum of the Manhattan distance to all of the coordinates to be less than 32. For each location, add up the distances to all of the given coordinates; if the total of those distances is less than 32, that location is within the desired region. Using the same coordinates as above, the resulting region looks like this:

 ..........
 .A........
 ..........
 ...###..C.
 ..#D###...
 ..###E#...
 .B.###....
 ..........
 ..........
 ........F.
 In particular, consider the highlighted location 4,3 located at the top middle of the region. Its calculation is as follows, where abs() is the absolute value function:

 Distance to coordinate A: abs(4-1) + abs(3-1) =  5
 Distance to coordinate B: abs(4-1) + abs(3-6) =  6
 Distance to coordinate C: abs(4-8) + abs(3-3) =  4
 Distance to coordinate D: abs(4-3) + abs(3-4) =  2
 Distance to coordinate E: abs(4-5) + abs(3-5) =  3
 Distance to coordinate F: abs(4-8) + abs(3-9) = 10
 Total distance: 5 + 6 + 4 + 2 + 3 + 10 = 30
 Because the total distance to all coordinates (30) is less than 32, the location is within the region.

 This region, which also includes coordinates D and E, has a total size of 16.

 Your actual region will need to be much larger than this example, though, instead including all locations with a total distance of less than 10000.

 What is the size of the region containing all locations which have a total distance to all given coordinates of less than 10000?
 */
public class Day6 {
  @Nested
  public static class Day6Test {

    static Stream<Arguments> part1examples() {
      return Stream.of(
        arguments(asList("1, 1",
                         "1, 6",
                         "8, 3",
                         "3, 4",
                         "5, 5",
                         "8, 9"), "17")
      );
    }

    static Stream<Arguments> part2examples() {
      return Stream.of(
        arguments(asList("1, 1",
                         "1, 6",
                         "8, 3",
                         "3, 4",
                         "5, 5",
                         "8, 9"), "16")
      );
    }

    @ParameterizedTest
    @MethodSource
    public void part1examples(final List<String> input, String expected) {
      assertThat(new Day6(input).part1()).isEqualTo(expected);
    }

    @Test
    public void part1solution() {
      String actual = new Day6(Inputs.forDay("6")).part1();
      System.out.println(actual);
      assertThat(actual).isEqualTo("3449");
    }

    @ParameterizedTest
    @MethodSource
    public void part2examples(final List<String> input, String expected) {
      assertThat(new Day6(input).part2(Range.lessThan(32))).isEqualTo(expected);
    }

    @Test
    public void part2solution() {
      String actual = new Day6(Inputs.forDay("6")).part2(Range.lessThan(10_000));
      System.out.println(actual);
    }

  }

  @Value
  private static class Point {
    private int row;
    private int column;

    Integer manhattanDistance(final Point other) {
      return abs(getRow() - other.getRow()) + abs(getColumn() - other.getColumn());
    }

    @Override
    public String toString() {
      return "(" + row + "," + column + ")";
    }
  }

  private static final Pattern REGEX = Pattern.compile("(\\d+),\\s+(\\d+)");
  private final List<Point> points;

  public Day6(final List<String> inputLines) {
    points = parsePoints(inputLines);
  }

  private List<Point> parsePoints(final List<String> inputLines) {
    return inputLines.stream()
                     .map(line -> {
                       Matcher matcher = REGEX.matcher(line);
                       checkArgument(matcher.find());
                       return new Point(parseInt(matcher.group(2)),
                                        parseInt(matcher.group(1)));

                     })
                     .collect(toList());
  }

  private String part1() {
    Point[][] grid = createGridWithMinDistance(points);
    //printGrid(grid);

    Multiset<Point> pointCounts = countByPoint(grid);
    Set<Point> pointOnBorders = getPointsOnBorder(grid);

    Multiset.Entry<Point> maxPoint = pointCounts.entrySet()
                                                .stream()
                                                .filter(e -> !pointOnBorders.contains(e.getElement()))
                                                .max(comparing(Multiset.Entry::getCount))
                                                .orElseThrow(IllegalStateException::new);
    System.out.println("max point: " + maxPoint);
    return String.valueOf(maxPoint.getCount());
  }

  private Multiset<Point> countByPoint(final Point[][] grid) {
    Multiset<Point> result = HashMultiset.create();

    range(0, grid.length)
      .forEach(row -> {
        Arrays.stream(grid[row])
              .filter(Objects::nonNull)
              .forEach(result::add);
      });

    return result;
  }

  private Set<Point> getPointsOnBorder(final Point[][] input) {
    Set<Point> result = newHashSet();
    // row 0
    result.addAll(asList(input[0]));

    // row n
    result.addAll(asList(input[input.length - 1]));

    for (Point[] row : input) {
      // column 0
      result.add(row[0]);
      // column n
      result.add(row[row.length - 1]);
    }

    result.remove(null);

    return result;
  }

  private Point[][] createGridWithMinDistance(final List<Point> input) {
    int maxRow = max(input, comparing(Point::getRow)).getRow();
    int maxColumn = max(input, comparing(Point::getColumn)).getColumn();

    Point[][] result = new Point[maxRow + 1][maxColumn + 1];

    range(0, result.length)
      .forEach(row -> {
        range(0, result[0].length)
          .forEach(column -> {
            Point current = new Point(row, column);

            input.sort(comparing(current::manhattanDistance));

            boolean noTieDistance = current.manhattanDistance(input.get(0)) < current.manhattanDistance(input.get(1));
            if (noTieDistance) {
              result[row][column] = input.get(0);
            }
          });
      });
    return result;
  }

  private <T> void printGrid(final T[][] grid) {
    for (T[] row : grid) {
      System.out.println(Arrays.toString(row));
    }
  }

  private String part2(final Range<Integer> validRange) {
    Integer[][] grid = createGridWithSumOfDistances(points);
    //printGrid(grid);

    long count = countValuesWithinRange(grid, validRange);
    System.out.printf("Values in %s are %d%n", validRange, count);
    return String.valueOf(count);
  }

  private long countValuesWithinRange(final Integer[][] input, final Range<Integer> range) {
    return range(0, input.length).boxed()
                                 .flatMap(row -> Arrays.stream(input[row]))
                                 .filter(range::contains)
                                 .count();
  }

  private Integer[][] createGridWithSumOfDistances(final List<Point> input) {
    int maxRow = max(input, comparing(Point::getRow)).getRow();
    int maxColumn = max(input, comparing(Point::getColumn)).getColumn();

    Integer[][] result = new Integer[maxRow + 1][maxColumn + 1];

    range(0, result.length)
      .forEach(row -> {
        range(0, result[0].length)
          .forEach(column -> {
            Point current = new Point(row, column);

            result[row][column] = input.stream()
                                       .mapToInt(current::manhattanDistance)
                                       .sum();

          });
      });
    return result;
  }

}
