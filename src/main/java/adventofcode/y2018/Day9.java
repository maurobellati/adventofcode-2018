package adventofcode.y2018;

/*
--- Day 9: Marble Mania ---
You talk to the Elves while you wait for your navigation system to initialize. To pass the time, they introduce you to their favorite marble game.

The Elves play this game by taking turns arranging the marbles in a circle according to very particular rules.
The marbles are numbered starting with 0 and increasing by 1 until every marble has a number.

First, the marble numbered 0 is placed in the circle.
At this point, while it contains only a single marble, it is still a circle: the marble is both clockwise from itself and counter-clockwise from itself.
This marble is designated the current marble.

Then, each Elf takes a turn placing the lowest-numbered remaining marble into the circle between the marbles that are 1 and 2 marbles clockwise of the current marble.
(When the circle is large enough, this means that there is one marble between the marble that was just placed and the current marble.)
The marble that was just placed then becomes the current marble.

However, if the marble that is about to be placed has a number which is a multiple of 23, something entirely different happens.
First, the current player keeps the marble they would have placed, adding it to their score.
In addition, the marble 7 marbles counter-clockwise from the current marble is removed from the circle and also added to the current player's score.
The marble located immediately clockwise of the marble that was removed becomes the new current marble.

For example, suppose there are 9 players. After the marble with value 0 is placed in the middle, each player (shown in square brackets) takes a turn.
The result of each of those turns would produce circles of marbles like this, where clockwise is to the right and the resulting current marble is in parentheses:

[-] (0)
[1]  0 (1)
[2]  0 (2) 1
[3]  0  2  1 (3)
[4]  0 (4) 2  1  3
[5]  0  4  2 (5) 1  3
[6]  0  4  2  5  1 (6) 3
[7]  0  4  2  5  1  6  3 (7)
[8]  0 (8) 4  2  5  1  6  3  7
[9]  0  8  4 (9) 2  5  1  6  3  7
[1]  0  8  4  9  2(10) 5  1  6  3  7
[2]  0  8  4  9  2 10  5(11) 1  6  3  7
[3]  0  8  4  9  2 10  5 11  1(12) 6  3  7
[4]  0  8  4  9  2 10  5 11  1 12  6(13) 3  7
[5]  0  8  4  9  2 10  5 11  1 12  6 13  3(14) 7
[6]  0  8  4  9  2 10  5 11  1 12  6 13  3 14  7(15)
[7]  0(16) 8  4  9  2 10  5 11  1 12  6 13  3 14  7 15
[8]  0 16  8(17) 4  9  2 10  5 11  1 12  6 13  3 14  7 15
[9]  0 16  8 17  4(18) 9  2 10  5 11  1 12  6 13  3 14  7 15
[1]  0 16  8 17  4 18  9(19) 2 10  5 11  1 12  6 13  3 14  7 15
[2]  0 16  8 17  4 18  9 19  2(20)10  5 11  1 12  6 13  3 14  7 15
[3]  0 16  8 17  4 18  9 19  2 20 10(21) 5 11  1 12  6 13  3 14  7 15
[4]  0 16  8 17  4 18  9 19  2 20 10 21  5(22)11  1 12  6 13  3 14  7 15
[5]  0 16  8 17  4 18(19) 2 20 10 21  5 22 11  1 12  6 13  3 14  7 15
[6]  0 16  8 17  4 18 19  2(24)20 10 21  5 22 11  1 12  6 13  3 14  7 15
[7]  0 16  8 17  4 18 19  2 24 20(25)10 21  5 22 11  1 12  6 13  3 14  7 15

The goal is to be the player with the highest score after the last marble is used up.
Assuming the example above ends after the marble numbered 25, the winning score is 23+9=32
(because player 5 kept marble 23 and removed marble 9, while no other player got any points in this very short example game).

Here are a few more examples:

10 players; last marble is worth 1618 points: high score is 8317
13 players; last marble is worth 7999 points: high score is 146373
17 players; last marble is worth 1104 points: high score is 2764
21 players; last marble is worth 6111 points: high score is 54718
30 players; last marble is worth 5807 points: high score is 37305

What is the winning Elf's score?


--- Part Two ---
Amused by the speed of your answer, the Elves are curious:

What would the new winning Elf's score be if the number of the last marble were 100 times larger?
 */

import static java.lang.Integer.parseInt;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.common.base.Preconditions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day9 {

  @Nested
  public static class Day9Test {
    static Stream<Arguments> part1examples() {
      return Stream.of(
        arguments("9 players; last marble is worth 25 points", "32"),
        arguments("10 players; last marble is worth 1618 points", "8317"),
        arguments("13 players; last marble is worth 7999 points", "146373"),
        arguments("17 players; last marble is worth 1104 points", "2764"),
        arguments("21 players; last marble is worth 6111 points", "54718"),
        arguments("30 players; last marble is worth 5807 points", "37305")
      );
    }

    @ParameterizedTest
    @MethodSource
    public void part1examples(final String input, final String expected) {
      assertThat(new Day9(input).part1()).isEqualTo(expected);
    }

    @Test
    public void part1solution() {
      String actual = new Day9(Inputs.forDay("9").get(0)).part1();
      System.out.println(actual);
      assertThat(actual).isEqualTo("384892");
    }

    @Test
    public void part2solution() {
      String actual = new Day9(Inputs.forDay("9").get(0)).part2();
      System.out.println(actual);
      //      assertThat(actual).isEqualTo("26753");
    }
  }

  private static final class Board extends ArrayDeque<Integer> {
    void rotate(final int position) {
      if (position > 0) {
        range(0, position).forEach(i -> addFirst(removeLast()));
      }
      if (position < 0) {
        range(0, Math.abs(position) - 1).forEach(i -> addLast(removeFirst()));
      }
    }
  }

  private final int marblesCount;
  private final int playersCount;
  private final long[] scores;

  public Day9(final String input) {
    Matcher matcher = Pattern.compile("(\\d+) players; last marble is worth (\\d+) points").matcher(input);
    Preconditions.checkArgument(matcher.find());
    playersCount = parseInt(matcher.group(1));
    marblesCount = parseInt(matcher.group(2));

    scores = new long[playersCount];
  }

  private String part1() {
    return String.valueOf(solve(marblesCount));
  }

  private String part2() {
    return String.valueOf(solve(marblesCount * 100));
  }

  private long solve(final int marblesCount) {
    final Board board = new Board();
    board.add(0);
    for (int marble = 1; marble <= marblesCount; marble++) {
      if (marble % 23 == 0) {
        board.rotate(-7);
        int playerIndex = marble % playersCount;
        scores[playerIndex] += marble + board.pop();
      } else {
        board.rotate(2);
        board.addLast(marble);
      }
    }
    return Arrays.stream(scores).max()
                 .orElseThrow(IllegalStateException::new);
  }

}
