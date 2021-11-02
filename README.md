# cmessages

examples for email/sms/whatsapp api

本样例演示了不同语言如何快速简单的对接 `CMessages API`，（将相关 `API` 进行简单封装之后无需考虑签名逻辑)

该短信 `API` 目前提供了非常精简的 4 个 `API` :

- `sms/get_balance`: 余额查询接口
- `sms/send_verify`: 验证码发送
- `sms/send_message`: 营销短信发送
- `sms/query`: 查询接口

具体的 API 文档已作为附件放入 [docs](./docs) 目录，您可自行查阅并实现。

# 签名算法

`app_id` + 时间(参数`datetime`) + `app_key` 生成小写的 `MD5` 字符串作为签名。因此每一个 `API` 都会有公共的参数:

```json
{
    "app_id": "xxx",
    "datetime": 20211102145107,
    "app_key": "xxxx",
    "sign": md5(`app_id` + 时间(参数`datetime`) + `app_key` )
}
```

> 需要注意的是此处的 `datetime` 必须为北京时间。因此请将时区设置为 `Asia/ShangHai`

# 支持的编程语言:

- Python
  - Python2.7 & Python3 (时区数据暂无，若出现签名异常，请检查时区是否设置为北京时间 `Asia/Shanghai`)
- PHP
  - PHP 5.6 & PHP 7.x (依赖 `curl` 扩展)
- Node
  - Node v16.3.x
- Java
  - OpenJDK 16.0,相关依赖见 `pom.xml`

除了 Java 外，均使用单一文件，您可以自由拷贝至自己项目使用。
