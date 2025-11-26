import java.util.Comparator;
import java.util.List;

public class Sorter {
    private static Comparator<Asset> currentSorter = new Asset.SymbolComparator(true);

    // Private constructor - static utility class
    private Sorter() {}

    public static void setSorter(Comparator<Asset> sorter) {
        currentSorter = sorter;
    }

    public static Comparator<Asset> getCurrentSorter() {
        return currentSorter;
    }

    public static List<Asset> sort(List<Asset> assets) {
        assets.sort(currentSorter);
        return assets;
    }

    public static void resetToDefault() {
        currentSorter = new Asset.SymbolComparator(true);
    }

    public static String getCurrentSortDescription() {
        if (currentSorter instanceof Asset.SymbolComparator) {
            return "Symbol (A-Z)";
        } else if (currentSorter instanceof Asset.TotalValueComparator) {
            Asset.TotalValueComparator comp = (Asset.TotalValueComparator) currentSorter;
            return comp.ascending ? "Total Value (Low to High)" : "Total Value (High to Low)";
        } else if (currentSorter instanceof Asset.ProfitAmountComparator) {
            Asset.ProfitAmountComparator comp = (Asset.ProfitAmountComparator) currentSorter;
            return comp.ascending ? "Profit Amount (Low to High)" : "Profit Amount (High to Low)";
        } else if (currentSorter instanceof Asset.ProfitPercentComparator) {
            Asset.ProfitPercentComparator comp = (Asset.ProfitPercentComparator) currentSorter;
            return comp.ascending ? "Profit % (Low to High)" : "Profit % (High to Low)";
        } else if (currentSorter instanceof Asset.BuyPriceComparator) {
            Asset.BuyPriceComparator comp = (Asset.BuyPriceComparator) currentSorter;
            return comp.ascending ? "Buy Price (Low to High)" : "Buy Price (High to Low)";
        } else if (currentSorter instanceof Asset.AmountComparator) {
            Asset.AmountComparator comp = (Asset.AmountComparator) currentSorter;
            return comp.ascending ? "Amount (Low to High)" : "Amount (High to Low)";
        }
        return "Symbol (A-Z)";
    }
}