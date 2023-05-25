package chartreux.applilabv2.Entity;

public class Role {
    private String id;
    private String libelle;
    private int num;

    public Role(String id, String libelle, int num) {
        this.id = id;
        this.libelle = libelle;
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
