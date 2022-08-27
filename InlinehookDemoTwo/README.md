## [Inline hook 的原理](https://www.sunmoonblog.com/2019/07/15/inline-hook-basic/)
#### inline hook 由3部分组成：
* Hook - 为了 hook 目标函数(旧函数)，会向其代码中写入一个5个字节的跳转指令(实际跳转指令以及指令大小跟平台相关)
* Proxy - 用于指定被 hook 的目标函数将要跳转到的函数(新函数)
* Trampoline - 用于调用旧函数
  * hook 成功后，如何调用原先的旧函数
  * 无限递归问题

