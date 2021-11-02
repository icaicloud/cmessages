<?php

class CMessagesAPI {
    private $appId;
    private $appKey;
    private $debug = true; // 调成改值用来测试
    const BASE_URI = 'https://api.cmessages.com';

    public function __construct($appId, $appKey) {
        $this->appId = $appId;
        $this->appKey = $appKey;
    }


   /**
    * 获取短信余额
    * @returns
    */
    public function getBalance() {
        return $this->post('/sms/get_balance');
    }

   /**
    * 发送验证码,查询接口可使用query接口查询
    *
    * @param string mobile 手机号(手机号可携带国标代码,比如+86视为中国)
    * @param string content 短信验证码内容,比如: 您的验证码为1234
    *
    * @see query
    */
    public function sendVerify($mobile, $content) {
        return $this->post('/sms/send_verify', ['mobile' => $mobile, 'content' => $content]);
    }

   /**
    * 发送营销短信
    *
    * @param array|string mobiles 手机号列表,单个手机号时可给字符串(手机号可携带国标代码,比如+86视为中国)
    * @param string       content 需要发送的营销内容
    *
    * @see query
    */
    public function sendMessage($mobiles, $content) {
        return $this->post('/sms/send_message', [
            'mobile' => is_array($mobiles) ? $mobiles : [mobiles],
            'content' => $content
        ]);
    }

   /**
    * 短信查询
    * @param array ids 短信ID列表, 此ID来自sendVerify和sendMessage
    *
    * @see sendMessage
    * @see sendVerify
    *
    */
    public function query($ids) {
        return $this->post('/sms/query', ['id_list' => $ids]);
    }

    /**
     * GET wrapper for CMessagesAPI.
     *
     * @param string $url        The url to get.
     * @param array  $parameters The parameters.
     *
     * @return array             The response from server.
     */
    public function get($url, array $parameters = array())
    {
        $response = $this->authRequest($url,'GET',$parameters);
        return json_decode($response, true);
    }

    /**
     * POST wrapper for CMessagesAPI.
     *
     * @param string $url        The url to post.
     * @param array  $parameters The parameters.
     *
     * @return array             The response from server.
     */
    public function post($url, array $parameters = array())
    {
        $response = $this->authRequest($url,'POST',$parameters);
        return json_decode($response,true);
    }

    /**
     * Prepare parser the request.
     *
     * @param string $url        The url to request.
     * @param string $method     Such as post,get,delete,put and so on.
     * @param array  $parameters The parameters.
     *
     * @return mixed    $response       The responses.
     * @throws JMOpenApiException If method doese not support.
     */
    public function authRequest($url, $method = 'GET', array $parameters = array())
    {
        if (strrpos($url, 'http://') !== 0 && strrpos($url, 'https://') !== 0 ) {
            $url = self::BASE_URI.$url;
        }
        $ts = (new \DateTime('now', new \DateTimeZone('Asia/Shanghai')))->format('YmdHis');
        $parameters['app_id'] = $this->appId;
        $parameters['datetime'] = $ts;
        $parameters['sign'] = md5($this->appId . $ts . $this->appKey);

        switch (strtoupper($method)) {
            case 'GET':
                $url = $url.'?'.http_build_query($parameters);
                return $this->request($url,'GET');
                break;
            case 'POST':
                $body = json_encode($parameters);
                return $this->request($url,'POST', $body, ['Content-Type: application/json']);
                break;
            default:
                throw new JMOpenApiException('No support method');
                // return $this->request($url,$method)
                break;
        }
    }

    /**
     * Make the HTTP request.
     *
     * @param string $url      The url to request.
     * @param string $method   The method name.
     * @param string $postdata The parameters.
     * @param array  $headers  The headers.
     *
     * @return mixed    $response   The responses.
     * @throws JMOpenApiException    If method does not support.
     */
    private function request($url, $method, $postdata = null, array $headers = array())
    {
        // curl settings for http
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_0);
        curl_setopt($ch, CURLOPT_USERAGENT, "CMessagesSDK-php");
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 60);
        curl_setopt($ch, CURLOPT_TIMEOUT, 60);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_ENCODING, 'utf8');

        // parse for those wrapper
        switch (strtoupper($method)) {
            case 'GET':
                break;
            case 'POST':
                if (!empty($postdata)) {
                    curl_setopt($ch, CURLOPT_POST, true);
                    curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
                }
                break;
            default:
                throw new JMOpenApiException('No support method');
                break;
        }

        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        // curl_setopt($ch, CURLINFO_HEADER_OUT, true);
        $response = curl_exec($ch);
        if ($this->debug) {
            echo "post data:\r\n";
            print_r($postdata);
            echo "reqsonse:\r\n";
            print_r($response);
        }
        curl_close($ch);
        return $response;
    }
}



///// 以下是测试代码
// See https://stackoverflow.com/questions/2413991/php-equivalent-of-pythons-name-main
if ($argv && $argv[0] && realpath($argv[0] === __FILE__ )) {
    $api = new CMessagesAPI("dp76QL927CwxV8483e", "A4dQ42b8rykuxn3ah38qMEB9ft26Lm7D");
    print_r($api->getBalance());
}
