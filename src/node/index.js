const crypto = require("crypto");
const https = require("https");

const MESSAGE_STATUS = {
  0: "SUCCESS",
  1: "PENDING",
  2: "SENDING",
  3: "FAILED",
};

class CMessagesAPI {
  /**
   *
   * @param {string} appId
   * @param {string} appKey
   */
  constructor(appId, appKey) {
    this.appId = appId;
    this.appKey = appKey;
  }

  /**
   * 获取当前北京时间
   */
  get now() {
    // 北京时间时区偏移量(process.env.TZ='Asia/Shanghai', new Date().getTimeZoneOffset())
    const tzOffset = -480 * 60000;
    return new Date(Date.now() - tzOffset)
      .toISOString()
      .slice(0, 19)
      .replace(/\D/g, "");
  }

  /**
   *
   * @param {Object} payload
   * @returns
   */
  preparePayload(payload = {}) {
    const ts = this.now;
    const sign = crypto
      .createHash("md5")
      .update(`${this.appId}${ts}${this.appKey}`)
      .digest("hex");
    return {
      ...payload,
      app_id: this.appId,
      datetime: ts,
      sign: sign,
    };
  }

  /**
   *
   * @param {string} method 请求方法, GET / POST
   * @param {string} endPoint 请求地址
   * @param {Object} payload 请求内容
   * @param {integer} timeout 毫秒，默认60秒
   *
   * @returns {Object}
   */
  httpRequest(method, endPoint, payload = null, timeout = 0) {
    const option = {
      method: method.toUpperCase(),
      host: "api.cmessages.com",
      port: 443,
      path: endPoint,
      headers: {
        "Content-Type": "application/json",
        "User-Agent": "CMessagesSDK-node",
      },
    };
    timeout = timeout || 600000;
    const postData = JSON.stringify(this.preparePayload(payload || {}));
    // console.debug("Send POST data: ", postData);
    return new Promise((resolve, reject) => {
      const req = https.request(option, (res) => {
        if (res.statusCode < 200 || res.statusCode >= 500) {
          return reject(
            new Error(`服务器返回状态异常:Status Code: ${res.statusCode}`)
          );
        }
        const chunks = [];
        res.on("data", (chunk) => chunks.push(chunk));
        res.on("end", () =>
          resolve(JSON.parse(Buffer.concat(chunks).toString()))
        );
      });
      req.on("socket", (socket) => {
        socket.setTimeout(timeout);
        socket.on("timeout", () => {
          req.destroy();
          return reject(new Error(`请求超时,timeout=${timeout}`));
        });
      });
      req.on("error", reject);
      req.write(postData);
      req.end();
    });
  }

  /**
   * 获取短信余额
   * @returns
   */
  getBalance = () => this.httpRequest("POST", "/sms/get_balance");

  /**
   * 发送验证码,查询接口可使用query接口查询
   *
   * @param {string} mobile 手机号(手机号可携带国标代码,比如+86视为中国)
   * @param {string} content 短信验证码内容,比如: 您的验证码为1234
   *
   * @see query
   */
  sendVerify = (mobile, content) =>
    this.httpRequest("POST", "/sms/send_verify", { mobile, content });

  /**
   * 发送营销短信
   *
   * @param {Array | string} mobiles 手机号列表,单个手机号时可给字符串(手机号可携带国标代码,比如+86视为中国)
   * @param {string} content 需要发送的营销内容
   *
   * @see query
   */
  sendMessage = (mobiles, content) =>
    this.httpRequest("POST", "/sms/send_message", {
      mobile: Array.isArray(mobiles) ? mobiles : [mobiles],
      content,
    });

  /**
   * 短信查询
   * @param {Array} ids 短信ID列表, 此ID来自sendVerify和sendMessage
   *
   * @see sendMessage
   * @see sendVerify
   *
   */
  query = (ids) => this.httpRequest("POST", "/sms/query", { id_list: ids });
}

///// 以下是测试代码
if (typeof require !== "undefined" && require.main === module) {
  const api = new CMessagesAPI(
    "dp76QL927CwxV8483e",
    "A4dQ42b8rykuxn3ah38qMEB9ft26Lm7D"
  );

  api
    .getBalance()
    .then((balance) => {
      console.log(`Got balance:`, balance);
      return api.sendVerify("+13169742594", "您的验证码为1234");
    })
    .then((resp) => {
      console.log("Send result:", resp);
      return [resp.id];
    })
    .then((resp) => {
      console.log("query:", resp);
      return api.query(resp);
    })
    .then(console.log);
}
