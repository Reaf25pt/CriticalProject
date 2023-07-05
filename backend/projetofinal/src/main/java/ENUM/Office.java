package ENUM;

/**
 * Represents the possible values attribute Office can have, for both Project and User entities
 */
public enum Office {



    LISBOA("Lisboa"), // 0
    COIMBRA("Coimbra"),  // 1
    PORTO("Porto"), // 2
    TOMAR("Tomar"), // 3
    VISEU("Viseu"), // 4
    VILAREAL("Vila Real"); // 5

    private final String city;

    private Office(String city) {
        this.city = city;
    }



    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "Office{" +
                "city='" + city + '\'' +
                '}';
    }
}
