package entity;

public enum Office {

    LISBOA("Lisboa"),
    COIMBRA("Coimbra"),
    PORTO("Porto"),
    TOMAR("Tomar"),
    VISEU("Viseu"),
    VILAREAL("Vila Real");

    private final String city;

    private Office(String city) {
        this.city = city;
    }

}
