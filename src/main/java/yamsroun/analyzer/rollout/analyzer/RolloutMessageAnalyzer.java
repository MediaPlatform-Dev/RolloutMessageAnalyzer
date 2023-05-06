package yamsroun.analyzer.rollout.analyzer;

import yamsroun.analyzer.rollout.data.RolloutInfo;

import java.util.List;

public interface RolloutMessageAnalyzer {

    void analyzeAllMessage();

    List<RolloutInfo> getResult();
}
