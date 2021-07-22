
## arcface3 SDK
依赖虹软人脸识别SDK3.0 linux java

## java环境
依赖环境jdk
```
docker pull openjdk:11.0.11-jdk
```

## run.sh
```
java -jar bdca-visionsense-sense-arcface3-0.0.1-SNAPSHOT.jar --server.port=50000
```

## APIs

API接口http://localhost:50000/swagger-ui.html

1. [人脸识别接口](#人脸识别接口)

## 人脸识别接口
| id | 类型 | 接口                       | 说明                    |
| -- | ---- | -------------------------- | ----------------------- |
| 1. | GET | [/sense](#sense-get) | 状态  |
| 2. | POST | [/sense](#sense-post) | 感知 |
| 3. | PUT | [/sense](#sense-put) | 测试 |

### /sense GET
感知服务状态

#### 业务参数
无

#### 返回参数
* health: true/false

#### 返回示例
```
{
  "health": true
}
```

### /sense POST
感知服务

#### 业务参数
* bases64: 图像Base64字符串  
* boxes: （可选）自定义识别范围框  
* inclusive: （可选）自定义识别范围和识别结果目标的匹配是否要求全包含：false-相交关系（默认值）；true-包含关系；  
* options: （可选）其他自定义参数和值   

```
{
  "bases64": "bases64",
  "boxes": [
    {
      "bottom": 0,
      "left": 0,
      "right": 0,
      "top": 0
    }
  ],
  "inclusive": false,
  "options": {}
}
```

#### 返回参数
* bases64: 图像Base64字符串
* result: 识别结果参数和值

#### 返回示例
```
{
  "bases64": "bases64",
  "result": {
    "size": 2
  }
}
```

### /sense PUT
感知测试，可直接上传图像文件，不需要设置图像base64

#### 业务参数
* image: 图像文件
* bases64: 图像Base64字符串
* boxes: （可选）自定义识别范围框
* inclusive: （可选）自定义识别范围和识别结果目标的匹配是否要求全包含：false-相交关系（默认值）；true-包含关系；
* options: （可选）其他自定义参数和值

```
{
  "bases64": "bases64",
  "boxes": [
    {
      "bottom": 0,
      "left": 0,
      "right": 0,
      "top": 0
    }
  ],
  "inclusive": false,
  "options": {}
}
```

#### 返回参数
* bases64: 图像Base64字符串
* result: 识别结果参数和值

#### 返回示例
```
{
  "bases64": "bases64",
  "result": {
    "size": 2
  }
}
```