#coding=utf-8
import json
import hashlib
try:
    from urllib.request import urlopen, Request
except ImportError:
    # python 2.7
    from urllib2 import urlopen, Request

from datetime import datetime

class CMessagesAPI:
    def __init__(self, appId, appKey):
        self.appId = appId
        self.appKey = appKey
        self.baseUri = 'https://api.cmessages.com'
    
    def get_balance(self):
        """获取短信余额"""
        return self.post('/sms/get_balance')

    def query(self, ids):
        """查询短信状态"""
        return self.post('/sms/query', {'id_list': ids})

    def send_verify(self, mobile, content):
        """发送短信短信验证码"""
        return self.post('/sms/send_verify', {'mobile': mobile, 'content': content})

    def send_message(self, mobiles, content):
        """发送营销短信"""
        return self.post('/sms/send_message', {'mobile': mobiles if isinstance(mobiles, (list, tuple)) else [mobiles], 'content': content})

    def prepare_payload(self, payload = None):
        """CMessages签名计算方式"""
        payload = payload or {}
        payload['app_id'] = self.appId
        ts = datetime.now().strftime('%Y%m%d%H%M%S')
        payload['datetime'] = ts
        payload['sign'] = hashlib.md5('{0}{1}{2}'.format(self.appId, ts, self.appKey).encode()).hexdigest()
        # print('{0}{1}{2}'.format(self.appId, ts, self.appKey))
        return payload

    def post(self, endPoint, payload = None):
        req = Request('{0}{1}'.format(self.baseUri, endPoint),
            data=json.dumps(self.prepare_payload(payload)).encode(),
            headers={'Content-Type': 'application/json', 'User-Agent': 'CMessagesSDK-python'})
        resp = urlopen(req)
        return json.loads(resp.read())

if __name__ == '__main__':
    api = CMessagesAPI("dp76QL927CwxV8483e", "A4dQ42b8rykuxn3ah38qMEB9ft26Lm7D")
    print(api.get_balance())
