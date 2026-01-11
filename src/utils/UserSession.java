
package utils;

import model.Technicien;

public class UserSession {

    private static Technicien technicienConnecte;

    public static void setTechnicien(Technicien technicien) {
        technicienConnecte = technicien;
    }

    public static Technicien getTechnicien() {
        return technicienConnecte;
    }

    public static void clear() {
        technicienConnecte = null;
    }
}
