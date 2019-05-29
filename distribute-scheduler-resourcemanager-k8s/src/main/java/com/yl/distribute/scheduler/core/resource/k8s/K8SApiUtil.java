package com.yl.distribute.scheduler.core.resource.k8s;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;

public class K8SApiUtil  {
	String hostApiUrl = "http://10.10.10.10:8081/";
	String namespace = "default";
	Config config = new ConfigBuilder().withMasterUrl(hostApiUrl).build();
	KubernetesClient client = new DefaultKubernetesClient(config); 
	public static void main(String[] args) {
		K8SApiUtil k = new K8SApiUtil();
		String podName = "ultra-basic";
		String templateName = "F:\\mywork\\workspace\\work2018\\kubenernetes-client-java\\src\\main\\resources\\ultra-basic-yaml.template";
		String basicImageName = "ultra/basic:v1.0";
		Map<String,Object> map = k.createPod(templateName,podName,basicImageName,"/docker/data","1000","2000","basic_data");
		System.out.println(map.get("status"));
		System.out.println(map.get("message"));
		System.out.println(map.get("podName"));
		System.out.println(map.get("podNameOriginal"));
		System.out.println(map.get("podIp"));
		
		String podName1 = map.get("podName").toString();
		System.out.println(k.getPodIpInfo(podName1).getStatus().getPodIP());
	}
	
	public K8SApiUtil() {}
	
	/**
	 * @param namespace 默认空间:default
	 * @param hostApiUrl 默认api服务器地址,如:http://10.10.10.10:8081
	 */
	public K8SApiUtil(String namespace,String hostApiUrl){
		this.namespace = namespace;
		this.hostApiUrl = hostApiUrl;
	}
	/**
	 * @param hostApiUrl 默认api服务器地址,如:http://10.10.10.10:8081
	 */
	public K8SApiUtil(String hostApiUrl){
		this.hostApiUrl = hostApiUrl;
	}
	/**
	 * @return 取得当前 namespace列表
	 */
	public List<Namespace> getNameSpace(){
		List<Namespace> nameSpaceList =client.namespaces().list().getItems();
		return nameSpaceList;
	}
	
	/**
	 * @return 取得当前 namespace下pod列表
	 * @see Pod
	 */
	public List<Pod> getPodList(){
		List<Pod>  podList = client.pods().inNamespace(namespace).list().getItems();
		return podList;
	}
	/**
	 * @param podName pod名称
	 * @param namespace 命名空间,默认default
	 * @示例 	  获取podIp:pod.getStatus().getPodIP(); 
	 * @return Pod 
	 * @see Pod
	 */
	public Pod getPodIpInfo(String podName) {
		Pod pod = new Pod();
		pod = client.pods().inNamespace(namespace).withName(podName).get();
		return pod;
	}
	
	/**
	 * @param podName 要删除的pod名称
	 * @return map对象,status,message,podName,podIp
	 * @说明 	 map.get("status"),status 为1时为成功,为-1时为失改
	 * @说明  message 操作成功或失败信息
	 * @说明  podName 删除的pod名称
	 * @说明  podIp 删除的podIP地址
	 */
	public Map<String,Object> delPodInfo(String podName) {
		Map<String,Object> resultMap = new HashMap<>();
		Pod pod = new Pod();
	    try {
	        //获取要删除的pod
	        pod = client.pods().inNamespace(namespace).withName(podName).get();
	        //Pod 删除
	        client.pods().inNamespace(namespace).withName(podName).delete();
	        resultMap.put("status", 1);
			resultMap.put("message", "删除pod成功");
			resultMap.put("podName", pod.getMetadata().getName());
			resultMap.put("podIp",pod.getStatus().getPodIP());
	    }catch (Exception e){
	    	resultMap.put("status", -1);
			resultMap.put("message", "删除pod失败:"+e.getMessage());
	    }
	    return resultMap;
	}
	
	/**
	 * @param templatePathName 创建模板的名称,全路径
	 * @param podName	要创建的pod名称
	 * @param basicImageName 创建此pod需要依赖的父镜像
	 * @param shareDir 共享目录
	 * @param userId	用户唯一ID
	 * @param courseId  课程ID
	 * @param bigDataBasicDir 此目录为大数据或其他课程提供基础数据目录,默认值::basic_data
	 * @return  map 信息,信息如下
	 * @说明 	 map.get("status"),status 为1时为成功,为-1时为失改
	 * @说明  message 操作成功或失败信息
	 * @说明  podName 创建的pod名称_成功后的名称
	 * @说明  podNameOriginal 创建的pod名称_原始传入的名称
	 * @说明  podIp 创建成功后的podIp信息,创建后的pod并不会立即取得其ip信息,可休息15 秒左右在次来获取
	 */
	public Map<String,Object> createPod(String templatePathName,String podName,String basicImageName,
				String shareDir,String userId,String courseId,String bigDataBasicDir) {
		Map<String,Object> resultMap = new HashMap<>();
		String suffix_10 = getRandomString(10).toLowerCase();
		String suffix_5 = getRandomString(5).toLowerCase();
		String podNameNew = podName + "-" + suffix_10 + "-" + suffix_5;
		try {
			Map<String,Object> templateMap = getTemplateContent(templatePathName);
			if(templateMap.get("status").equals(-1)) {
				resultMap.put("status", -1);
				resultMap.put("message","创建Pod失败,错误信息为:"+templateMap.get("message"));
			}
			String podStr = templateMap.get("result").toString();
			// 完整的pod名称
			String podNameFix = podName;
			String generateName = podName + "-" + suffix_10 + "-";
			String ownerReferences_name =  podName + "-" + suffix_10;
			String userIdToCodePath = userId + "/" + courseId;
			if(bigDataBasicDir == null || "".equals(bigDataBasicDir)) {
				bigDataBasicDir = "basic_data";
			}
			podStr = podStr.replace("${podName}", podNameNew)
					.replace("${nameSpace}", namespace)
					.replace("${basicImageName}", basicImageName)
					.replace("${generateName}",generateName)
					.replace("${podNameFix}",podNameFix)
					.replace("${ownerReferences_name}",ownerReferences_name)
					.replace("${nfsDir}",shareDir)
					.replace("${userIdToCodePath}",userIdToCodePath)
					.replace("${bigDataBasicDir}",bigDataBasicDir);
			return createPod(podStr,podName);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", -1);
			resultMap.put("podNameOriginal", podNameNew);
			resultMap.put("message", "创建pod失败:"+e.getMessage());
			return resultMap;
		}
	}
	private Map<String,Object> createPod(String podContent,String podName) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			List<HasMetadata> resources = null;
			// resources = client.load(new FileInputStream(fileName)).get();
			resources = client.resourceList(podContent).get();
			if (resources.isEmpty()) {
				resultMap.put("status", -1);
				resultMap.put("message", "pod资源文件为空,创建pod失败");
				resultMap.put("podNameOriginal", podName);
			}
			HasMetadata resource = resources.get(0);
			if (resource instanceof Pod) {
				Pod pod = (Pod) resource;
				NonNamespaceOperation<Pod, PodList, DoneablePod, PodResource<Pod, DoneablePod>> pods = client.pods().inNamespace(namespace);
				Pod result = pods.create(pod);
				resultMap.put("status", 1);
				resultMap.put("message", "创建pod成功");
				resultMap.put("podName",  result.getMetadata().getName());
				resultMap.put("podIp", result.getStatus().getPodIP());
				resultMap.put("podNameOriginal", podName);

			} else {
				resultMap.put("status", -1);
				resultMap.put("podNameOriginal", podName);
				resultMap.put("message", "pod资源文件不包含pod信息");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("status", -1);
			resultMap.put("podNameOriginal", podName);
			resultMap.put("message", "创建pod失败:"+e.getMessage());
		}
		return resultMap;
	}
	
	private static String getRandomString(int length){
	    //定义一个字符串（A-Z，a-z，0-9）即62位；
	    String str="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
	    //由Random生成随机数
	        Random random=new Random();  
	        StringBuffer sb=new StringBuffer();
	        //长度为几就循环几次
	        for(int i=0; i<length; ++i){
	          //产生0-61的数字
	          int number=random.nextInt(62);
	          //将产生的数字通过length次承载到sb中
	          sb.append(str.charAt(number));
	        }
	        //将承载的字符转换成字符串
	        return sb.toString();
	  }
	private Map<String,Object> getTemplateContent(String filePathAndFileName) {
		StringBuffer templateStr = new StringBuffer();
		Map<String,Object> resultMap = new HashMap<>();
		BufferedReader reader = null;
		try {
			File file = new File(filePathAndFileName);
			if(!file.isFile()) {
				resultMap.put("status", -1);
				resultMap.put("message", "模板文件不存在");
			}
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				templateStr.append(tempString + System.getProperty("line.separator"));
			}
			reader.close();
		} catch (IOException e) {
			resultMap.put("status", -1);
			resultMap.put("message", e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		resultMap.put("status", 1);
		resultMap.put("message","读取模板文件成功");
		resultMap.put("result",templateStr.toString());
		return resultMap;
	}
}
