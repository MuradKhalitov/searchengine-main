package searchengine.repository;

import searchengine.model.Indecs;
import searchengine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Indecs, Integer>, IndexRepositoryCustom {
    @Query(value = "select i from Indecs i join Page p " +
            "on p.site = :site and i.page = p")
    List<Indecs> findAllBySite(@Param("site") Site site);
    @Query("select i from Indecs i, Lemma l, Page p, Site s " +
            "where s = :site and i.page = p and p.site = :site " +
            "and i.lemma = l and l.lemma = :textLemma")
    List<Indecs> findAllByTextLemmaAndSite(
            @Param("textLemma") String lemma, @Param("site") Site site);
    @Query(value = "select i from Indecs i, Lemma l, Page p, Site s " +
            "where s.type = 'INDEXED' and l.lemma = :textLemma " +
            "and l.site = s and p.site = s " +
            "and i.lemma = l and i.page = p")
    List<Indecs> findAllByTextLemma(@Param("textLemma") String lemma);
}
