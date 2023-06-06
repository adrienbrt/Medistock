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

    public static boolean checkKeyExistsLabo(List<Pair<Laboratoire, Role>> list, Laboratoire key) {
        for (Pair<Laboratoire, Role> pair : list) {
            if (pair.getKey().equals(key)) {
                return true; // La clé existe déjà dans la liste
            }
        }
        return false; // La clé n'a pas été trouvée dans la liste
    }



    public static Role checkRole(List<Pair<Laboratoire, Role>> list, Laboratoire laboratoire) {
        for (Pair<Laboratoire, Role> pair : list) {
            if (pair.getKey().equals(laboratoire)) {
                return pair.getValue(); // La valeur existe dans la liste
            }
        }
        return null; // La valeur n'a pas été trouvée dans la liste
    }
}
