package yamsroun.analyzer.rollout.data;

public enum RolloutType {
    DEPLOYMENT, ROLLBACK, RE_DEPLOYMENT;

    public boolean isDefault() {
        return this == DEPLOYMENT;
    }
}
