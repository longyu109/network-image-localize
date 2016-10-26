package com.ly.localize.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by bert on 16-5-20.
 */
public class UrlUtil {
    private static final Logger log = LoggerFactory.getLogger(UrlUtil.class);

    private static String hostsName = "/urls.properties";
    private static String hostsName2 = "/properties/urls.properties";
    private static Properties urls;

    private static String center_domian, passport_domain, club_domain,
            feed_domain, app_domain, home_domain, msg_domain, square_domain,
            pay_domain, payms_domain, world_domain, nali_domain, search_domain,
            karaoke_domain, fiveone_domain, bar51_domain,auth51_domain,cms_domain,
            app_kaixin_domain,
            app_renren_domain,app_qidian_domain
            ;

    static {
        urls = new Properties();
        try {
            InputStream in = UrlUtil.class.getResourceAsStream(hostsName);
            if (in != null)
                urls.load(in);
        } catch (Exception e) {
            log
                    .error(
                            "Do not find a "
                                    + hostsName
                                    + " file on classpath for loading static resources like images",
                            e);
        }
        try {
            InputStream in = UrlUtil.class.getResourceAsStream(hostsName2);
            if (in != null)
                urls.load(in);
        } catch (Exception e) {
            log
                    .error(
                            "Do not find a "
                                    + hostsName2
                                    + " file on classpath for loading static resources like images",
                            e);
        }
        center_domian = urls.getProperty("server.center.domain");
        passport_domain = urls.getProperty("server.passport.domain");
        club_domain = urls.getProperty("server.club.domain");
        feed_domain = urls.getProperty("server.feed.domain");
        app_domain = urls.getProperty("server.app.domain");
        home_domain = urls.getProperty("server.home.domain");
        msg_domain = urls.getProperty("server.msg.domain");
        square_domain = urls.getProperty("server.square.domain");
        world_domain = urls.getProperty("server.world.domain");
        nali_domain = urls.getProperty("server.9nali.domain");
        pay_domain = urls.getProperty("server.pay.domain");
        payms_domain = urls.getProperty("server.payms.domain");
        search_domain = urls.getProperty("server.search.domain");
        karaoke_domain = urls.getProperty("server.karaoke.domain");
        fiveone_domain = urls.getProperty("server.fiveone.domain");
        bar51_domain = urls.getProperty("server.bar51.domain");
        auth51_domain = urls.getProperty("server.auth51.domain");
        cms_domain = urls.getProperty("server.cms.domain");
        app_kaixin_domain = urls.getProperty("server.appkaixin.domain");
        app_renren_domain = urls.getProperty("server.apprenren.domain");
        app_qidian_domain = urls.getProperty("server.appqidian.domain");
    }

    /**
     * @return center domain
     */
    public static String center() {
        return center_domian;
    }

    /**
     * @return passport domain
     */
    public static String passport() {
        return passport_domain;
    }

    /**
     * @return zone domain
     */
    public static String club() {
        return club_domain;
    }

    /**
     * @return feed domain
     */
    public static String feed() {
        return feed_domain;
    }

    /**
     * @return app domain
     */
    public static String app() {
        return app_domain;
    }

    /**
     * @return home domain
     */
    public static String home() {
        return home_domain;
    }

    /**
     * @return msg domain
     */
    public static String msg() {
        return msg_domain;
    }

    /**
     * @return square domain
     */
    public static String square() {
        return square_domain;
    }

    /**
     * @return pay domain
     */
    public static String pay() {
        return pay_domain;
    }

    public static String nali() {
        return nali_domain;
    }

    public static String search() {
        return search_domain;
    }

    public static String karaoke() {
        return karaoke_domain;
    }

    public static String fiveone() {
        return fiveone_domain;
    }

    public static String bar51() {
        return bar51_domain;
    }

    public static String auth51() {
        return auth51_domain;
    }

    /**
     * @return square domain
     */
    public static String payms() {
        return payms_domain;
    }

    public static String world() {
        return world_domain;
    }

    public static String cms() {
        return cms_domain;
    }
    public static String appKaixin() {
        return app_kaixin_domain;
    }
    public static String appRenren() {
        return app_renren_domain;
    }
    public static String appQidian() {
        return app_qidian_domain;
    }

    public static String get(String key) {
        return urls.getProperty(key, key);
    }


}
