package dto;

public class ActiveProjectToken {

    private boolean hasActiveProject;

    private int activeProjectId;

    public ActiveProjectToken() {
    }

    public boolean isHasActiveProject() {
        return hasActiveProject;
    }

    public void setHasActiveProject(boolean hasActiveProject) {
        this.hasActiveProject = hasActiveProject;
    }

    public int getActiveProjectId() {
        return activeProjectId;
    }

    public void setActiveProjectId(int activeProjectId) {
        this.activeProjectId = activeProjectId;
    }
}
