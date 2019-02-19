package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Range;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
--- Day 4: Repose Record ---
You've sneaked into another supply closet - this time, it's across from the prototype suit manufacturing lab. You need to sneak inside and fix the issues with the suit, but there's a guard stationed outside the lab, so this is as close as you can safely get.

As you search the closet for anything that might help, you discover that you're not the first person to want to sneak in. Covering the walls, someone has spent an hour starting every midnight for the past few months secretly observing this guard post! They've been writing down the ID of the one guard on duty that night - the Elves seem to have decided that one guard was enough for the overnight shift - as well as when they fall asleep or wake up while at their post (your puzzle input).

For example, consider the following records, which have already been organized into chronological order:

[1518-11-01 00:00] Guard #10 begins shift
[1518-11-01 00:05] falls asleep
[1518-11-01 00:25] wakes up
[1518-11-01 00:30] falls asleep
[1518-11-01 00:55] wakes up
[1518-11-01 23:58] Guard #99 begins shift
[1518-11-02 00:40] falls asleep
[1518-11-02 00:50] wakes up
[1518-11-03 00:05] Guard #10 begins shift
[1518-11-03 00:24] falls asleep
[1518-11-03 00:29] wakes up
[1518-11-04 00:02] Guard #99 begins shift
[1518-11-04 00:36] falls asleep
[1518-11-04 00:46] wakes up
[1518-11-05 00:03] Guard #99 begins shift
[1518-11-05 00:45] falls asleep
[1518-11-05 00:55] wakes up
Timestamps are written using year-month-day hour:minute format. The guard falling asleep or waking up is always the one whose shift most recently started. Because all asleep/awake times are during the midnight hour (00:00 - 00:59), only the minute portion (00 - 59) is relevant for those events.

Visually, these records show that the guards are asleep at these times:

Date   ID   Minute
            000000000011111111112222222222333333333344444444445555555555
            012345678901234567890123456789012345678901234567890123456789
11-01  #10  .....####################.....#########################.....
11-02  #99  ........................................##########..........
11-03  #10  ........................#####...............................
11-04  #99  ....................................##########..............
11-05  #99  .............................................##########.....
The columns are Date, which shows the month-day portion of the relevant day; ID, which shows the guard on duty that day; and Minute, which shows the minutes during which the guard was asleep within the midnight hour. (The Minute column's header shows the minute's ten's digit in the first row and the one's digit in the second row.) Awake is shown as ., and asleep is shown as #.

Note that guards count as asleep on the minute they fall asleep, and they count as awake on the minute they wake up. For example, because Guard #10 wakes up at 00:25 on 1518-11-01, minute 25 is marked as awake.

If you can figure out the guard most likely to be asleep at a specific time, you might be able to trick that guard into working tonight so you can have the best chance of sneaking in.
You have two strategies for choosing the best guard/minute combination.

Strategy 1: Find the guard that has the most minutes asleep. What minute does that guard spend asleep the most?

In the example above, Guard #10 spent the most minutes asleep, a total of 50 minutes (20+25+5), while Guard #99 only slept for a total of 30 minutes (10+10+10). Guard #10 was asleep most during minute 24 (on two days, whereas any other minute the guard was asleep was only seen on one day).

While this example listed the entries in chronological order, your entries are in the order you found them. You'll need to organize them before they can be analyzed.

What is the ID of the guard you chose multiplied by the minute you chose? (In the above example, the answer would be 10 * 24 = 240.)


--- Part Two ---
Strategy 2: Of all guards, which guard is most frequently asleep on the same minute?

In the example above, Guard #99 spent minute 45 asleep more than any other guard or minute - three times in total. (In all other cases, any guard spent any minute asleep at most twice.)

What is the ID of the guard you chose multiplied by the minute you chose? (In the above example, the answer would be 99 * 45 = 4455.)

 */
public class Day4 {

  @Nested
  public static class Day4Test {

    static Stream<Arguments> part1examples() {
      return Stream.of(
        arguments(shuffle("[1518-11-01 00:00] Guard #10 begins shift",
                          "[1518-11-01 00:05] falls asleep",
                          "[1518-11-01 00:25] wakes up",
                          "[1518-11-01 00:30] falls asleep",
                          "[1518-11-01 00:55] wakes up",
                          "[1518-11-01 23:58] Guard #99 begins shift",
                          "[1518-11-02 00:40] falls asleep",
                          "[1518-11-02 00:50] wakes up",
                          "[1518-11-03 00:05] Guard #10 begins shift",
                          "[1518-11-03 00:24] falls asleep",
                          "[1518-11-03 00:29] wakes up",
                          "[1518-11-04 00:02] Guard #99 begins shift",
                          "[1518-11-04 00:36] falls asleep",
                          "[1518-11-04 00:46] wakes up",
                          "[1518-11-05 00:03] Guard #99 begins shift",
                          "[1518-11-05 00:45] falls asleep",
                          "[1518-11-05 00:55] wakes up"), "240")
      );
    }

    static Stream<Arguments> part2examples() {
      return Stream.of(
        arguments(shuffle("[1518-11-01 00:00] Guard #10 begins shift",
                          "[1518-11-01 00:05] falls asleep",
                          "[1518-11-01 00:25] wakes up",
                          "[1518-11-01 00:30] falls asleep",
                          "[1518-11-01 00:55] wakes up",
                          "[1518-11-01 23:58] Guard #99 begins shift",
                          "[1518-11-02 00:40] falls asleep",
                          "[1518-11-02 00:50] wakes up",
                          "[1518-11-03 00:05] Guard #10 begins shift",
                          "[1518-11-03 00:24] falls asleep",
                          "[1518-11-03 00:29] wakes up",
                          "[1518-11-04 00:02] Guard #99 begins shift",
                          "[1518-11-04 00:36] falls asleep",
                          "[1518-11-04 00:46] wakes up",
                          "[1518-11-05 00:03] Guard #99 begins shift",
                          "[1518-11-05 00:45] falls asleep",
                          "[1518-11-05 00:55] wakes up"), "4455")
      );
    }

    private static List<String> shuffle(final String... input) {
      List<String> result = asList(input);
      Collections.shuffle(result);
      return result;
    }

    @ParameterizedTest
    @MethodSource
    public void part1examples(final List<String> input, String expected) {
      assertThat(new Day4(input).part1()).isEqualTo(expected);
    }

    @Test
    public void part1solution() {
      String actual = new Day4(Inputs.forDay("4")).part1();
      System.out.println(actual);
    }

    @ParameterizedTest
    @MethodSource
    public void part2examples(final List<String> input, String expected) {
      assertThat(new Day4(input).part2()).isEqualTo(expected);
    }

    @Test
    public void part2solution() {
      String actual = new Day4(Inputs.forDay("4")).part2();
      System.out.println(actual);
    }
  }

  private static final Pattern GUARD_BEGINS_SHIFT = Pattern.compile("Guard #(\\d+)");
  private static final Pattern GUARD_FALLS_ASLEEP = Pattern.compile("(\\d+)] falls asleep");
  private static final Pattern GUARD_WAKES_UP = Pattern.compile("(\\d+)] wakes up");
  private final Multimap<Integer, Range<Integer>> guardIdToSleepRanges;

  public Day4(final List<String> inputLines) {
    guardIdToSleepRanges = parseSchedule(inputLines);
  }

  private int findMostSleepyGuardId() {
    Map<Integer, Integer> guardIdToTotalSleepTime = Maps.transformValues(guardIdToSleepRanges.asMap(),
                                                                         this::totalSpan);

    Map.Entry<Integer, Integer> maxEntry = Collections.max(guardIdToTotalSleepTime.entrySet(), comparingInt(Map.Entry::getValue));
    return maxEntry.getKey();
  }

  private Multiset.Entry<Integer> findMostSleepyMinuteAndValue(final Collection<Range<Integer>> input) {
    Multiset<Integer> byMinute = HashMultiset.create();

    input.stream()
         .flatMap(range -> rangeClosed(range.lowerEndpoint(), range.upperEndpoint()).boxed())
         .forEach(byMinute::add);

    return Collections.max(byMinute.entrySet(), comparingInt(Multiset.Entry::getCount));
  }

  private Map.Entry<Integer, Integer> findMostSleepyMinutePerGuard() {
    Map<Integer, Multiset.Entry<Integer>> guardIdToMostSleepyMinute = Maps.transformValues(guardIdToSleepRanges.asMap(),
                                                                                           this::findMostSleepyMinuteAndValue);

    Map.Entry<Integer, Multiset.Entry<Integer>> max = Collections.max(guardIdToMostSleepyMinute.entrySet(),
                                                                      comparing((Map.Entry<Integer, Multiset.Entry<Integer>> it) -> it.getValue().getCount()));
    return new AbstractMap.SimpleEntry<>(max.getKey(), max.getValue().getElement());
  }

  private Multimap<Integer, Range<Integer>> parseSchedule(final List<String> input) {
    Multimap<Integer, Range<Integer>> result = HashMultimap.create();

    List<String> sorted = input.stream()
                               .sorted()
                               .collect(toList());

    int i = 0;
    Integer guardId = null;
    Integer fallsAsleepAt = null;

    while (i < sorted.size()) {
      Matcher guardMatcher = GUARD_BEGINS_SHIFT.matcher(sorted.get(i));
      if (guardMatcher.find()) {
        guardId = parseInt(guardMatcher.group(1));
        i++;
        continue;
      }

      Matcher fallsAsleepMatcher = GUARD_FALLS_ASLEEP.matcher(sorted.get(i));
      if (fallsAsleepMatcher.find()) {
        fallsAsleepAt = parseInt(fallsAsleepMatcher.group(1));
        i++;
        continue;
      }

      Matcher wakesUpMatcher = GUARD_WAKES_UP.matcher(sorted.get(i));
      if (wakesUpMatcher.find()) {
        int wakesUpAt = parseInt(wakesUpMatcher.group(1));

        checkNotNull(guardId);
        checkNotNull(fallsAsleepAt);
        result.put(guardId, Range.closed(fallsAsleepAt, wakesUpAt - 1));

        i++;
        continue;
      }

    }
    return result;
  }

  private String part1() {
    int mostSleepyGuardId = findMostSleepyGuardId();
    Multiset.Entry<Integer> mostSleepyMinuteAndValue = findMostSleepyMinuteAndValue(scheduleForGuard(mostSleepyGuardId));
    int mostSleepyMinute = mostSleepyMinuteAndValue.getElement();

    int result = mostSleepyGuardId * mostSleepyMinute;
    System.out.printf("GuardId (%s) x minute (%s) = %s%n", mostSleepyGuardId, mostSleepyMinute, result);
    return String.valueOf(result);
  }

  private String part2() {
    Map.Entry<Integer, Integer> guardAndMinute = findMostSleepyMinutePerGuard();

    Integer result = guardAndMinute.getKey() * guardAndMinute.getValue();
    System.out.printf("GuardId (%s) x minute (%s) = %s%n", guardAndMinute.getKey(), guardAndMinute.getValue(), result);
    return String.valueOf(result);
  }

  private Collection<Range<Integer>> scheduleForGuard(final int guardId) {
    return guardIdToSleepRanges.get(guardId);
  }

  private int totalSpan(final @Nullable Collection<Range<Integer>> ranges) {
    return ranges.stream()
                 .mapToInt(it -> it.upperEndpoint() - it.lowerEndpoint() + 1)
                 .sum();
  }

}
