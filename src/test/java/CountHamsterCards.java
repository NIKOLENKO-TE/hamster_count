import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CountHamsterCards {
  static String filePath = "src/test/resources/Hamster.csv"; //Address of the file
  static double coinsBalance = 0;
  static final String RESET = "\033[0m";
  static final String RED = "\033[0;31m";
  static final String GREEN = "\033[0;32m";
  static final String YELLOW = "\033[0;33m";
  static final String BLUE = "\033[0;34m";
  static final String PURPLE = "\033[0;35m";
  static final String CYAN = "\033[0;36m";
  static final String WHITE = "\033[0;37m";

  static class Card {
    String name;
    int value;
    int boost;

    Card(String name, int value, int boost) {
      this.name = name;
      this.value = value;
      this.boost = boost;
    }

    double efficiency() {
      return (double) boost / value;
    }
  }

  public static void main(String[] args) throws IOException {
    List<Card> cards = readCardsFromCSV();
    printMostEfficientCombinationByValue(cards);
    saveResultsToCSV(cards);
    generateHTML(cards);
  }

  private static List<Card> readCardsFromCSV() throws IOException {
    List<Card> cards = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(";");
        if (parts.length >= 3) {
          String name = parts[0].trim();
          if (name.isEmpty() || name.contains("Cards name") || name.equals("PR&Team") || name.equals("Legal") || name.equals("SPECIALS")) {
            continue;
          }
          int value = Integer.parseInt(parts[1].trim());
          int boost = Integer.parseInt(parts[2].trim());
          cards.add(new Card(name, value, boost));
        } else if (parts.length >= 2 && parts[0].trim().equals("Coins ballance:")) {
          coinsBalance = Double.parseDouble(parts[1].trim());
        }
      }
    }
    return cards;
  }

  private static void printMostEfficientCombinationByValue(List<Card> cards) {
    List<Card> sortedByEfficiency = new ArrayList<>(cards);
    sortedByEfficiency.sort(Comparator.comparingDouble(Card::efficiency).reversed());

    List<Card> mostEfficientCombination = new ArrayList<>();
    List<Card> cardsToRemove = new ArrayList<>();
    int remainingBalance = (int) coinsBalance;
    int totalBoost = 0;
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    // Find the most efficient card
    Card mostEfficientCard = sortedByEfficiency.get(0);
    double maxEfficiency = mostEfficientCard.efficiency();

    System.out.println(YELLOW + "///////////////////////////////////Card to buy by Most Efficient Combination by Value/////////////////////////////////////////////" + RESET);
    for (Card card : sortedByEfficiency) {
      if (remainingBalance >= card.value) {
        remainingBalance -= card.value;
        totalBoost += card.boost;
        mostEfficientCombination.add(card);
        cardsToRemove.add(card);
      }
    }

    sortedByEfficiency.removeAll(cardsToRemove);
    mostEfficientCombination.sort(Comparator.comparingDouble(c -> (double) ((Card) c).boost / ((Card) c).value).reversed());
    int cardNumber = 0;
    Map<String, String> storeColors = new HashMap<>();
    storeColors.put("Markets", YELLOW);
    storeColors.put("PR&Team", GREEN);
    storeColors.put("Legal", BLUE);
    storeColors.put("SPECIALS", CYAN);

    for (Card card : mostEfficientCombination) {
      cardNumber++;
      double efficiencyPercentage = (card.efficiency() / maxEfficiency) * 100;
      String formattedValue = numberFormat.format(card.value);
      String formattedBoost = numberFormat.format(card.boost);
      String[] nameParts = card.name.split(":");
      String storeName = nameParts[0];
      String cardName = nameParts.length > 1 ? nameParts[1].trim() : "";
      String color = storeColors.getOrDefault(storeName, WHITE); // Default to white if no color found
      System.out.printf("%d.Store: [%s%s%s], Card: {%s%s%s}, value: [%s], boost: [%s], efficiency: [%.2f%%]\n", cardNumber, color, storeName, RESET, color, cardName, RESET, formattedValue.replace(",", "."), formattedBoost.replace(",", "."), efficiencyPercentage);
    }

    String formattedRemainingBalance = numberFormat.format(remainingBalance);
    System.out.println("Not enough money to buy any new card. \nRemaining balance: " + formattedRemainingBalance);
    NumberFormat numberFormatBoost = NumberFormat.getNumberInstance(Locale.US);
    String formattedBalance = numberFormatBoost.format(totalBoost);
    System.out.println(RED + "Total boost: [" + formattedBalance + "]" + RESET);
    printCoinsBalance(coinsBalance);
  }

  private static void printCoinsBalance(double coinsBalance) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    String formattedBalance = numberFormat.format(coinsBalance);
    System.out.println("Actual coins balance: " + formattedBalance);
  }

  private static void saveResultsToCSV(List<Card> cards) throws IOException {
    List<Card> sortedByEfficiency = new ArrayList<>(cards);
    sortedByEfficiency.sort(Comparator.comparingDouble(Card::efficiency).reversed());

    List<Card> mostEfficientCombination = new ArrayList<>();
    List<Card> cardsToRemove = new ArrayList<>();
    int remainingBalance = (int) coinsBalance;
    int totalBoost = 0;
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    // Find the most efficient card
    Card mostEfficientCard = sortedByEfficiency.get(0);
    double maxEfficiency = mostEfficientCard.efficiency();

    for (Card card : sortedByEfficiency) {
      if (remainingBalance >= card.value) {
        remainingBalance -= card.value;
        totalBoost += card.boost;
        mostEfficientCombination.add(card);
        cardsToRemove.add(card);
      }
    }

    sortedByEfficiency.removeAll(cardsToRemove);
    mostEfficientCombination.sort(Comparator.comparingDouble(c -> (double) ((Card) c).boost / ((Card) c).value).reversed());

    // Define the desired store order
    String[] storeOrder = {"Markets", "PR&Team", "Legal", "SPECIALS"};

    try (FileWriter writer = new FileWriter("src/test/resources/most_efficient_combination_by_value.csv")) {
      writer.append("Store");
      writer.append(",");
      writer.append("Card");
      writer.append(",");
      writer.append("Value");
      writer.append(",");
      writer.append("Boost");
      writer.append(",");
      writer.append("Efficiency");
      writer.append("\n");

      for (String store : storeOrder) {
        for (Card card : mostEfficientCombination) {
          String[] nameParts = card.name.split(":");
          String storeName = nameParts[0];
          if (storeName.equals(store)) {
            int cardNumber = mostEfficientCombination.indexOf(card) + 1;
            double efficiencyPercentage = (card.efficiency() / maxEfficiency) * 100;
            String formattedValue = numberFormat.format(card.value);
            String formattedBoost = numberFormat.format(card.boost);
            String cardName = nameParts.length > 1 ? nameParts[1].trim() : "";
            writer.append(storeName);
            writer.append(",");
            writer.append(cardName);
            writer.append(",");
            writer.append(formattedValue.replace(",", "."));
            writer.append(",");
            writer.append(formattedBoost.replace(",", "."));
            writer.append(",");
            writer.append(String.format("%.2f%%", efficiencyPercentage).replace(",", ".")); // Replace comma in percentage
            writer.append("\n");
          }
        }
      }
      // Add the remaining lines to the CSV file
      writer.append("Remaining balance: ");
      writer.append(",");
      writer.append(numberFormat.format(remainingBalance).replace(",", "."));
      writer.append("\n");
      writer.append("Total boost: ");
      writer.append(",");
      writer.append(numberFormat.format(totalBoost).replace(",", ".")); // Format totalBoost
      writer.append("\n");
      writer.append("Actual balance: ");
      writer.append(",");
      writer.append(numberFormat.format(coinsBalance).replace(",", "."));
    }
  }

  private static void generateHTML(List<Card> cards) throws IOException {
    List<Card> sortedByEfficiency = new ArrayList<>(cards);
    sortedByEfficiency.sort(Comparator.comparingDouble(Card::efficiency).reversed());

    List<Card> mostEfficientCombination = new ArrayList<>();
    List<Card> cardsToRemove = new ArrayList<>();
    int remainingBalance = (int) coinsBalance;
    int totalBoost = 0;
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    // Find the most efficient card
    Card mostEfficientCard = sortedByEfficiency.get(0);
    double maxEfficiency = mostEfficientCard.efficiency();

    for (Card card : sortedByEfficiency) {
      if (remainingBalance >= card.value) {
        remainingBalance -= card.value;
        totalBoost += card.boost;
        mostEfficientCombination.add(card);
        cardsToRemove.add(card);
      }
    }

    sortedByEfficiency.removeAll(cardsToRemove);
    mostEfficientCombination.sort(Comparator.comparingDouble(c -> (double) ((Card) c).boost / ((Card) c).value).reversed());

    // Define the desired store order
    String[] storeOrder = {"Markets", "PR&Team", "Legal", "SPECIALS"};

    try (FileWriter writer = new FileWriter("src/test/resources/index.html")) {
      writer.append("<!DOCTYPE html>\n");
      writer.append("<html>\n");
      writer.append("<head>\n");
      writer.append("<title>Hamster Card Analysis</title>\n");
      writer.append("<style>\n");
      writer.append("table {\n");
      writer.append("  border-collapse: collapse;\n");
      writer.append("  width: 100%;\n");
      writer.append("}\n");
      writer.append("th, td {\n");
      writer.append("  text-align: left;\n");
      writer.append("  padding: 8px;\n");
      writer.append("  border: 1px solid black;\n");
      writer.append("}\n");
      writer.append("th {\n");
      writer.append("  background-color: #f2f2f2;\n");
      writer.append("}\n");
      writer.append("</style>\n");
      writer.append("</head>\n");
      writer.append("<body>\n");
      writer.append("<h1>Hamster Card Analysis</h1>\n");
      writer.append("<table>\n");
      writer.append("  <tr>\n");
      writer.append("    <th>Store</th>\n");
      writer.append("    <th>Card</th>\n");
      writer.append("    <th>Value</th>\n");
      writer.append("    <th>Boost</th>\n");
      writer.append("    <th>Efficiency</th>\n");
      writer.append("  </tr>\n");

      for (String store : storeOrder) {
        for (Card card : mostEfficientCombination) {
          String[] nameParts = card.name.split(":");
          String storeName = nameParts[0];
          if (storeName.equals(store)) {
            int cardNumber = mostEfficientCombination.indexOf(card) + 1;
            double efficiencyPercentage = (card.efficiency() / maxEfficiency) * 100;
            String formattedValue = numberFormat.format(card.value);
            String formattedBoost = numberFormat.format(card.boost);
            String cardName = nameParts.length > 1 ? nameParts[1].trim() : "";
            writer.append("  <tr>\n");
            writer.append("    <td>" + storeName + "</td>\n");
            writer.append("    <td>" + cardName + "</td>\n");
            writer.append("    <td>" + formattedValue.replace(",", ".") + "</td>\n");
            writer.append("    <td>" + formattedBoost.replace(",", ".") + "</td>\n");
            writer.append("    <td>" + String.format("%.2f%%", efficiencyPercentage).replace(",", ".") + "</td>\n");
            writer.append("  </tr>\n");
          }
        }
      }
      writer.append("</table>\n");
      writer.append("<p>Remaining balance: " + numberFormat.format(remainingBalance).replace(",", ".") + "</p>\n");
      writer.append("<p>Total boost: " + numberFormat.format(totalBoost).replace(",", ".") + "</p>\n");
      writer.append("<p>Actual balance: " + numberFormat.format(coinsBalance).replace(",", ".") + "</p>\n");
      writer.append("</body>\n");
      writer.append("</html>\n");
    }
  }
}