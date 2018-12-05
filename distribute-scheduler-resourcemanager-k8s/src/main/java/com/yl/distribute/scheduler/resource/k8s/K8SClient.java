package com.yl.distribute.scheduler.resource.k8s;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;

public class K8SClient {
	
	private static final K8SClient K8SClient = new K8SClient();
	
	public static KubernetesClient createClient() throws Exception {
	    KubernetesClient client;
	    try {
			String host = "localhost";
			Integer port = 8443;
			ClassLoader classLoader = K8SClient.getClass().getClassLoader();
			File ca = new File(classLoader.getResource("ca.crt").getFile());
			File clientcert = new File(classLoader.getResource("client.crt").getFile());
			File clientkey = new File(classLoader.getResource("client.key").getFile());
			  
			Config kubeConfig = new ConfigBuilder()				 
			      .withMasterUrl(host + ":" +port)
			      .withCaCertFile(ca.getAbsolutePath())
			      .withClientCertFile(clientcert.getAbsolutePath())
			      .withClientKeyFile(clientkey.getAbsolutePath())
			      .build();	
			  client = new DefaultKubernetesClient(kubeConfig);
		  } catch (Exception e) {
			  throw e;
          }
	      return client;
   }
   
   public static Pod createPod(KubernetesClient client) {
	   
	   ResourceRequirements resources = new ResourceRequirements();
	   Map<String,Quantity> limit = new HashMap<String,Quantity>();
	   Map<String,Quantity> requests = new HashMap<String,Quantity>();
	   
	   limit.put("cpu", new Quantity("1"));
	   limit.put("memory", new Quantity("512mi"));
	   
	   requests.put("cpu", new Quantity("1"));
	   requests.put("memory", new Quantity("64mi"));
	   
	   resources.setLimits(limit);
	   resources.setRequests(requests);
       Pod pod = client.pods().inNamespace("scheduler").createNew()
    	          .withNewMetadata()
    	          .withName("schedulerpod")    	          
    	          .addToLabels("server", "scheduler")
    	          .endMetadata()
    	          .withNewSpec()
    	          .addNewContainer().withName("executor").withImage("executor")
    	          .withCommand("pwd")
    	          .withResources(resources)
    	          .addNewPort().withContainerPort(80).endPort()
    	          .endContainer()
    	          .endSpec().done();
       return pod;
   }
}