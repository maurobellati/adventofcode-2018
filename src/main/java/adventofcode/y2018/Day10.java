package adventofcode.y2018;

/*
It's no use; your navigation system simply isn't capable of providing walking directions in the arctic circle, and certainly not in 1018.

The Elves suggest an alternative. In times like these, North Pole rescue operations will arrange particles of light in the sky to guide missing Elves back to base. Unfortunately, the message is easy to miss: the particles move slowly enough that it takes hours to align them, but have so much momentum that they only stay aligned for a second. If you blink at the wrong time, it might be hours before another message appears.

You can see these particles of light floating in the distance, and record their position in the sky and their velocity, the relative change in position per second (your puzzle input). The coordinates are all given from your perspective; given enough time, those positions and velocities will move the particles into a cohesive message!

Rather than wait, you decide to fast-forward the process and calculate what the particles will eventually spell.

For example, suppose you note the following particles:

position=< 9,  1> velocity=< 0,  2>
position=< 7,  0> velocity=<-1,  0>
position=< 3, -2> velocity=<-1,  1>
position=< 6, 10> velocity=<-2, -1>
position=< 2, -4> velocity=< 2,  2>
position=<-6, 10> velocity=< 2, -2>
position=< 1,  8> velocity=< 1, -1>
position=< 1,  7> velocity=< 1,  0>
position=<-3, 11> velocity=< 1, -2>
position=< 7,  6> velocity=<-1, -1>
position=<-2,  3> velocity=< 1,  0>
position=<-4,  3> velocity=< 2,  0>
position=<10, -3> velocity=<-1,  1>
position=< 5, 11> velocity=< 1, -2>
position=< 4,  7> velocity=< 0, -1>
position=< 8, -2> velocity=< 0,  1>
position=<15,  0> velocity=<-2,  0>
position=< 1,  6> velocity=< 1,  0>
position=< 8,  9> velocity=< 0, -1>
position=< 3,  3> velocity=<-1,  1>
position=< 0,  5> velocity=< 0, -1>
position=<-2,  2> velocity=< 2,  0>
position=< 5, -2> velocity=< 1,  2>
position=< 1,  4> velocity=< 2,  1>
position=<-2,  7> velocity=< 2, -2>
position=< 3,  6> velocity=<-1, -1>
position=< 5,  0> velocity=< 1,  0>
position=<-6,  0> velocity=< 2,  0>
position=< 5,  9> velocity=< 1, -2>
position=<14,  7> velocity=<-2,  0>
position=<-3,  6> velocity=< 2, -1>

Each line represents one point. Positions are given as <X, Y> pairs:
X represents how far left (negative) or right (positive) the point appears, while Y represents how far up (negative) or down (positive) the point appears.

At 0 seconds, each point has the position given.
Each second, each point's velocity is added to its position. So, a point with velocity <1, -2> is moving to the right,
but is moving upward twice as quickly. If this point's initial position were <3, 9>, after 3 seconds, its position would become <6, 3>.

Over time, the particles listed above would move like this:

Initially:
      |
........#.............
................#.....
.........#.#..#.......
......................
#..........#.#.......# --
...............#......
....#.................
..#.#....#............
.......#..............
......#...............
...#...#.#...#........
....#..#..#.........#.
.......#..............
...........#..#.......
#...........#.........
...#.......#..........

After 1 second:
......................
......................
..........#....#......
........#.....#.......
..#.........#......#..
......................
......#...............
....##.........#......
......#.#.............
.....##.##..#.........
........#.#...........
........#...#.....#...
..#...........#.......
....#.....#.#.........
......................
......................

After 2 seconds:
......................
......................
......................
..............#.......
....#..#...####..#....
......................
........#....#........
......#.#.............
.......#...#..........
.......#..#..#.#......
....#....#.#..........
.....#...#...##.#.....
........#.............
......................
......................
......................

After 3 seconds:
......................
......................
......................
......................
......#...#..###......
......#...#...#.......
......#...#...#.......
......#####...#.......
......#...#...#.......
......#...#...#.......
......#...#...#.......
......#...#..###......
......................
......................
......................
......................

After 4 seconds:
......................
......................
......................
............#.........
........##...#.#......
......#.....#..#......
.....#..##.##.#.......
.......##.#....#......
...........#....#.....
..............#.......
....#......#...#......
.....#.....##.........
...............#......
...............#......
......................
......................

After 3 seconds, the message appeared briefly: HI. Of course, your message will be much longer and will take many more seconds to appear.

What message will eventually appear in the sky?

--- Part Two ---

Good thing you didn't have to wait, because that would have taken a long time - much longer than the 3 seconds in the example above.

Impressed by your sub-hour communication capabilities, the Elves are curious: exactly how many seconds would they have needed to wait for that message to appear?

 */

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Integer.parseInt;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day10 {

  @Nested
  public static class Day10Test {

    public void part1examples() {
      assertThat(new Day10(exampleInput()).part1()).isEqualTo("");
    }

    @Test
    public void part1solution() {
      String actual = new Day10(Inputs.forDay("10")).part1();
      System.out.println(actual);
    }

    @Test
    public void part2examples() {
      assertThat(new Day10(exampleInput()).part2()).isEqualTo("3");
    }

    @Test
    public void part2solution() {
      String actual = new Day10(Inputs.forDay("10")).part2();
      System.out.println(actual);
    }

    List<String> exampleInput() {
      return asList("position=< 9,  1> velocity=< 0,  2>",
                    "position=< 7,  0> velocity=<-1,  0>",
                    "position=< 3, -2> velocity=<-1,  1>",
                    "position=< 6, 10> velocity=<-2, -1>",
                    "position=< 2, -4> velocity=< 2,  2>",
                    "position=<-6, 10> velocity=< 2, -2>",
                    "position=< 1,  8> velocity=< 1, -1>",
                    "position=< 1,  7> velocity=< 1,  0>",
                    "position=<-3, 11> velocity=< 1, -2>",
                    "position=< 7,  6> velocity=<-1, -1>",
                    "position=<-2,  3> velocity=< 1,  0>",
                    "position=<-4,  3> velocity=< 2,  0>",
                    "position=<10, -3> velocity=<-1,  1>",
                    "position=< 5, 11> velocity=< 1, -2>",
                    "position=< 4,  7> velocity=< 0, -1>",
                    "position=< 8, -2> velocity=< 0,  1>",
                    "position=<15,  0> velocity=<-2,  0>",
                    "position=< 1,  6> velocity=< 1,  0>",
                    "position=< 8,  9> velocity=< 0, -1>",
                    "position=< 3,  3> velocity=<-1,  1>",
                    "position=< 0,  5> velocity=< 0, -1>",
                    "position=<-2,  2> velocity=< 2,  0>",
                    "position=< 5, -2> velocity=< 1,  2>",
                    "position=< 1,  4> velocity=< 2,  1>",
                    "position=<-2,  7> velocity=< 2, -2>",
                    "position=< 3,  6> velocity=<-1, -1>",
                    "position=< 5,  0> velocity=< 1,  0>",
                    "position=<-6,  0> velocity=< 2,  0>",
                    "position=< 5,  9> velocity=< 1, -2>",
                    "position=<14,  7> velocity=<-2,  0>",
                    "position=<-3,  6> velocity=< 2, -1>");
    }
  }

  @Data
  @AllArgsConstructor
  private static class Pair {
    private int x;
    private int y;

    Pair move(final Pair delta) {
      return new Pair(x + delta.getX(), y + delta.getY());
    }

    Pair scale(final int factor) {
      if (factor == 1) {
        return this;
      }
      return new Pair(x * factor, y * factor);
    }

  }

  @Data
  @AllArgsConstructor
  private static class Particle {
    private Pair position;
    private Pair velocity;

    void move(int time) {
      position = position.move(velocity.scale(time));
    }
  }

  private final List<Particle> particles;

  public Day10(final List<String> input) {
    Pattern p = Pattern.compile("position=<\\s*([-\\d]+),\\s*([-\\d]+)> velocity=<\\s*([-\\d]+),\\s*([-\\d]+)>");

    particles = input.stream()
                     .map(p::matcher)
                     .peek(matcher -> checkArgument(matcher.find()))
                     .map(matcher -> new Particle(new Pair(parseInt(matcher.group(1)), parseInt(matcher.group(2))),
                                                  new Pair(parseInt(matcher.group(3)), parseInt(matcher.group(4)))))
                     .collect(toList());
  }

  private int computeDistance() {
    return (maxX() - minX()) + (maxY() - minY());
  }

  private int maxX() {
    return positions().mapToInt(Pair::getX).max().orElseThrow(IllegalStateException::new);
  }

  private int maxY() {
    return positions().mapToInt(Pair::getY).max().orElseThrow(IllegalStateException::new);
  }

  private int minX() {
    return positions().mapToInt(Pair::getX).min().orElseThrow(IllegalStateException::new);
  }

  private int minY() {
    return positions().mapToInt(Pair::getY).min().orElseThrow(IllegalStateException::new);
  }

  private String part1() {
    solve();
    print();
    return "";
  }

  private String part2() {
    int steps = solve();
    print();
    return String.valueOf(steps);
  }

  private Stream<Pair> positions() {
    return particles.stream().map(Particle::getPosition);
  }

  private void print() {
    PrintStream out = System.out;

    List<Pair> points = positions().distinct()
                                   .sorted(comparing(Pair::getY).thenComparing(Pair::getX))
                                   .collect(toList());

    //    points.forEach(out::println);
    //    out.printf("X=[%s, %s] | Y=[%s, %s]%n", minX(), maxX(), minY(), maxY());

    int pointIndex = 0;
    for (int y = minY(); y <= maxY(); y++) {
      for (int x = minX(); x <= maxX(); x++) {

        Pair point = points.get(pointIndex);
        if (point.getX() == x && point.getY() == y) {
          out.print("#");
          pointIndex = min(pointIndex + 1, points.size());
        } else {

          out.print(" ");
        }

      }

      out.println();
    }
  }

  private int solve() {
    int minDistance = computeDistance();
    int distance;

    int steps = 0;
    while ((distance = computeDistance()) <= minDistance) {
      minDistance = distance;
      steps++;
      particles.forEach(p -> p.move(1));
    }

    // revert bach one step
    steps--;
    particles.forEach(p -> p.move(-1));

    return steps;
  }

}
