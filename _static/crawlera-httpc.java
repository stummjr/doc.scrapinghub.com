import java.io.File;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class ClientProxyAuthentication {

    public static void main(String[] args) throws Exception {
        
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(new File("/path/to/jre/lib/security/cacerts"),
                                   "changeit".toCharArray(),
                                   new TrustSelfSignedStrategy())
                .build();

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[] {"TLSv1"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("proxy.crawlera.com", 8010),
                new UsernamePasswordCredentials("<API KEY>", ""));
        
        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setSSLSocketFactory(sslsf)
                .build())
        {
            HttpHost target = new HttpHost("twitter.com", 443, "https");
            HttpHost proxy = new HttpHost("proxy.crawlera.com", 8010);

            AuthCache authCache = new BasicAuthCache();

            BasicScheme basicAuth = new BasicScheme();
            basicAuth.processChallenge(
                    new BasicHeader(HttpHeaders.PROXY_AUTHENTICATE,
                                    "Basic realm=\"Crawlera\""));
            authCache.put(proxy, basicAuth);

            HttpClientContext ctx = HttpClientContext.create();
            ctx.setAuthCache(authCache);

            RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .build();
            
            HttpGet httpget = new HttpGet("/");
            httpget.setConfig(config);

            System.out.println("Executing request " + httpget.getRequestLine() +
                " to " + target + " via " + proxy);

            try (CloseableHttpResponse response = httpclient.execute(
                target, httpget, ctx))
            {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println("----------------------------------------");
                System.out.println(EntityUtils.toString(response.getEntity()));
                EntityUtils.consume(response.getEntity());
            }
        }
    }
}
