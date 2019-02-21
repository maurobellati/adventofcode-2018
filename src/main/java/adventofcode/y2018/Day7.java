package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import lombok.Value;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 --- Day 7: The Sum of Its Parts ---
 You find yourself standing on a snow-covered coastline; apparently, you landed a little off course. The region is too hilly to see the North Pole from here, but you do spot some Elves that seem to be trying to unpack something that washed ashore. It's quite cold out, so you decide to risk creating a paradox by asking them for directions.

 "Oh, are you the search party?" Somehow, you can understand whatever Elves from the year 1018 speak; you assume it's Ancient Nordic Elvish. Could the device on your wrist also be a translator? "Those clothes don't look very warm; take this." They hand you a heavy coat.

 "We do need to find our way back to the North Pole, but we have higher priorities at the moment. You see, believe it or not, this box contains something that will solve all of Santa's transportation problems - at least, that's what it looks like from the pictures in the instructions." It doesn't seem like they can read whatever language it's in, but you can: "Sleigh kit. Some assembly required."

 "'Sleigh'? What a wonderful name! You must help us assemble this 'sleigh' at once!" They start excitedly pulling more parts out of the box.

 The instructions specify a series of steps and requirements about which steps must be finished before others can begin (your puzzle input). Each step is designated by a single letter. For example, suppose you have the following instructions:

 Step C must be finished before step A can begin.
 Step C must be finished before step F can begin.
 Step A must be finished before step B can begin.
 Step A must be finished before step D can begin.
 Step B must be finished before step E can begin.
 Step D must be finished before step E can begin.
 Step F must be finished before step E can begin.
 Visually, these requirements look like this:


 -->A--->B--
 /    \      \
 C      -->D----->E
 \           /
 ---->F-----
 Your first goal is to determine the order in which the steps should be completed. If more than one step is ready, choose the step which is first alphabetically. In this example, the steps would be completed as follows:

 Only C is available, and so it is done first.
 Next, both A and F are available. A is first alphabetically, so it is done next.
 Then, even though F was available earlier, steps B and D are now also available, and B is the first alphabetically of the three.
 After that, only D and F are available. E is not available because only some of its prerequisites are complete. Therefore, D is completed next.
 F is the only choice, so it is done next.
 Finally, E is completed.
 So, in this example, the correct order is CABDFE.

 In what order should the steps in your instructions be completed?

 --- Part Two ---
 As you're about to begin construction, four of the Elves offer to help. "The sun will set soon; it'll go faster if we work together." Now, you need to account for multiple people working on steps simultaneously. If multiple steps are available, workers should still begin them in alphabetical order.

 Each step takes 60 seconds plus an amount corresponding to its letter: A=1, B=2, C=3, and so on. So, step A takes 60+1=61 seconds, while step Z takes 60+26=86 seconds. No time is required between steps.

 To simplify things for the example, however, suppose you only have help from one Elf (a total of two workers) and that each step takes 60 fewer seconds (so that step A takes 1 second and step Z takes 26 seconds). Then, using the same instructions as above, this is how each second would be spent:

 Second   Worker 1   Worker 2   Done
 0        C          .
 1        C          .
 2        C          .
 3        A          F       C
 4        B          F       CA
 5        B          F       CA
 6        D          F       CAB
 7        D          F       CAB
 8        D          F       CAB
 9        D          .       CABF
 10        E          .       CABFD
 11        E          .       CABFD
 12        E          .       CABFD
 13        E          .       CABFD
 14        E          .       CABFD
 15        .          .       CABFDE
 Each row represents one second of time.
 The Second column identifies how many seconds have passed as of the beginning of that second.
 Each worker column shows the step that worker is currently doing (or . if they are idle).
 The Done column shows completed steps.

 Note that the order of the steps has changed;
 this is because steps now take time to finish and multiple workers can begin multiple steps simultaneously.

 In this example, it would take 15 seconds for two workers to complete these steps.

 With 5 workers and the 60+ second step durations described above, how long will it take to complete all of the steps?
 */
public class Day7 {

  @Nested
  public static class Day7Test {

    static Stream<Arguments> part1examples() {
      return Stream.of(
        arguments(asList("Step C must be finished before step A can begin.",
                         "Step C must be finished before step F can begin.",
                         "Step A must be finished before step B can begin.",
                         "Step A must be finished before step D can begin.",
                         "Step B must be finished before step E can begin.",
                         "Step D must be finished before step E can begin.",
                         "Step F must be finished before step E can begin."), "CABDFE")
      );
    }

    @ParameterizedTest
    @MethodSource
    public void part1examples(final List<String> input, String expected) {
      assertThat(new Day7(input).part1()).isEqualTo(expected);
    }

    @Test
    public void part1solution() {
      String actual = new Day7(Inputs.forDay("7")).part1();
      System.out.println(actual);
    }

    @Test
    public void part2examples() {
      List<String> input = asList("Step C must be finished before step A can begin.",
                                  "Step C must be finished before step F can begin.",
                                  "Step A must be finished before step B can begin.",
                                  "Step A must be finished before step D can begin.",
                                  "Step B must be finished before step E can begin.",
                                  "Step D must be finished before step E can begin.",
                                  "Step F must be finished before step E can begin.");
      String actual = new Day7(input).part2(2, c -> c - 'A' + 1);
      assertThat(actual).isEqualTo("15");
    }

    @Test
    public void part2solution() {
      String actual = new Day7(Inputs.forDay("7")).part2(5, c -> c - 'A' + 61);
      System.out.println(actual);
      assertThat(actual).isEqualTo("1118");
    }
  }

  private static final Pattern REGEX = Pattern.compile("Step (\\w) must.*(\\w+) can begin");
  private final Multimap<Character, Character> graph;

  public Day7(final List<String> inputLines) {
    graph = HashMultimap.create();
    inputLines.forEach(line -> {
      Matcher matcher = REGEX.matcher(line);
      checkArgument(matcher.find());
      graph.put(matcher.group(1).toCharArray()[0], matcher.group(2).toCharArray()[0]);
    });
  }

  private String part1() {
    System.out.println(graph);

    Set<Character> roots = difference(graph.keySet(), newHashSet(graph.values()));

    List<Character> result = newArrayList();

    TreeSet<Character> nextTasks = new TreeSet<>(roots);
    System.out.println("roots: " + roots);

    while (!nextTasks.isEmpty()) {
      System.out.printf("nextTasks: %s | ", nextTasks);
      Predicate<Character> hasNoPredecessors = node -> !graph.containsValue(node);
      Character next = nextTasks.stream()
                                .filter(hasNoPredecessors)
                                .findFirst()
                                .orElseThrow(IllegalStateException::new);
      nextTasks.remove(next);

      System.out.printf("choose: %s%n", next);

      result.add(next);

      List<Character> unseenNewNodes = graph.removeAll(next)
                                            .stream().filter(it -> !result.contains(it))
                                            .collect(toList());
      nextTasks.addAll(unseenNewNodes);
    }

    return result.stream().map(Object::toString).collect(joining(""));
  }

  @Value
  private static class Schedule {
    private int endsAt;
    private Character name;

    @Override
    public String toString() {
      return "[" + name + ", endsAt=" + endsAt + "]";
    }
  }

  private String part2(final int workersCount, final Function<Character, Integer> taskTimeProvider) {
    System.out.println(graph);
    int taskCount = Sets.union(graph.keySet(), newHashSet(graph.values())).size();

    LinkedHashMap<Integer, Schedule> workersAndSchedules = Maps.newLinkedHashMap();
    rangeClosed(1, workersCount)
      .forEach(i -> workersAndSchedules.put(i, null));

    List<Character> completedTasks = newArrayList();

    Set<Character> roots = difference(graph.keySet(), newHashSet(graph.values()));
    TreeSet<Character> nextTasks = new TreeSet<>(roots);
    System.out.println("roots: " + roots);

    int t = 0;
    while (completedTasks.size() < taskCount) {
      System.out.printf("### t = %d%n", t);

      // Free up finished tasks
      List<Map.Entry<Integer, Schedule>> ended = findTaskEndedAt(workersAndSchedules, t);
      ended.stream()
           .sorted(comparing(e -> e.getValue().getName()))
           .forEach(entry -> {
             Character completedTask = entry.getValue().getName();
             System.out.printf("completed task %s%n", completedTask);
             completedTasks.add(completedTask);
             nextTasks.remove(completedTask);

             // Free up worker
             Integer worker = entry.getKey();
             workersAndSchedules.put(worker, null);

             // Enqueue new unseen tasks
             List<Character> unseenNewTasks = graph.removeAll(completedTask)
                                                   .stream().filter(it -> !completedTasks.contains(it))
                                                   .collect(toList());
             nextTasks.addAll(unseenNewTasks);
           });

      System.out.printf("nextTasks: %s | ", nextTasks);
      Predicate<Character> hasNoPredecessors = node -> !graph.containsValue(node);
      long freeWorkersCount = workersAndSchedules.values().stream().filter(Objects::isNull).count();
      List<Character> startableTasks = nextTasks.stream()
                                                .filter(hasNoPredecessors)
                                                .limit(freeWorkersCount)
                                                .collect(toList());

      System.out.printf("startable: %s%n", startableTasks);

      if (!startableTasks.isEmpty()) {
        for (Character task : startableTasks) {
          Integer taskDuration = taskTimeProvider.apply(task);
          nextTasks.remove(task);
          Integer worker = workersAndSchedules.entrySet()
                                              .stream()
                                              .filter(e -> e.getValue() == null)
                                              .findFirst()
                                              .map(Map.Entry::getKey)
                                              .orElseThrow(() -> new IllegalStateException("One worker must be free here"));
          Schedule schedule = new Schedule(t + taskDuration, task);
          System.out.printf("assigned %s to W%d%n", schedule, worker);
          workersAndSchedules.put(worker, schedule);
        }
      }

      System.out.println("workers: " + workersAndSchedules);
      t = workersAndSchedules.values().stream()
                             .filter(Objects::nonNull)
                             .mapToInt(Schedule::getEndsAt)
                             .min().orElse(t);
      System.out.printf("jumping at time %d%n%n", t);

    }

    System.out.printf("Time %d | schedule: %s%n", t, completedTasks);
    return String.valueOf(t);
  }

  private List<Map.Entry<Integer, Schedule>> findTaskEndedAt(final Map<Integer, Schedule> workerToTask, final int currentT) {
    return workerToTask.entrySet()
                       .stream()
                       .filter(e1 -> e1.getValue() != null && e1.getValue().getEndsAt() <= currentT)
                       .collect(toList());
  }
}
