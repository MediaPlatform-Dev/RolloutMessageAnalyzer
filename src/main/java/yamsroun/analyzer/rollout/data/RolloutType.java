package yamsroun.analyzer.rollout.data;

public enum RolloutType {
    DEPLOYMENT, ROLLBACK, RE_DEPLOYMENT;

    public static RolloutType getDefalut() {
        return DEPLOYMENT;
    }

    public boolean isDefault() {
        return this == getDefalut();
    }
}
