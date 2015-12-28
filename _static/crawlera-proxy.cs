using System;
using System.IO;
using System.Net;

namespace ProxyRequest
{
    class MainClass
    {
        public static void Main (string[] args)
        {
            var myProxy = new WebProxy("http://proxy.crawlera.com:8010");
            myProxy.Credentials = new NetworkCredential("<API KEY>", "");

            HttpWebRequest request = (HttpWebRequest)WebRequest.Create("https://twitter.com");
            request.Proxy = myProxy;
            request.PreAuthenticate = true;

            WebResponse response = request.GetResponse();
            Console.WriteLine("Response Status: " 
                + ((HttpWebResponse)response).StatusDescription);
            Console.WriteLine("\nResponse Headers:\n" 
                + ((HttpWebResponse)response).Headers);

            Stream dataStream = response.GetResponseStream();
            var reader = new StreamReader(dataStream);
            string responseFromServer = reader.ReadToEnd();
            Console.WriteLine("Response Body:\n" + responseFromServer);
            reader.Close();

            response.Close();
        }
    }
}
