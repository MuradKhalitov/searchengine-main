package searchengine.repository;

import lombok.extern.slf4j.Slf4j;
import searchengine.model.Indecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.List;

@Slf4j
@Repository
@Transactional
public class IndexRepositoryImpl implements IndexRepositoryCustom {
    @Autowired
    EntityManager entityManager;
    public static final String TABS = "\t\t";

    @Override
    public void insertIndexList(String siteName, List<Indecs> indices) {
        int ONE_THOUSAND = 1000;
        int SAVING_PORTION = 100 * ONE_THOUSAND;
        StringBuilder insertBuilder = new StringBuilder();
        int currIndex = 0;
        while (currIndex < indices.size()) {
            currIndex = buildInserts(indices, insertBuilder, currIndex);

            String sql = "insert into search_engine.index(page_id, lemma_id, search_engine.index.rank)" +
                    " values " + insertBuilder;
            insertBuilder.setLength(0);
            Query query = entityManager.
                    createNativeQuery(sql);
            query.executeUpdate();
            if (currIndex % SAVING_PORTION == 0) {
                log.info(TABS + "Сайт \"" + siteName + "\": " +
                        "сохранено " + currIndex / ONE_THOUSAND + " тыс. индексов");
            } else {
                log.info(TABS + "Сайт \"" + siteName + "\": " +
                        "сохранено " + currIndex + " индексов");
            }
        }
    }

    private int buildInserts(List<Indecs> indices, StringBuilder insertBuilder, int currIndex) {
        for (int i = 0; i++ < 100_000 && currIndex < indices.size(); currIndex++) {
            Indecs indecs = indices.get(currIndex);
            insertBuilder.append(insertBuilder.length() == 0 ? "" : ",")
                    .append("(").append(indecs.getPage().getId())
                    .append(",").append(indecs.getLemma().getId())
                    .append(",").append(indecs.getRank()).append(")");
        }
        return currIndex;
    }
}
