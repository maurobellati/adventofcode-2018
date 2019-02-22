package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

/*
--- Day 8: Memory Maneuver ---
The sleigh is much easier to pull than you'd expect for something its weight. Unfortunately, neither you nor the Elves know which way the North Pole is from here.

You check your wrist device for anything that might help. It seems to have some kind of navigation system! Activating the navigation system produces more bad news: "Failed to start navigation system. Could not read software license file."

The navigation system's license file consists of a list of numbers (your puzzle input). The numbers define a data structure which, when processed, produces some kind of tree that can be used to calculate the license number.

The tree is made up of nodes; a single, outermost node forms the tree's root, and it contains all other nodes in the tree (or contains nodes that contain nodes, and so on).

Specifically, a node consists of:

A header, which is always exactly two numbers:
The quantity of child nodes.
The quantity of metadata entries.
Zero or more child nodes (as specified in the header).
One or more metadata entries (as specified in the header).
Each child node is itself a node that has its own header, child nodes, and metadata. For example:

2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2
A----------------------------------
    B----------- C-----------
                     D-----
In this example, each node of the tree is also marked with an underline starting with a letter for easier identification. In it, there are four nodes:

A, which has 2 child nodes (B, C) and 3 metadata entries (1, 1, 2).
B, which has 0 child nodes and 3 metadata entries (10, 11, 12).
C, which has 1 child node (D) and 1 metadata entry (2).
D, which has 0 child nodes and 1 metadata entry (99).
The first check done on the license file is to simply add up all of the metadata entries. In this example, that sum is 1+1+2+10+11+12+2+99=138.

What is the sum of all metadata entries?

--- Part Two ---
The second check is slightly more complicated: you need to find the value of the root node (A in the example above).

The value of a node depends on whether it has child nodes.

If a node has no child nodes, its value is the sum of its metadata entries. So, the value of node B is 10+11+12=33, and the value of node D is 99.

However, if a node does have child nodes, the metadata entries become indexes which refer to those child nodes.
A metadata entry of 1 refers to the first child node, 2 to the second, 3 to the third, and so on.
The value of this node is the sum of the values of the child nodes referenced by the metadata entries.
If a referenced child node does not exist, that reference is skipped.
A child node can be referenced multiple time and counts each time it is referenced.
A metadata entry of 0 does not refer to any child node.

For example, again using the above nodes:

Node C has one metadata entry, 2.
Because node C has only one child node, 2 references a child node which does not exist, and so the value of node C is 0.
Node A has three metadata entries: 1, 1, and 2. The 1 references node A's first child node, B, and the 2 references node A's second child node, C.
Because node B has a value of 33 and node C has a value of 0, the value of node A is 33+33+0=66.
So, in this example, the value of the root node is 66.

What is the value of the root node?
 */
public class Day8 {

  @Nested
  public static class Day8Test {

    @Test
    public void part1examples() {
      assertThat(new Day8(asList("2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2")).part1()).isEqualTo("138");
    }

    @Test
    public void part1solution() {
      String actual = new Day8(Inputs.forDay("8")).part1();
      System.out.println(actual);
      assertThat(actual).isEqualTo("42146");
    }

    @Test
    public void part2examples() {
      assertThat(new Day8(asList("2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2")).part2()).isEqualTo("66");
    }

    @Test
    public void part2solution() {
      String actual = new Day8(Inputs.forDay("8")).part2();
      System.out.println(actual);
      assertThat(actual).isEqualTo("26753");
    }
  }

  private static final Pattern REGEX = Pattern.compile("Step (\\w) must.*(\\w+) can begin");

  private final Deque<Integer> items;

  public Day8(final List<String> inputLines) {
    checkArgument(inputLines.size() == 1);
    items = Arrays.stream(inputLines.get(0).split(" "))
                  .map(Integer::parseInt)
                  .collect(toCollection(LinkedList::new));
  }

  private int computeMetadataSum(final Queue<Integer> input) {
    if (input.isEmpty()) {
      return 0;
    }
    checkArgument(input.size() >= 3);

    Integer childrenCount = input.poll();
    Integer metadataCount = input.poll();

    int childrenValue = range(0, childrenCount).map(i -> computeMetadataSum(input)).sum();

    int value = pollAndSum(input, metadataCount);
    return value + childrenValue;
  }

  private Integer getChildValues(final Queue<Integer> input) {
    if (input.isEmpty()) {
      return 0;
    }
    checkArgument(input.size() >= 3);

    Integer childrenCount = input.poll();
    Integer metadataCount = input.poll();
    List<Integer> children = range(0, childrenCount)
      .mapToObj(i -> getChildValues(input))
      .collect(toList());

    checkState(children.size() == childrenCount);

    if (childrenCount == 0) {
      return pollAndSum(input, metadataCount);
    }

    return range(0, metadataCount).mapToObj(i -> input.poll())
                                  .filter(index -> index <= children.size())
                                  .mapToInt(index -> children.get(index - 1))
                                  .sum();
  }

  private String part1() {
    return String.valueOf(computeMetadataSum(items));
  }

  private String part2() {
    return String.valueOf(getChildValues(items));
  }

  private int pollAndSum(final Queue<Integer> input, final Integer count) {
    return range(0, count).map(i -> input.poll()).sum();
  }

}
