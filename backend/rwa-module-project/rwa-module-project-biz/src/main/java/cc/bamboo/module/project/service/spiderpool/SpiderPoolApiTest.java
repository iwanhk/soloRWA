package cc.bamboo.module.project.service.spiderpool;

import cc.bamboo.module.project.util.RSACoder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * SpiderPool API 独立测试类
 * 可直接运行main方法测试API调用
 *
 * @author Swolf
 */
public class SpiderPoolApiTest {

    // ======== 请修改以下配置 ========

    private static final String COIN = "btc";
    private static final String BASE_URL = "https://test.spiderpool.com";

    private static final String ACCESS_KEY = "15D073DCD3674E2392BFCD643A215008";

    private static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAItzxcIMNZKHEvjyboS5mSWeKbs7oE+EymbU3epR5GrgR5dZigoVOEHYuI4hZqo2gbqkymUnXGaGBxDyNisdHb9yGew+fU7Kzs1DwT27SM7SjayV2o/Xx189s0uxscWZAeZjbz1yhGxeV06bScxyE4u3x4AtfCrqyd/ub5ssc2bvAgMBAAECgYBdquHGOVUymq/Pr2c0l0Wa0uXM8Xv4bYVWFN0KKv92Gjd8HZRuuVoUQHGWZjEDW6E/sLiRwSJ7asMMSuS/iMsVclniuzEZXbltI4Atj2YOvP2N2pk5OgzxTZLwp32ZndY6wSMXebkGm/ZWNwumNkxheXsD8QfiLmROoAWOmhsHoQJBAMhAD3wyU8khQUpdkiPVTmafbUWDxdhuN9APWBd5O+AGnjsH25IONsY6mFnzSqXjJK9M+fBSKJ0utOzSIyb5/MUCQQCyRptLAWwdLklAv/3ZWuawBGxBYwalZ3bIu846sX6nL9nUQP8H+zJ4p/44ZPgBKkktTLx2dbVok2R8NKJiO/gjAkBABPruYciXjKIq/C80dYVTGbT9fFtMDjCZu84V8xRJUtRTWve/gfvQ8/qxBy7eSff4c0uUoS+K0NvWuneyhklhAkAymr48FURGKDxc5+K7zwTKlAbSrLRXQT5DjqPknzuPY5LWoDiEYHacLzuHcInBcupgtHaOvazn6WbNpvctIU63AkAyVRbFkHiGYOcEI1SezadTO2uxiBp0/wXhITUEI5wDMPo51JaAI7fllPIbBQabJM7AO05pnR1S8b0K5X8pYGNZ";

    private static final String SUBACCOUNT = "aaa123";

 /*   private static final String BASE_URL = "https://api.spiderpool.com";
    private static final String ACCESS_KEY = "1601216D1F1840A78A6D1BDC5D55226D";
    private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKLvZ1bo3/K2MucnsC7SAmEkSelUaW4bKozHx2xtFyy8WhCXkI+Skz/JqnFJgQ1ntQ+3IgL8IwsC2OgVdLFt6FhQz4XcZOCqgPsvPo86qQfhSI/cUKkc603KwKO7SMLfhbWsS9PzXI1V79/36pKsSddF/5Fy5ava6NXZF6ceVE0/AgMBAAECgYB6pukNvWPH22vaIkA4HpL3+U9CO+7Zmc2gCEuQzVYisO7DirShv17lJ7QMb56cLrNXzy3lPVRL16rcmjorzmaV0Er0zDkXJsjRCF0Q2dGeBn/Dp0dB8XfQfuejti7Wb9zdihowwjkN73IcW2LJwpwUZligxGkvpTnuhMvX6VIE8QJBAN2qeyk4rTE65aF6zORmtz49j4HEr27mYFb+90wzbjRYnjzLlEhNbBKZ+DL0Hcsougq3nY1dG7c7AGd96Zlx+zUCQQC8LB/diYaCun9yetMvxivbFig+AyRsiKihXkHZr4xS0cbKp4lHZdmEAzPC7tqO4fUWGdN4OD7dgOsG1nWvbMEjAkEApQoYlxFuyfe7HybKRMhBaOL25TynDeATHNLLF343cFwGvOcqaUbTz2m6BV4CjM+u7OnXMXiAeUVBwMxbz/awfQJAZUEVEH+NKtEx/ScMdrubI7PjiaLlOaW7n8F5i6qDxpjs8GWAYDP0/K1AcBdY8eOynubcvOb/JO/XCAHkp7XMowJBALnDYhyAmVEnZxoeoy++dlnuaUrfTsWA/qkIBeShHawBilavqc16zgkQR+hsUH+/P2idkVpsNUdGzlDMfTXR97k=";
    private static final String SUBACCOUNT = "swolf";*/

    // ================================

    public static void main(String[] args) {
        System.out.println("========== SpiderPool API 测试 ==========");
        System.out.println("API URL: " + BASE_URL);
        System.out.println("AccessKey: " + ACCESS_KEY);
        System.out.println("币种: " + COIN);
        System.out.println("子账号: " + SUBACCOUNT);
        System.out.println("==========================================\n");

        try {
            // 查询昨天的收益 (使用UTC+0时间戳)
            long yesterdayTimestamp = 1716854400;
            System.out.println("查询时间戳 (秒): " + yesterdayTimestamp);
            System.out.println();

            // 调用API
            testGetDayProfitDetailInfo(COIN, SUBACCOUNT, yesterdayTimestamp);

        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取子账号日收益
     */
    private static void testGetDayProfitDetailInfo(String coin, String subaccount, long timeStamp) throws Exception {
        System.out.println(">>> 调用 getDayProfitDetailInfo API");

        // 1. 构建请求数据JSON
        JSONObject dataJsonObj = new JSONObject();
        dataJsonObj.set("coin", coin);
        dataJsonObj.set("subaccount", subaccount);
        /*dataJsonObj.set("startTimestamp", 1769040000);
        dataJsonObj.set("startTimestamp", 1769212800);*/
        dataJsonObj.set("timeStamp", 1769126400);
        String dataJson = dataJsonObj.toString();
        System.out.println("DataJson: " + dataJson);

        // 2. 获取当前时间戳
        long timestamp = System.currentTimeMillis();
        System.out.println("请求时间戳 (毫秒): " + timestamp);

        // 3. 生成签名
        //String signData = dataJson + ACCESS_KEY + timestamp;
        String signData = dataJson + "|" + timestamp;
        System.out.println("签名原文: " + signData);

        String sign = RSACoder.sign(signData.getBytes("UTF-8"), PRIVATE_KEY);
        System.out.println("签名结果: " + sign);

        // 4. 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("dataJson", dataJson);
        requestBody.set("accessKey", ACCESS_KEY);
        requestBody.set("timestamp", timestamp);
        requestBody.set("sign", sign);

        System.out.println("\n请求体:");
        System.out.println(JSONUtil.toJsonPrettyStr(requestBody));

        // 5. 发送POST请求 (通过代理)
        String url = BASE_URL + "/v2/sp/subaccount/getDayProfitDetailInfo";
        System.out.println("\n请求URL: " + url);
        System.out.println("使用代理: 127.0.0.1:7890");
        System.out.println("发送请求中...\n");

        HttpResponse response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(30000)
                .setHttpProxy("127.0.0.1", 7890) // 设置代理
                .execute();

        // 6. 输出响应
        System.out.println("HTTP状态码: " + response.getStatus());
        System.out.println("响应体:");

        String responseBody = response.body();
        try {
            // 尝试格式化JSON输出
            JSONObject respJson = JSONUtil.parseObj(responseBody);
            System.out.println(JSONUtil.toJsonPrettyStr(respJson));

            // 7. 解析结果
            int code = respJson.getInt("code", -1);
            String msg = respJson.getStr("msg", "");

            System.out.println("\n========== 解析结果 ==========");
            System.out.println("Code: " + code);
            System.out.println("Msg: " + msg);

            if (code == 200 || "SUCCESS".equalsIgnoreCase(msg)) {
                String dataResult =  respJson.getStr("data", "");
                System.out.println("dataResult: " + dataResult);
                JSONObject data = respJson.getJSONObject("data");
                if (data != null) {

                    System.out.println("\n日收益信息:");
                    System.out.println("  币种: " + data.getLong("subaccount"));
                    System.out.println("  子账户: " + data.getLong("coin"));
                    System.out.println("  钱包地址: " + data.getLong("withdrawAddress"));
                    System.out.println("  日期 (时间戳): " + data.getLong("day"));
                    System.out.println("  币种: " + data.getStr("coin"));
                    System.out.println("  子账号: " + data.getStr("userName"));
                    System.out.println("  日均算力: " + data.getBigDecimal("avgShareAccept") + " H/s");
                    System.out.println("  总收益: " + data.getBigDecimal("dayProfit") + " " + coin.toUpperCase());
                    System.out.println("  PPS收益: " + data.getBigDecimal("ppsDayProfit") + " " + coin.toUpperCase());
                    System.out.println("  手续费收益: " + data.getBigDecimal("pplnsDayProfit") + " " + coin.toUpperCase());
                    System.out.println("  难度: " + data.getStr("difficult"));
                } else {
                    System.out.println("响应data为空");
                }
            } else {
                System.err.println("API请求失败!");
            }

        } catch (Exception e) {
            // 非JSON响应
            System.out.println("123");
            System.out.println(responseBody);
        }

        System.out.println("==============================\n");
    }

}
