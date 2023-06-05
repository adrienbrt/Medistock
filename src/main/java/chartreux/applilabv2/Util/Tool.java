package chartreux.applilabv2.Util;

import chartreux.applilabv2.Entity.Ingredient;
import javafx.util.Pair;

import java.util.List;

public class Tool {
    public static boolean checkKeyExists(List<Pair<Ingredient, Integer>> list, Ingredient key) {
        for (Pair<Ingredient, Integer> pair : list) {
            if (pair.getKey().equals(key)) {
                return true; // La clé existe déjà dans la liste
            }
        }
        return false; // La clé n'a pas été trouvée dans la liste
    }
}
