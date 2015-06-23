import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ClientProxyAuthentication {

    public static void main(String[] args) throws Exception {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("paygo.crawlera.com", 8010),
                new UsernamePasswordCredentials("<API KEY>", ""));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
        try {
            HttpHost target = new HttpHost("twitter.com", 443, "http");
            HttpHost proxy = new HttpHost("paygo.crawlera.com", 8010);

            AuthCache authCache = new BasicAuthCache();

            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            HttpClientContext ctx = HttpClientContext.create();
            ctx.setAuthCache(authCache);

            RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .build();
            HttpGet httpget = new HttpGet("/");
            httpget.addHeader("X-Crawlera-Use-HTTPS", "1");
            httpget.setConfig(config);

            System.out.println("Executing request " + httpget.getRequestLine() + 
                " to " + target + " via " + proxy);

            CloseableHttpResponse response = httpclient.execute(target, httpget, ctx);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println("----------------------------------------");
                System.out.println(EntityUtils.toString(response.getEntity()));
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}
