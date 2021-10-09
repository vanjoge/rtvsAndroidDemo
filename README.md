# rtvsAndroidDemo
## 演示在Android上如何调用RTVS实现1078播放的demo

### 主要流程如下

1. 写一个静态页，调用调用rtvs.js的hls模式播放，同时在onHlsPlay事件中返回true阻止rtvs.js本身的播放；
2. Android端加入一个隐藏的Webview加载这个静态页；
3. 调用js方法开启实时视频，等待onHlsPlay事件返回rtmp地址；
4. 拿到rtmp地址后直接用ijkplayer播放rtmp流。


### IOS可用同样的方式实现
