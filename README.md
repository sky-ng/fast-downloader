# fast-downloader

### 主要功效

- 支持1MB以内文件单线程下载
- 支持1MB以上文件多线程下载
- 支持断点续传，妈妈再也不用担心下载了半天的文件断网全没啦

### 使用方法

```shell
git clone git@github.com:sky-ng/fast-downloader.git
cd fast-downloader/
mvn package
cd target/
java -jar fast-downloader-1.0-SNAPSHOT.jar url
```