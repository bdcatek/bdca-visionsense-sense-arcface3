
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

## SenseDTO
实体类[SenseDTO](src/main/java/com/bdca/sense/entity/SenseDTO.java)定义了接口输入和输出参数

## 感知接口APIs

API接口http://localhost:50000/swagger-ui.html

1. [人脸检测](#人脸检测接口)

## 人脸检测接口
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
* health: 状态是否正常，true/false
* msg: （可选）自定义提示信息

#### 返回示例
```
{
  "health": false,
  "msg": "初始化失败！"
}
```

### /sense POST
感知服务

#### 业务参数[SenseDTO](src/main/java/com/bdca/sense/entity/SenseDTO.java)
* bases64: 图像Base64字符串
* boxes: （可选）自定义识别范围框
* inclusive: （可选）自定义识别范围和识别结果目标的匹配是否要求全包含：false-相交关系（默认值）；true-包含关系；
* options: （可选）其他自定义参数和值;

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
  "options": {
    "mode": "VIDEO"
  }
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
* options: （可选）其他自定义参数和值;

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
  "options": {
    "mode": "VIDEO"
  }
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
