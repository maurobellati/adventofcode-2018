package adventofcode.y2018;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;

/*
 --- Day 2: Inventory Management System ---
 You stop falling through time, catch your breath, and check the screen on the device. "Destination reached. Current Year: 1518. Current Location: North Pole Utility Closet 83N10." You made it! Now, to find those anomalies.

 Outside the utility closet, you hear footsteps and a voice. "...I'm not sure either. But now that so many people have chimneys, maybe he could sneak in that way?" Another voice responds, "Actually, we've been working on a new kind of suit that would let him fit through tight spaces like that. But, I heard that a few days ago, they lost the prototype fabric, the design plans, everything! Nobody on the team can even seem to remember important details of the project!"

 "Wouldn't they have had enough fabric to fill several boxes in the warehouse? They'd be stored together, so the box IDs should be similar. Too bad it would take forever to search the warehouse for two similar box IDs..." They walk too far away to hear any more.

 Late at night, you sneak to the warehouse - who knows what kinds of paradoxes you could cause if you were discovered - and use your fancy wrist device to quickly scan every box and produce a list of the likely candidates (your puzzle input).

 To make sure you didn't miss any, you scan the likely candidate boxes again, counting the number that have an ID containing exactly two of any letter and then separately counting those with exactly three of any letter. You can multiply those two counts together to get a rudimentary checksum and compare it to what your device predicts.

 For example, if you see the following box IDs:

 abcdef contains no letters that appear exactly two or three times.
 bababc contains two a and three b, so it counts for both.
 abbcde contains two b, but no letter appears exactly three times.
 abcccd contains three c, but no letter appears exactly two times.
 aabcdd contains two a and two d, but it only counts once.
 abcdee contains two e.
 ababab contains three a and three b, but it only counts once.
 Of these box IDs, four of them contain a letter which appears exactly twice,
 and three of them contain a letter which appears exactly three times.
 Multiplying these together produces a checksum of 4 * 3 = 12.

 What is the checksum for your list of box IDs?
 */
public class Day2Part1 extends Base {

  @Nested
  public static class Day2Part1Test {

    @Test
    public void example() {
      assertThat(new Day2Part1(asList("abcdef",
                                      "bababc",
                                      "abbcde",
                                      "abcccd",
                                      "aabcdd",
                                      "abcdee",
                                      "ababab")).solve()).isEqualTo("12");
    }

    @Test
    public void getFrequenciesByCharTest() {
      Map<String, Integer> actual = getFrequenciesByChar("hello");
      assertThat(actual).isEqualTo(ImmutableMap.of("h", 1,
                                                   "e", 1,
                                                   "l", 2,
                                                   "o", 1));
    }

    @Test
    public void getFrequenciesByCountTest() {
      ArrayListMultimap<Integer, String> expected = ArrayListMultimap.create();
      expected.put(1, "h");
      expected.put(1, "e");
      expected.put(1, "o");
      expected.put(2, "l");
      Multimap<Integer, String> actual = getFrequenciesByCount("hello");
      assertThat(actual.size()).isEqualTo(4);
      assertThat(actual.keySet().size()).isEqualTo(2);
      assertThat(actual.get(1)).containsExactlyInAnyOrder("h", "e", "o");
      assertThat(actual.get(2)).containsExactly("l");
    }

    @Test
    public void solution() {
      System.out.println(new Day2Part1(Inputs.forDay("2")).solve());
    }

  }

  public Day2Part1(final List<String> inputLines) {
    super(inputLines);
  }

  /**
   Given "hello" returns {"h" => 1, "e" => 1, "l" => 2, "o" => 1}
   */
  static Map<String, Integer> getFrequenciesByChar(final String input) {
    Map<String, Integer> result = newHashMap();
    asList(input.split(""))
      .forEach(s -> result.merge(s, 1, Integer::sum));
    return result;
  }

  /**
   Given "hello" returns {1 => ["h", "e", "o"], 2 => ["l"]}
   */
  static Multimap<Integer, String> getFrequenciesByCount(final String input) {
    Map<String, Integer> counts = getFrequenciesByChar(input);
    return Multimaps.invertFrom(Multimaps.forMap(counts), ArrayListMultimap.create());
  }

  public String solve() {
    Multiset<Integer> values = HashMultiset.create();

    inputLines.stream()
              .map(Day2Part1::getFrequenciesByCount)
              .map(Multimap::keySet)
              .forEach(values::addAll);

    return String.valueOf(values.count(2) * values.count(3));

  }
}
