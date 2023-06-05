package chartreux.applilabv2.Util;

import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import javafx.util.Pair;

import java.util.List;
import java.util.Objects;

public class Tool {
    public static <K, V>  boolean checkKeyExistsIng(List<Pair<K, V>> list, Ingredient key) {
        for (Pair<K, V> pair : list) {
            if (pair.getKey().equals(key)) {
                return true; // La clé existe déjà dans la liste
            }
        }
        return false; // La clé n'a pas été trouvée dans la liste
    }

    public static <K, V>  boolean checkKeyExistsLabo(List<Pair<K, V>> list, Laboratoire key) {
        for (Pair<K, V> pair : list) {
            if (pair.getKey().equals(key)) {
                return true; // La clé existe déjà dans la liste
            }
        }
        return false; // La clé n'a pas été trouvée dans la liste
    }

    public static <K, V> boolean checkValueExists(List<Pair<K, V>> list, Role role) {
        for (Pair<K, V> pair : list) {
            if (pair.getValue().equals(role)) {
                return true; // La valeur existe dans la liste
            }
        }
        return false; // La valeur n'a pas été trouvée dans la liste
    }
}
