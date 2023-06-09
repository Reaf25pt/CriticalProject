package ENUM;

public enum SkillType {
    KNOWLEDGE("Conhecimento"),
    SOFTWARE("Software"),
    HARDWARE("Hardware"),
    TOOL("Ferramenta");


    private final String type;

    private SkillType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
