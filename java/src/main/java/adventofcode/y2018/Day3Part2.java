package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
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
Amidst the chaos, you notice that exactly one claim doesn't overlap by even a single square inch of fabric with any other claim. If you can somehow draw attention to it, maybe the Elves will be able to make Santa's suit after all!

For example, in the claims above, only claim 3 is intact after all claims are made.

What is the ID of the only claim that doesn't overlap?
 */
public class Day3Part2 extends Day3 {

  @Nested
  public static class Day3Part2Test {

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
      assertThat(new Day3Part2(input).solve()).isEqualTo(expected);
    }

    @Test
    public void solution() {
      String actual = new Day3Part2(Inputs.forDay("3")).solve();
      System.out.println(actual);
      //      assertThat(actual).isEqualTo("118539");
    }

  }

  public Day3Part2(final List<String> inputLines) {
    super(inputLines);
  }

  /**
   * Almost same idea as Part1.
   * Use a multimap to map point->square
   * Collect overlapping squares
   * Diff from all squares to find the non-overlapping one.
   */
  @Override
  public String solve() {
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

}
