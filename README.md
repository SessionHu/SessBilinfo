# SessBilinfo
- 使用 Java 通过 Bilibili API 获取信息

## 使用

- 依照下方帮助运行 (*SessBilinfo.jar*为你下载的文件)
  ```text
  用法
      java -jar "SessBilinfo.jar"
  命令参数
      -a, --force-ansi 强制使用 JANSI 库
      -d, --debug      启用 DEBUG 输出
      -n, --nocookie   删除 Cookie 文件后运行程序
  环境变量
      OPEN_BILI_DEBUG  当值为 "true" 时, 与 '-d' 或 '-debug' 相同
  帮助及信息
      -h, --help       输出本帮助信息
      -v, --version    输出版本和其她信息
  ```
- `~/.openbili/` 下存储了本项目的本地文件, 如遇 Bug 可在 Issue 中上传 `~/.openbili/logs/` 中的日志

## 开发
- 本项目为使用 `Java 8` 的**控制台**应用程序, 请确认设备使用的 JDK 版本
- 请遵守开发所[依赖项目](NOTES.md#依赖项)的许可
## 许可
- 本项目使用 `MIT License` 进行分发使用
- 关于本项目的[参照说明](NOTES.md#参照项)
