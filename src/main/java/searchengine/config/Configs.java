package searchengine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "config")
@Data
public class Configs {
    private Integer maxPagesInSite;
    private List<CongifSite> sites;

    private static Boolean inited = false;

    private static Configs configs;

    public Configs() {
        configs = this;
    }

    public static Configs getConfigs() {
        synchronized (Configs.class) {
            if (!inited) {
                init();
                inited = true;
            }
        }
        return configs;
    }

    public static void init() {
        for (CongifSite sun : configs.sites) {
            URL url;
            try {
                url = new URL(sun.getUrl());
            } catch (MalformedURLException e) {
                continue;
            }
            sun.setUrl(url.getProtocol() + "://" + url.getHost());
        }
    }

    public static List<String> getAllSiteUrls() {
        List<String> siteUrls = new ArrayList<>();
        List<CongifSite> urlNames = Configs.getConfigs().getSites();
        urlNames.forEach(sun -> siteUrls.add(sun.getUrl()));
        return siteUrls;
    }


    @Data
    public static class CongifSite {
        private String url;
        private String name;
        private int pause;

        public static String getNameByUrl(String url) {
            CongifSite congifSite = Configs.configs.sites.stream()
                    .filter(siteUrlName -> siteUrlName.getUrl().equals(url))
                    .findFirst().orElse(null);
            return congifSite != null ? congifSite.getName() : "";
        }

        public static int getPauseBySiteName(String name) {
            CongifSite congifSite = Configs.configs.sites.stream()
                    .filter(siteUrlName -> siteUrlName.getName().equals(name))
                    .findFirst().orElse(null);
            return congifSite != null ? congifSite.getPause() : 0;
        }
    }

}
