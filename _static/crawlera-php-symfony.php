<?php

namespace AppBundle\Controller;

use GuzzleHttp\Client;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Symfony\Component\HttpFoundation\Response;

class CrawleraController extends Controller
{
    /**
     * @Route("/crawlera", name="crawlera")
     */
    
    public function crawlAction()
    {
        $url = 'https://twitter.com';
        $client = new Client(['base_uri' => $url]);
        $crawler = $client->get($url, ['proxy' => 'http://<API KEY>:@proxy.crawlera.com:8010'])->getBody();

        return new Response(
            '<html><body> '.$crawler.' </body></html>'
        );
    }
}
