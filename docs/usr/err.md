## 错误

### 退出代码

- 0 正常退出
  <details>
    <summary>
      示例
    </summary>
    <p>
    
    ```log
    [WARN] 无效的操作编号
    ```
    
    </p>
  </details>

- 1 错误的输入
  <details>
    <summary>
      示例
    </summary>
    <p>
    
    ```log
    [FATAL] 无效的 Mid
    ```
    
    </p>
  </details>

- 64 网络错误
  <details>
    <summary>
      示例
    </summary>
    <p>
    
    ```log
    [FATAL] 域名解析失败, 请检查网络连接与hosts文件配置
    ```
    
    ```log
    [FATAL] SSL 握手失败, 请检查网络连接是否稳定
    ```
    
    ```log
    [FATAL] HTTP 请求发生未知错误
    [FATAL] XXXXXXXX: XXXXX
    [FATAL]   at XXXXXX(XXXX:XX)
    [FATAL]   ...
    ```
    
    </p>
  </details>
