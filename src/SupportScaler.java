import java.util.Set;

public interface SupportScaler {
        double scale(Set<Integer> itemset, double rawSupport);
    } // vi har lavet et interface der kan normalisere support.
// Vi så dette som en god udvidelse i vores kode, fordi vores data var inkonsistent,
// og vi ønsket at gøre op for at stjernetegn var underrepræsenteret.
// Dette flyttet vi ud af AprioriAggreval1 så den ikke indeholdt case-specifik kode.