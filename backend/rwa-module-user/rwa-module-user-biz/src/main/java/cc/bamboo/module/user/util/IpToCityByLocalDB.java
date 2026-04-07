package cc.bamboo.module.user.util;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.InputStream;

/**
 * 本地通过 IP2Region 数据库获取城市信息（离线）
 * 启动时一次性将 xdb 数据加载到内存，后续查询零 IO 开销
 */
@Slf4j
public class IpToCityByLocalDB {

    private static final String DB_PATH = "ip2region_v4.xdb";

    /** 内存缓存的 xdb 数据（只加载一次） */
    private static final byte[] XDB_DATA;

    static {
        try (InputStream is = IpToCityByLocalDB.class.getClassLoader().getResourceAsStream(DB_PATH)) {
            if (is == null) {
                throw new RuntimeException("找不到IP数据库文件: " + DB_PATH);
            }
            XDB_DATA = IoUtil.readBytes(is);
            log.info("[IpToCityByLocalDB] 加载 ip2region 数据库成功，大小: {} bytes", XDB_DATA.length);
        } catch (Exception e) {
            throw new RuntimeException("加载 ip2region 数据库失败", e);
        }
    }

    /**
     * 本地解析 IP 到城市
     *
     * @param ip IP 地址
     * @return 格式：国家-省份-城市
     */
    public static String getCityByIpLocal(String ip) {
        try {
            // 基于内存数据创建 Searcher（线程安全，无 IO）
            Searcher searcher = Searcher.newWithBuffer(XDB_DATA);
            String result = searcher.search(ip);
            // 结果格式：国家|区域|省份|城市|ISP
            String[] parts = result.split("\\|");
            String country = parts.length > 0 ? parts[0] : "";
            String province = parts.length > 1 ? parts[1] : "";
            String city = parts.length > 2 ? parts[2] : "";
            return String.format("%s-%s-%s", country, province, city);
        } catch (Exception e) {
            log.error("[getCityByIpLocal] IP解析失败, ip={}", ip, e);
            return null;
        }
    }

    public static void main(String[] args) {
        String ip = "114.114.114.114";
        System.out.println("IP " + ip + " 对应的信息：" + getCityByIpLocal(ip));
    }
}
