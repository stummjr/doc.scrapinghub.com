<?php

$ch = curl_init();

$url = 'https://twitter.com/';
$proxy = 'proxy.crawlera.com:8010';
$proxy_auth = '<API KEY>:';

curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_PROXY, $proxy);
curl_setopt($ch, CURLOPT_PROXYUSERPWD, $proxy_auth);
curl_setopt($ch, CURLOPT_HEADER, 1);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 30);
curl_setopt($ch, CURLOPT_TIMEOUT, 30);

$scraped_page = curl_exec($ch);
curl_close($ch);
echo $scraped_page;

?>
