import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CountHamsterCards {
  static String filePath = "src/test/resources/Hamster.csv"; //Address of the file
  static double coinsBalance = 0;
  static final String RESET = "\033[0m";
  static final String RED = "\033[0;31m";
  static final String GREEN = "\033[0;32m";
  static final String YELLOW = "\033[0;33m";

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
      if (value == 0) {
        return 0; // or handle this case in a way that makes sense for your application
      }
      return (double) boost / value;
    }
  }

  public static void main(String[] args) throws IOException {
    //FileInputStream file = new FileInputStream(filePath);
    //Workbook workbook = new XSSFWorkbook(file);
    //Sheet sheet = workbook.getSheetAt(0);
    List<Card> cards = new ArrayList<>();
    //getCellsFromTable(sheet);
    excludeNamesMarket(cards);
    //printSorted(cards);
    calculateEfficiencyPercentage(cards);
    //workbook.close();
    //file.close();
    printMostEfficientByBoost(cards);
    printMostEfficientCombinationByBoost(cards);
    printMostEfficientCombinationByValue(cards);
  }

  /*private static void getCellsFromTable(Sheet sheet) {
    Row firstRow = sheet.getRow(0);
    if (firstRow != null) {
      Cell cellE1 = firstRow.getCell(4); // E1
      if (cellE1 != null && cellE1.getCellType() == CellType.NUMERIC) {
        coinsBalance = cellE1.getNumericCellValue();
      }
    }
  }*/

  private static void excludeNamesMarket(List<Card> cards) throws IOException {
    // Read the CSV file
    List<String[]> lines = new ArrayList<>();
    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line.split(";"));
      }
    }

    // Process the lines
    for (int i = 1; i < lines.size(); i++) { // Start from line 1 to skip the header
      String[] parts = lines.get(i);
      if (parts.length >= 3) {
        String name = parts[0].trim();
        if (name.isEmpty() || name.contains("Cards name")) {
          continue; // Skip rows with empty names or containing "Cards name"
        }
        if (name.equals("PR&Team") || name.equals("Legal") || name.equals("SPECIALS")) {
          continue; // Skip these specific names
        }
        int value = Integer.parseInt(parts[1].trim());
        int boost = Integer.parseInt(parts[2].trim());
        cards.add(new Card(name, value, boost));
      }
    }
    // Get the coins balance
    if (lines.size() > 0) {
      String[] firstLine = lines.get(0);
      if (firstLine.length >= 2) {
        coinsBalance = Double.parseDouble(firstLine[1].trim());
      }
    }
  }

  private static void printSorted(List<Card> cards) {
    cards.sort(Comparator.comparingDouble(Card::efficiency).reversed());
    sortFirstTenEffiCard(cards);
  }
  /*private static void excludeNamesMarket(Sheet sheet, List<Card> cards) {
    for (Row row : sheet) {
      Cell firstCell = row.getCell(0);
      if (firstCell != null && firstCell.getCellType() == CellType.STRING && "Coins balance:".equals(firstCell.getStringCellValue())) {
        Cell cellB1 = row.getCell(1);
        if (cellB1 != null && cellB1.getCellType() == CellType.NUMERIC) {
          coinsBalance = cellB1.getNumericCellValue();
        }
      }
      if (row.getRowNum() > 0 && row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) {
        String name = row.getCell(0).getStringCellValue();
        if (name.isEmpty() || name.trim().isEmpty() || name.contains("Cards name")) {
          continue; // Skip rows with empty names or containing "Cards name"
        }
        if (name.equals("PR&Team") || name.equals("Legal") || name.equals("SPECIALS")) {
          continue; // Skip these specific names
        }
        int value = row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC ? (int) row.getCell(1).getNumericCellValue() : 0;
        int boost = row.getCell(2) != null && row.getCell(2).getCellType() == CellType.NUMERIC ? (int) row.getCell(2).getNumericCellValue() : 0;
        cards.add(new Card(name, value, boost));
      }
    }
  }*/

  private static void printCoinsBallance(double coinsBalance, String x) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    String formattedBalance = numberFormat.format(coinsBalance);
    System.out.println(x + formattedBalance);
  }
  private static void sortFirstTenEffiCard(List<Card> cards) {
    //System.out.println("Top 30 cards by performance relative to [boost / price]");
    int countEff = 0;
    for (Card card : cards) {
      if (countEff >= 30) {
        break; // Exit the loop after 10 cards
      }
      countEff++;
    }
  }
  private static void calculateEfficiencyPercentage(List<Card> cards) {
    // Находим самую эффективную карту
    Card mostEfficientCard = cards.get(0); // Первая карта после сортировки - самая эффективная
    double maxEfficiency = mostEfficientCard.efficiency();
    int cardNumber = 0;
    // Вычисляем эффективность каждой карты относительно самой эффективной
    for (Card card : cards) {
      cardNumber++;
      double efficiencyPercentage = (card.efficiency() / maxEfficiency) * 100;
      NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
      String formattedValue = numberFormat.format(card.value);
      String formattedBoost = numberFormat.format(card.boost);
      // Разделение имени на магазин и карточку
      String[] nameParts = card.name.split(":");
      String storeName = nameParts[0];
      String cardName = nameParts.length > 1 ? nameParts[1].trim() : "";  // Удаляем лишние пробелы

      // System.out.printf("%d.Store: [%s], Card: {%s}, value: [%s], boost: [%s], efficiency: [%.2f%%]\n", cardNumber, storeName, cardName, formattedValue, formattedBoost, efficiencyPercentage);

      if (cardNumber >= 30) {
        break; // Прерываем цикл после вывода нужного количества карт
      }
    }
  }
  private static void printMostEfficientByBoost(List<Card> cards) {
    List<Card> sortedByBoost = new ArrayList<>(cards);
    sortedByBoost.sort((c1, c2) -> c2.boost - c1.boost);
    //System.out.println("Top 30 cards by efficiency relative to [boost] where the most effective is {boost} = 100%");
    int cardNumber = 0;
    int totalBoost = 0;
    for (Card card : sortedByBoost) {
      cardNumber++;
      double efficiencyPercentage = (double) card.boost / sortedByBoost.get(0).boost * 100;
      NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
      String formattedValue = numberFormat.format(card.value);
      String formattedBoost = numberFormat.format(card.boost);
      String[] nameParts = card.name.split(":");
      String storeName = nameParts[0];
      String cardName = nameParts.length > 1 ? nameParts[1].trim() : "";
      // System.out.printf("%d.Store: [%s], Card: {%s}, value: [%s], boost: [%s], efficiency: [%.2f%%]\n", cardNumber, storeName, cardName, formattedValue, formattedBoost, efficiencyPercentage);
      if (cardNumber >= 30) {
        break;
      }
    }
    int remainingBalance = (int) coinsBalance;
    System.out.println(YELLOW + "///////////////////////////////////Card to buy by Most Efficient Boost/////////////////////////////////////////////" + RESET);
    int cardNumberNew = 0;
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    for (Card card : sortedByBoost) {
      if (remainingBalance >= card.value) {
        remainingBalance -= card.value;
        totalBoost += card.boost;
        cardNumberNew++;
        double efficiencyPercentage = (double) card.boost / sortedByBoost.get(0).boost * 100;
        String formattedValue = numberFormat.format(card.value);
        String formattedBoost = numberFormat.format(card.boost);
        String[] nameParts = card.name.split(":");
        String storeName = nameParts[0];
        String cardName = nameParts.length > 1 ? nameParts[1].trim() : "";
        System.out.printf("%d.Store: [%s], Card: {%s}, value: [%s], boost: [%s], efficiency: [%.2f%%]\n", cardNumberNew, storeName, cardName, formattedValue, formattedBoost, efficiencyPercentage);
      }
    }
    String formattedRemainingBalance = numberFormat.format(remainingBalance);
    System.out.println("Not enough money to buy any new card. \nRemaining balance: " + formattedRemainingBalance);
    System.out.println(GREEN + "Total boost from the most efficient combination: [" + totalBoost + "]" + RESET);
    printCoinsBallance(coinsBalance, "Actual coins balance: ");
  }
  private static void printMostEfficientCombinationByBoost(List<Card> cards) {
    List<Card> sortedByEfficiency = new ArrayList<>(cards);
    sortedByEfficiency.sort(Comparator.comparingDouble(Card::efficiency).reversed());

    List<Card> mostEfficientCombination = new ArrayList<>();
    int remainingBalance = (int) coinsBalance;
    int totalBoost = 0;

    for (Card card : sortedByEfficiency) {
      if (remainingBalance >= card.value) {
        remainingBalance -= card.value;
        totalBoost += card.boost;
        mostEfficientCombination.add(card);
      }
    }
    mostEfficientCombination.sort((card1, card2) -> {
      double efficiency1 = (double) card1.boost / sortedByEfficiency.get(0).boost * 100;
      double efficiency2 = (double) card2.boost / sortedByEfficiency.get(0).boost * 100;
      return Double.compare(efficiency2, efficiency1); // Reverse order for descending sort
    });
    System.out.println(YELLOW + "///////////////////////////////////Card to buy by Most Efficient Combination/////////////////////////////////////////////" + RESET);
    int cardNumber = 0;
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    for (Card card : mostEfficientCombination) {
      cardNumber++;
      /*
      The efficiency can be greater than 100% if the boost of the current card is greater than the boost of the best-performing card.
      This can happen if the best-performing card was chosen based on the boost/value ratio, rather than just the boost.
      In this case, a card with a lower boost but significantly lower value can be considered more efficient.
       */
      double efficiencyPercentage = (double) card.boost / sortedByEfficiency.get(0).boost * 100;
      String formattedValue = numberFormat.format(card.value);
      String formattedBoost = numberFormat.format(card.boost);
      String[] nameParts = card.name.split(":");
      String storeName = nameParts[0];
      String cardName = nameParts.length > 1 ? nameParts[1].trim() : "";
      System.out.printf("%d.Store: [%s], Card: {%s}, value: [%s], boost: [%s], efficiency: [%.2f%%]\n", cardNumber, storeName, cardName, formattedValue, formattedBoost, efficiencyPercentage);
    }

    String formattedRemainingBalance = numberFormat.format(remainingBalance);
    System.out.println("Not enough money to buy any new card. \nRemaining balance: " + formattedRemainingBalance);
    System.out.println(RED + "Total boost from the most efficient combination: [" + totalBoost + "]" + RESET);
    printCoinsBallance(coinsBalance, "Actual coins balance: ");
  }
  private static void printMostEfficientCombinationByValue(List<Card> cards) {
    List<Card> sortedByEfficiency = new ArrayList<>(cards);
    sortedByEfficiency.sort(Comparator.comparingDouble(Card::efficiency).reversed());

    List<Card> mostEfficientCombination = new ArrayList<>();
    List<Card> cardsToRemove = new ArrayList<>();
    int remainingBalance = (int) coinsBalance;
    int totalBoost = 0;

    // Находим самую эффективную карту
    Card mostEfficientCard = sortedByEfficiency.get(0); // Первая карта после сортировки - самая эффективная
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

    mostEfficientCombination.sort((card1, card2) -> {
      double efficiency1 = (double) card1.boost / card1.value;
      double efficiency2 = (double) card2.boost / card2.value;
      return Double.compare(efficiency2, efficiency1); // Reverse order for descending sort
    });

    System.out.println(YELLOW + "///////////////////////////////////Card to buy by Most Efficient Combination by Value/////////////////////////////////////////////" + RESET);
    int cardNumber = 0;
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    for (Card card : mostEfficientCombination) {
      cardNumber++;
      double efficiencyPercentage = (card.efficiency() / maxEfficiency) * 100;
      String formattedValue = numberFormat.format(card.value);
      String formattedBoost = numberFormat.format(card.boost);
      String[] nameParts = card.name.split(":");
      String storeName = nameParts[0];
      String cardName = nameParts.length > 1 ? nameParts[1].trim() : "";
      System.out.printf("%d.Store: [%s], Card: {%s}, value: [%s], boost: [%s], efficiency: [%.2f%%]\n", cardNumber, storeName, cardName, formattedValue, formattedBoost, efficiencyPercentage);
    }

    String formattedRemainingBalance = numberFormat.format(remainingBalance);
    System.out.println("Not enough money to buy any new card. \nRemaining balance: " + formattedRemainingBalance);
    System.out.println(RED + "Total boost from the most efficient combination: [" + totalBoost + "]" + RESET);
    printCoinsBallance(coinsBalance, "Actual coins balance: ");
  }
}