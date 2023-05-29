package ENUM;

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

    // TODO guardar na DB valor numérico, nome da cidade ou outra forma ? trabalhar no frontend ou backend ?

}
