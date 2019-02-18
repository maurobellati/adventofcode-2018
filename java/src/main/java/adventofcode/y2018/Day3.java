package adventofcode.y2018;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Integer.parseInt;
import static java.util.stream.IntStream.rangeClosed;

import lombok.Value;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class Day3 extends Base {

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

  public Day3(final List<String> inputLines) {
    super(inputLines);
  }

  protected Stream<Square> getSquares() {
    return inputLines.stream()
                     .map(this::parse);
  }

  protected Square parse(final String line) {
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
