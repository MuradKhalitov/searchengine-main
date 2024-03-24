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
    private List<ConfigSite> sites;

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
        for (ConfigSite sun : configs.sites) {
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
        List<ConfigSite> urlNames = Configs.getConfigs().getSites();
        urlNames.forEach(sun -> siteUrls.add(sun.getUrl()));
        return siteUrls;
    }


    @Data
    public static class ConfigSite {
        private String url;
        private String name;
        private int pause;

        public static String getNameByUrl(String url) {
            ConfigSite configSite = Configs.configs.sites.stream()
                    .filter(siteUrlName -> siteUrlName.getUrl().equals(url))
                    .findFirst().orElse(null);
            return configSite != null ? configSite.getName() : "";
        }

        public static int getPauseBySiteName(String name) {
            ConfigSite configSite = Configs.configs.sites.stream()
                    .filter(siteUrlName -> siteUrlName.getName().equals(name))
                    .findFirst().orElse(null);
            return configSite != null ? configSite.getPause() : 0;
        }
    }

}
