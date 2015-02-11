#ueditor-extend
----------
####对百度UEditor编辑器做扩展(Java)，开放文件存储方法和获取远程文件列表方法，使其更灵活，更容易和独立的文件服务或者文件存储的云服务结合。
----------

## 项目包含如下两部分： ##
1. **ueditor-extend-code** 扩展的源代码
1. **ueditor-extend-demo** 基于该扩展的小示例 (里面包含：Fastdfs的实现、七牛云存储的实现)

> - UeditorServiceFastdfsImpl是Fastdfs的实现。
> - Fastdfs相关配置项：
> - fdfs_client.conf (Fastdfs配置文件)
> - applicationContext.properties中配置文件系统中文件的访问根路径
> - dfsFileAccessBasePath=http://127.0.0.1

###

> - UeditorServiceQiniuImpl是七牛云存储的实现。
> - QiniuUtil中修改七牛云存储的相关参数

#使用方法如下：
#####1、项目中加入ueditor-extend-core源码，或者引入编译后的jar。
#####2、删除ueditor/jsp/controller.jsp文件，重新定义服务器统一请求接口路径。
> 我项目中使用的是Spring MVC，定义的入口是：ueditor/execute，所以需要修改ueditor.config.js中的serverUrl参数值为：URL + "/execute"。

#####3、移动ueditor/jsp/config.json文件至ueditor目录下，并删除jsp目录。保证入口地址和config.json处于同级。
#####4、实现net.viservice.editor.ueditor.UeditorService接口，完成获取表单中的文件、保存文件至文件服务器或通过云存储服务上传文件、远程文件访问。
#####5、在入口方法(ueditor/execute)中加入如下代码：
> - String rootPath = request.getServletContext().getRealPath("/");
> - String resultMsg = new UeditorActionEnter(request, rootPath, this.ueditoreService).exec();
> - 并返回resultMsg即可。

#####6、如果开启了UEditor编辑器的抓取远程图片功能（默认开启）的话，
> 记得修改config.json中的这项配置：
> /* 抓取远程图片配置 */
> "catcherLocalDomain": ["127.0.0.1", "localhost", "img.baidu.com", "你程序的IP/域名"],

###附：步骤如上所述，有描述不清楚的，还是直接参考ueditor-extend-demo中的代码吧。
