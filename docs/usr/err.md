# 错误

## 退出代码

- 0 正常退出
  <detail>
    <summary>
      示例
    </summary>
    <p>
    
    ```
    [WARN] 无效的操作编号
    ```
    
    </p>
  </detail>

- 1 错误的输入
  <detail>
    <summary>
      示例
    </summary>
    <p>
    
    ```
    [FATAL] 无效的 Mid
    ```
    
    </p>
  </detail>

- 64 网络错误
  <detail>
    <summary>
      示例
    </summary>
    <p>
    
    ```
    [FATAL] 域名解析失败, 请检查网络连接与hosts文件配置
    ```
    
    ```
    [FATAL] SSL 握手失败, 请检查网络连接是否稳定
    ```
    
    ```
    [FATAL] HTTP 请求发生未知错误
    [FATAL] XXXXXXXX: XXXXX
    [FATAL]   at XXXXXX(XXXX:XX)
    [FATAL]   ...
    ```
    
    </p>
  </detail>
