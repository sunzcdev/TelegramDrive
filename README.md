# TelegramDrive

这是一个利用Telegram可以永久保存文件的特性，而实现的Android Telegram 云盘。

使用的api有[tdapi](https://github.com/tdlib/td),[FileBrowserView](https://github.com/psaravan/FileBrowserView)

利用telegram消息中可以附带的caption作为对文件增删改查的标识，实现文件管理。

目前存在问题是，
1. telegram上传后不能立刻查询出来
2. 自动同步本地文件的功能仍未实现

新的想法：
1. 可以做一个云端相册管理工具
