package searchengine.repository;

import searchengine.model.Indecs;

import java.util.List;

public interface IndexRepositoryCustom {
    void insertIndexList(String siteName, List<Indecs> indices);
}
