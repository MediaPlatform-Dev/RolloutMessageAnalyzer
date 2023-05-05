package yamsroun.analyzer.rollout.analyzer;

import yamsroun.analyzer.rollout.data.RolloutInfo;

import java.util.List;

public interface RolloutMessageAnalyzer {

    public void analyzeAllMessage();

    public List<RolloutInfo> getResult();
}
